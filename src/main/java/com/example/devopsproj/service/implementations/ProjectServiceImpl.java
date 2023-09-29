package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.model.*;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final GitRepositoryRepository gitRepositoryRepository;
    private final ModelMapper modelMapper;
    private final GitHubCollaboratorServiceImpl collaboratorService;
    private final CollaboratorDTO collaboratorDTO;


    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());
        projectRepository.save(project);

        return modelMapper.map(project, ProjectDTO.class);
    }

    @Override
    public Optional<Project> getProjectById(Long id){
        return projectRepository.findById(id);
    }

    @Override
    public ResponseEntity<Object> getProject(Long id) {
        try {
            Optional<Project> checkProject = projectRepository.findById(id);

            if (checkProject.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Project project = checkProject.get();

            if (Boolean.TRUE.equals(project.getDeleted())) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(project.getProjectId());
            projectDTO.setProjectName(project.getProjectName());
            projectDTO.setProjectDescription(project.getProjectDescription());

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ProjectWithUsersDTO> getAllProjectsWithUsers() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectWithUsersDTO> projectsWithUsers = new ArrayList<>();

        for (Project project : projects) {
            List<User> userList = projectRepository.findAllUsersByProjectId(project.getProjectId());
            List<UserDTO> userDTOList = userList.stream()
                    .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                    .toList();

            ProjectWithUsersDTO projectWithUsers = new ProjectWithUsersDTO(
                    project.getProjectId(),
                    project.getProjectName(),
                    project.getProjectDescription(),
                    project.getLastUpdated(),
                    userDTOList
            );

            projectsWithUsers.add(projectWithUsers);
        }

        return projectsWithUsers;
    }

    public ResponseEntity<Object> addRepositoryToProject(Long projectId, Long repoId){
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<GitRepository> optionalGitRepository = gitRepositoryRepository.findById(repoId);
        if (optionalProject.isPresent() && optionalGitRepository.isPresent()) {
            Project project = optionalProject.get();
            GitRepository gitRepository = optionalGitRepository.get();

            // Check if the project has been deleted
            if (Boolean.FALSE.equals(project.getDeleted())) {
                gitRepository.setProject(project);
            } else {
                gitRepository.setProject(null);
            }
            gitRepositoryRepository.save(gitRepository);
            return ResponseEntity.ok("Stored successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public List<Project> getAll(){
        return projectRepository.findAll();
    }

    @Override
    public List<Project> getAllProjects(){
        return projectRepository.findAllProjects();
    }

    @Override
    public Project updateProject(Project updatedProject){
        return projectRepository.save(updatedProject);
    }

    //get all users in the project based on project id
    @Override
    public List<UserDTO> getAllUsersByProjectId(Long projectId) {
        List<User> users = projectRepository.findAllUsersByProjectId(projectId);

        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
    }

    @Override
    public List<User> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role){
        List<User> users = projectRepository.findAllUsersByProjectIdAndRole(projectId, role);
        if(users.isEmpty()){
            return Collections.emptyList();
        }
        else {
            return users;
        }
    }

    //to check if the project has already been deleted
    @Override
    public boolean existsByIdIsDeleted(Long id){
        Optional<Project> checkProject = projectRepository.findById(id);
        if (checkProject.isEmpty()){
            return true;
        }
        Project cproject = checkProject.get();
        return cproject.getDeleted(); //true if deleted=1, else false
    }

    //soft deleting project
    @Override
    public boolean softDeleteProject(Long id){
        try{
            projectRepository.softDeleteProject(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean existsProjectById(Long id){
        return projectRepository.existsById(id);
    }

    @Override
    public boolean existUserInProject(Long projectId, Long userId){
        List<User> userList = projectRepository.existUserInProject(projectId, userId);
        // False if user is present in project
        return !userList.isEmpty();
    }

    @Override
    public Integer getCountAllProjects(){
        return projectRepository.countAllProjects();
    }

    @Override
    public Integer getCountAllProjectsByRole(EnumRole enumRole) {
        return projectRepository.countAllProjectsByRole(enumRole);
    }

    @Override
    public Integer getCountAllProjectsByUserId(Long id) {
        return projectRepository.countAllProjectsByUserId(id);
    }

    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        return projectRepository.countAllUsersByProjectId(projectId);
    }

    @Override
    public List<ProjectNamePeopleCountDTO> getCountAllPeopleAndProjectName() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectNamePeopleCountDTO> peopleCountDTOS = new ArrayList<>();
        for (Project project : projects){
            ProjectNamePeopleCountDTO peopleCountDTO = new ProjectNamePeopleCountDTO();

            Integer count = projectRepository.countAllUsersByProjectId(project.getProjectId());

            peopleCountDTO.setProjectId(project.getProjectId());
            peopleCountDTO.setProjectName(project.getProjectName());
            peopleCountDTO.setCountPeople(count);
            peopleCountDTOS.add(peopleCountDTO);
        }
        return peopleCountDTOS;
    }

    @Override
    public Integer getCountAllUsersByProjectIdAndRole(Long projectId, EnumRole enumRole) {
        return projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole);
    }

    @Override
    public Integer getCountAllActiveProjects(){
        return projectRepository.countAllActiveProjects();
    }

    @Override
    public Integer getCountAllInActiveProjects(){
        return projectRepository.countAllInActiveProjects();
    }

    @Override
    public List<UserDTO> getUsersByProjectIdAndRole(Long projectId, String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        List<User> users = projectRepository.findUsersByProjectIdAndRole(projectId, userRole);
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
    }

    @Override
    public List<ProjectDTO> getProjectsWithoutFigmaURL() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();

        for (Project project : projects) {
            // Check if the project has a Figma URL set
            if (project.getFigma() == null || project.getFigma().getFigmaURL() == null) {
                // Create a ProjectDTO and add it to the list
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        return projectDTOs;
    }

    @Override
    public List<ProjectDTO> getProjectsWithoutGoogleDriveLink() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();

        for (Project project : projects) {
            if (project.getGoogleDrive() == null || project.getGoogleDrive().getDriveLink() == null) {
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        return projectDTOs;
    }

    @Override
    public ProjectDTO getProjectDetailsById(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            // Retrieve the required details from the project object
            String projectName = project.getProjectName();
            String projectDescription = project.getProjectDescription();
            boolean status = project.getDeleted();

            List<User> users = project.getUsers();
            String pmName = null;
            for (User user : users){
                if(user.getEnumRole()==EnumRole.PROJECT_MANAGER){
                    pmName = user.getName();
                    break;
                }
            }

            List<GitRepository> repositories = project.getRepositories();
            List<GitRepositoryDTO> repositoryDTOS = new ArrayList<>();
            for(GitRepository repository : repositories){
                GitRepositoryDTO repositoryDTO = new GitRepositoryDTO();
                repositoryDTO.setName(repository.getName());
                repositoryDTO.setDescription(repository.getDescription());
                repositoryDTOS.add(repositoryDTO);
            }

            Figma figma = project.getFigma();
            String figmaURL = figma != null ? figma.getFigmaURL() : null; // Retrieve the Figma URL
            FigmaDTO figmaDTO = new FigmaDTO(figmaURL);

            GoogleDrive googleDrive = project.getGoogleDrive();
            String driveLink = googleDrive != null ? googleDrive.getDriveLink() : null; // Retrieve the driveLink

            LocalDateTime lastUpdated = project.getLastUpdated();

            // Create and return a ProjectDTO object with the required details
            return new ProjectDTO(
                    projectName,
                    projectDescription,
                    status,
                    pmName,
                    repositoryDTOS,
                    figmaDTO,
                    new GoogleDriveDTO(driveLink),
                    lastUpdated
            );
        } else {
            return new ProjectDTO();
        }
    }

    // Add User to Project by User ID and Project ID
    @Override
    public ResponseEntity<Object> addUserToProjectByUserIdAndProjectId(Long projectId, Long userId){
        try {
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();
                if (existUserInProject(project.getProjectId(), user.getId())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                project.getUsers().add(user);
                projectRepository.save(project);
                List<UserDTO> userDTOList = project.getUsers().stream()
                        .map(users -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                        .collect(Collectors.toList());
                ProjectUserDTO projectUserDTO = new ProjectUserDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), userDTOList);
                return new ResponseEntity<>(projectUserDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Remove User from Project based on User ID and Project ID
    @Override
    public ResponseEntity<String> removeUserFromProjectByUserIdAndProjectId(Long projectId, Long userId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalProject.isPresent() && optionalUser.isPresent()) {
            Project project = optionalProject.get();
            User user = optionalUser.get();

            if (!project.getUsers().contains(user)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not part of the project");
            }

            project.getUsers().remove(user);
            projectRepository.save(project);

            return ResponseEntity.ok("User removed");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
        }
    }

    // Remove User from Project and Repo based on User ID and Project ID
    @Override
    public ResponseEntity<String> removeUserFromProjectAndRepo(Long projectId, Long userId, CollaboratorDTO collaboratorDTO){
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalProject.isPresent() && optionalUser.isPresent()) {
            Project project = optionalProject.get();
            User user = optionalUser.get();
            project.getUsers().remove(user);
            projectRepository.save(project);

            boolean deleted = collaboratorService.deleteCollaborator(collaboratorDTO);
            if (!deleted) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok("User removed");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
        }
    }

    // ----- Other methods ------

    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

}
