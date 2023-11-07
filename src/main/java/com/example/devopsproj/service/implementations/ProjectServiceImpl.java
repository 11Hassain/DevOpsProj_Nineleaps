package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.exceptions.ProjectNotFoundException;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.dto.responsedto.ProjectDTO;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final GitRepositoryRepository gitRepositoryRepository;
    private final GitHubCollaboratorServiceImpl collaboratorService;


    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    // Create a project from a ProjectDTO and return the mapped DTO
    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        logger.info("Creating a new project: {}", projectDTO.getProjectName());

        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());

        // Save the project and return it as a mapped DTO
        projectRepository.save(project);

        logger.info("Created project: {}", projectDTO.getProjectName());

        return modelMapper.map(project, ProjectDTO.class);
    }


    // Get a project by its ID
    @Override
    public ProjectDTO getProjectById(Long id) {
        logger.info("Retrieving project with ID: {}", id);

        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            // Convert the Project to ProjectDTO and return it
            ProjectDTO projectDTO = mapProjectToProjectDTO(project);
            logger.info("Retrieved project: {}", projectDTO.getProjectName());
            return projectDTO;
        } else {
            // Handle the case where the project with the given ID is not found
            logger.error("Project not found with ID: {}", id);
            throw new ProjectNotFoundException("Project not found with ID: " + id);
        }
    }

    // Get a list of all projects
    @Override
    public List<ProjectDTO> getAll() {
        logger.info("Retrieving all projects");

        List<Project> projects = projectRepository.findAll();
        if (projects.isEmpty()) {
            logger.warn("No projects found");
            throw new NotFoundException("No projects found");
        }

        List<ProjectDTO> projectDTOs = projects.stream()
                .map(this::mapProjectToProjectDTO)
                .toList(); // Use Stream.toList() to collect into a list

        logger.info("Retrieved {} projects", projectDTOs.size());

        return projectDTOs;
    }

    // Get a list of all projects, including inactive ones
    @Override
    public List<ProjectWithUsersDTO> getAllProjectsWithUsers() {
        logger.info("Retrieving all projects with users");

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

        logger.info("Retrieved {} projects with users", projectsWithUsers.size());

        return projectsWithUsers;
    }

    @Override
    public List<Project> getAllProjects() {
        logger.info("Retrieving all projects");

        List<Project> projects = projectRepository.findAll();
        logger.info("Retrieved {} projects", projects.size());

        return projects;
    }

    // Update a project
    @Override
    public Project updateProject(Project updatedProject) {
        logger.info("Updating project with ID: {}", updatedProject.getProjectId());

        return projectRepository.save(updatedProject);
    }

    // Get all users associated with a project by its ID
    @Override
    public List<UserDTO> getAllUsersByProjectId(Long projectId) {
        logger.info("Retrieving all users for project with ID: {}", projectId);

        List<User> users = projectRepository.findAllUsersByProjectId(projectId);

        if (users.isEmpty()) {
            logger.warn("No users found for project with ID: {}", projectId);
            throw new NotFoundException("No users found for project with ID: " + projectId);
        }

        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList(); // Use Stream.toList() to collect into a list

        logger.info("Retrieved {} users for project with ID: {}", userDTOs.size(), projectId);

        return userDTOs;
    }

    // Get all users associated with a project by its ID and role
    @Override
    public List<UserDTO> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role) {
        logger.info("Retrieving all users for project with ID: {} and role: {}", projectId, role);

        List<User> users = projectRepository.findAllUsersByProjectIdAndRole(projectId, role);

        if (users.isEmpty()) {
            logger.warn("No users found for project with ID: {} and role: {}", projectId, role);
            throw new NotFoundException("No users found for project with ID: " + projectId + " and role: " + role);
        }

        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserNames usernames = user.getUserNames();
                    String username = (usernames != null) ? usernames.getUsername() : null;
                    return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), username);
                })
                .toList(); // Use Stream.toList() to collect into a list

        logger.info("Retrieved {} users for project with ID: {} and role: {}", userDTOs.size(), projectId, role);

        return userDTOs;
    }



    @Override
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {
        logger.info("Updating project with ID: {}", projectId);

        Optional<Project> optionalProject = projectRepository.findById(projectId);

        if (optionalProject.isPresent()) {
            Project existingProject = optionalProject.get();
            existingProject.setProjectName(projectDTO.getProjectName());
            existingProject.setProjectDescription(projectDTO.getProjectDescription());
            existingProject.setLastUpdated(LocalDateTime.now());

            Project updatedProject = projectRepository.save(existingProject);

            logger.info("Updated project with ID: {}", projectId);

            // Return the ProjectDTO directly
            return new ProjectDTO(updatedProject.getProjectId(), updatedProject.getProjectName(), updatedProject.getProjectDescription(), updatedProject.getLastUpdated());
        } else {
            logger.error(PROJECT_WITH_ID_NOTFOUND, projectId);
            throw new NotFoundException("Project with ID " + projectId + " not found");
        }
    }

    @Override
    public ResponseEntity<String> deleteProject(Long id) {
        logger.info("Deleting project with ID: {}", id);

        Optional<Project> optionalProject = projectRepository.findById(id);

        if (optionalProject.isPresent()) {
            Project existingProject = optionalProject.get();

            if (Boolean.TRUE.equals(existingProject.getDeleted())) {
                logger.info("Project with ID {} doesn't exist", id);
                return ResponseEntity.ok("Project doesn't exist");
            }

            existingProject.setDeleted(true);
            existingProject.setLastUpdated(LocalDateTime.now());
            projectRepository.save(existingProject);

            logger.info("Deleted project with ID: {}", id);

            return ResponseEntity.ok("Deleted project successfully");
        } else {
            logger.error(PROJECT_WITH_ID_NOTFOUND, id);
            throw new NotFoundException("Project with ID " + id + " not found");
        }
    }




    @Override
    public ResponseEntity<Object> addUserToProject(Long projectId, Long userId) {
        try {
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();

                if (existUserInProject(project.getProjectId(), user.getId())) {
                    logger.warn("User with ID {} already exists in project with ID {}", user.getId(), project.getProjectId());
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }

                project.getUsers().add(user);
                projectRepository.save(project);

                List<UserDTO> userDTOList = project.getUsers().stream()
                        .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getEnumRole()))
                        .toList(); // Use Stream.toList() to collect into a list

                ProjectUserDTO projectUserDTO = new ProjectUserDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), userDTOList);

                logger.info("User with ID {} added to project with ID {}", user.getId(), project.getProjectId());
                return new ResponseEntity<>(projectUserDTO, HttpStatus.OK);
            } else {
                if (!optionalProject.isPresent()) {
                    logger.warn(PROJECT_WITH_ID_NOTFOUND, projectId);
                }
                if (!optionalUser.isPresent()) {
                    logger.warn(USER_WITH_ID_NOTFOUND, userId);
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Internal server error occurred while adding user to project. Project ID: {}, User ID: {}", projectId, userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<String> removeUserFromProject(Long projectId, Long userId) {
        try {
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();

                if (!project.getUsers().contains(user)) {
                    logger.info("User with ID {} is not part of the project with ID {}", user.getId(), project.getProjectId());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not part of the project");
                }

                project.getUsers().remove(user);
                projectRepository.save(project);

                logger.info("User with ID {} removed from project with ID {}", user.getId(), project.getProjectId());
                return ResponseEntity.ok("User removed");
            } else {
                if (!optionalProject.isPresent()) {
                    logger.warn(PROJECT_WITH_ID_NOTFOUND, projectId);
                }
                if (!optionalUser.isPresent()) {
                    logger.warn(USER_WITH_ID_NOTFOUND, userId);
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
            }
        } catch (Exception e) {
            logger.error(UNABLE_TO_REMOVE_USER_MESSAGE, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UNABLE_TO_REMOVE_USER_MESSAGE);
        }
    }


    @Override
    public ResponseEntity<String> removeUserFromProjectAndRepo(Long projectId, Long userId, CollaboratorDTO collaboratorDTO) {
        try {
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();
                project.getUsers().remove(user);
                projectRepository.save(project);

                boolean deleted = collaboratorService.deleteCollaborator(collaboratorDTO);
                if (!deleted) {
                    logger.warn("Failed to delete collaborator for user with ID {}", user.getId());
                    return ResponseEntity.badRequest().build();
                }

                logger.info("User with ID {} removed from project with ID {}", user.getId(), project.getProjectId());
                return ResponseEntity.ok("User removed");
            } else {
                if (!optionalProject.isPresent()) {
                    logger.warn(USER_WITH_ID_NOTFOUND, projectId);
                }
                if (!optionalUser.isPresent()) {
                    logger.warn(USER_WITH_ID_NOTFOUND, userId);
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
            }
        } catch (Exception e) {
            logger.error("Failed to remove user from project and repo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UNABLE_TO_REMOVE_USER_MESSAGE);
        }
    }

    @Override
    public boolean existsByIdIsDeleted(Long id) {
        // Add appropriate logger message for this method
        logger.info("Checking if project with ID {} exists and is deleted", id);
        return false;
    }

    // Soft delete a project by setting its deleted flag to true
    @Override
    public boolean softDeleteProject(Long id) {
        try {
            projectRepository.softDeleteProject(id);
            logger.info("Project with ID {} soft-deleted", id);
            return true;
        } catch (Exception e) {
            logger.error("Failed to soft-delete project with ID {}", id, e);
            return false;
        }
    }

    // Check if a project with the given ID exists
    @Override
    public boolean existsProjectById(Long id) {
        // Add appropriate logger message for this method
        logger.info("Checking if project with ID {} exists", id);
        return projectRepository.existsById(id);
    }

    // Check if a user exists in a project by their IDs
    @Override
    public boolean existUserInProject(Long projectId, Long userId) {
        List<User> userList = projectRepository.existUserInProject(projectId, userId);
        return !userList.isEmpty();
    }

    // Get the count of all projects
    @Override
    public Integer getCountAllProjects() {
        Integer countProjects = projectRepository.countAllProjects();
        logger.info("Count of all projects: {}", countProjects);
        return countProjects != null ? countProjects : 0;
    }




    @Override
    public Integer getCountAllProjectsByRole(EnumRole enumRole) {
        Integer countProjects = projectRepository.countAllProjectsByRole(enumRole);
        logger.info("Count of all projects with role {}: {}", enumRole, countProjects);
        return countProjects != null ? countProjects : 0;
    }

    @Override
    public Integer getCountAllProjectsByUserId(Long id) {
        Integer countProjects = projectRepository.countAllProjectsByUserId(id);
        logger.info("Count of all projects for user with ID {}: {}", id, countProjects);
        return countProjects != null ? countProjects : 0;
    }

    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        Integer countUsers = projectRepository.countAllUsersByProjectId(projectId);
        logger.info("Count of all users associated with project ID {}: {}", projectId, countUsers);
        return countUsers != null ? countUsers : 0;
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

            logger.info("Project with ID {}: People count - {}", project.getProjectId(), count);
        }
        return peopleCountDTOS;
    }

    @Override
    public Integer getCountAllUsersByProjectIdAndRole(Long projectId, EnumRole enumRole) {
        Integer countUsers = projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole);
        logger.info("Count of all users associated with project ID {} and role {}: {}", projectId, enumRole, countUsers);
        return countUsers != null ? countUsers : 0;
    }

    @Override
    public Integer getCountAllActiveProjects() {
        Integer countProjects = projectRepository.countAllActiveProjects();
        logger.info("Count of all active projects: {}", countProjects);
        return countProjects != null ? countProjects : 0;
    }


    @Override
    public Integer getCountAllInActiveProjects() {
        Integer countProjects = projectRepository.countAllInActiveProjects();
        logger.info("Count of all inactive projects: {}", countProjects);
        return countProjects != null ? countProjects : 0;
    }

    @Override
    public List<UserDTO> getUsersByProjectIdAndRole(Long projectId, String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        List<User> users = projectRepository.findUsersByProjectIdAndRole(projectId, userRole);
        logger.info("Users associated with project ID {} and role {}: Count - {}", projectId, role, users.size());
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
    }

    @Override
    public ResponseEntity<Object> addRepositoryToProject(Long projectId, Long repoId) {
        try {
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<GitRepository> optionalGitRepository = gitRepositoryRepository.findById(repoId);
            if (optionalProject.isPresent() && optionalGitRepository.isPresent()) {
                Project project = optionalProject.get();
                GitRepository gitRepository = optionalGitRepository.get();

                if (Boolean.FALSE.equals(project.getDeleted())) {
                    gitRepository.setProject(project);
                } else {
                    gitRepository.setProject(null);
                }
                gitRepositoryRepository.save(gitRepository);

                logger.info("Repository added to project ID {}: Repository ID - {}", projectId, repoId);
                return ResponseEntity.ok("Stored successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error while adding repository to project ID {}: Repository ID - {}", projectId, repoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
        }
    }



    @Override
    public List<ProjectDTO> getProjectsWithoutFigmaURL() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();
        for (Project project : projects) {
            if (project.getFigma() == null || project.getFigma().getFigmaURL() == null) {
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        logger.info("Projects without Figma URL count: {}", projectDTOs.size());
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
        logger.info("Projects without Google Drive link count: {}", projectDTOs.size());
        return projectDTOs;
    }

    @Override
    public ProjectDTO getProjectDetailsById(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            logger.info("Project details retrieved for project ID: {}", projectId);
            return mapProjectToDTO(project);
        } else {
            logger.info("Project details not found for project ID: {}", projectId);
            return new ProjectDTO();
        }
    }



    private ProjectDTO mapProjectToDTO(Project project) {
        String projectName = project.getProjectName();
        String projectDescription = project.getProjectDescription();
        String pmName = findProjectManagerName(project);
        List<GitRepositoryDTO> repositoryDTOS = mapGitRepositoriesToDTOs(project.getRepositories());
        FigmaDTO figmaDTO = mapFigmaToDTO(project.getFigma());
        GoogleDriveDTO googleDriveDTO = mapGoogleDriveToDTO(project.getGoogleDrive());
        LocalDateTime lastUpdated = project.getLastUpdated();

        return new ProjectDTO(
                projectName,
                projectDescription,
                pmName,
                repositoryDTOS,
                figmaDTO,
                googleDriveDTO,
                lastUpdated
        );
    }

    private String findProjectManagerName(Project project) {
        List<User> users = project.getUsers();
        String pmName = null;
        for (User user : users) {
            if (user.getEnumRole() == EnumRole.PROJECT_MANAGER) {
                pmName = user.getName();
                break;
            }
        }
        return pmName;
    }

    private List<GitRepositoryDTO> mapGitRepositoriesToDTOs(List<GitRepository> repositories) {
        List<GitRepositoryDTO> repositoryDTOS = new ArrayList<>();
        for (GitRepository repository : repositories) {
            GitRepositoryDTO repositoryDTO = new GitRepositoryDTO();
            repositoryDTO.setName(repository.getName());
            repositoryDTO.setDescription(repository.getDescription());
            repositoryDTOS.add(repositoryDTO);
        }
        return repositoryDTOS;
    }

    private FigmaDTO mapFigmaToDTO(Figma figma) {
        String figmaURL = (figma != null) ? figma.getFigmaURL() : null;
        return new FigmaDTO(figmaURL);
    }

    private GoogleDriveDTO mapGoogleDriveToDTO(GoogleDrive googleDrive) {
        String driveLink = (googleDrive != null) ? googleDrive.getDriveLink() : null;
        return new GoogleDriveDTO(driveLink);
    }

    // Map a Project entity to a ProjectDTO
    @Override
    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

    @Override
    public Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        // Map other properties as needed
        return project;
    }
    private static final String UNABLE_TO_REMOVE_USER_MESSAGE = "Unable to remove user";
    private static final String PROJECT_WITH_ID_NOTFOUND ="Project with ID {} not found";
    private static final String USER_WITH_ID_NOTFOUND ="User with ID {} not found";

}