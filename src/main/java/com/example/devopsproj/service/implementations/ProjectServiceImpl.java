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
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final GitRepositoryRepository gitRepositoryRepository;
    private final GitHubCollaboratorServiceImpl collaboratorService;


    // Create a project from a ProjectDTO and return the mapped DTO
    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());

        // Save the project and return it as a mapped DTO
        projectRepository.save(project);

        return modelMapper.map(project, ProjectDTO.class);
    }

    // Get a project by its ID
    @Override
    public ProjectDTO getProjectById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            // Convert the Project to ProjectDTO
            ProjectDTO projectDTO = mapProjectToProjectDTO(project);
            return projectDTO;
        } else {
            // Handle the case where the project with the given ID is not found
            throw new ProjectNotFoundException("Project not found with ID: " + id);
        }
    }


    // Get a list of all projects
    @Override
    public List<ProjectDTO> getAll() {
        try {
            List<Project> projects = projectRepository.findAll();
            if (projects.isEmpty()) {
                throw new NotFoundException("No projects found");
            }

            return projects.stream()
                    .map(project -> new ProjectDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), project.getLastUpdated(), project.getDeleted()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Handle other exceptions here
            throw new ServiceException("An error occurred while fetching projects", e);
        }
    }


    // Get a list of all projects, including inactive ones

    @Override
    public List<ProjectWithUsersDTO> getAllProjectsWithUsers() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectWithUsersDTO> projectsWithUsers = new ArrayList<>();

        for (Project project : projects) {
            List<UserDTO> userList = getAllUsersByProjectId(project.getProjectId());
            if (userList == null) {
                userList = new ArrayList<>();
            }
            List<UserDTO> userDTOList = userList.stream()
                    .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                    .collect(Collectors.toList());

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

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAllProjects();
    }



    // Update a project
    @Override
    public Project updateProject(Project updatedProject) {
        return projectRepository.save(updatedProject);
    }

    // Get all users associated with a project by its ID
    @Override
    public List<UserDTO> getAllUsersByProjectId(Long projectId) {
        List<User> users = projectRepository.findAllUsersByProjectId(projectId);

        if (users.isEmpty()) {
            throw new NotFoundException("No users found for project with ID: " + projectId);
        }

        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .collect(Collectors.toList());
    }
    // Get all users associated with a project by its ID and role
    @Override
    public List<UserDTO> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role) {
        try {
            List<User> users = projectRepository.findAllUsersByProjectIdAndRole(projectId, role);

            if (users.isEmpty()) {
                throw new NotFoundException("No users found for project with ID: " + projectId + " and role: " + role);
            }

            return users.stream()
                    .map(user -> {
                        UserNames usernames = user.getUserNames();
                        String username = (usernames != null) ? usernames.getUsername() : null;
                        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), username);
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            // Handle the case where an invalid role is provided.
            throw new IllegalArgumentException("Invalid role provided.");
        } catch (Exception e) {
            // Handle other exceptions.
            throw new RuntimeException("Internal server error.");
        }
    }

    @Override
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        if (optionalProject.isPresent()) {
            Project existingProject = optionalProject.get();
            existingProject.setProjectName(projectDTO.getProjectName());
            existingProject.setProjectDescription(projectDTO.getProjectDescription());
            existingProject.setLastUpdated(LocalDateTime.now());

            Project updatedProject = projectRepository.save(existingProject);

            // Create the ProjectDTO within the service
            ProjectDTO updatedProjectDTO = new ProjectDTO(updatedProject.getProjectId(), updatedProject.getProjectName(), updatedProject.getProjectDescription(), updatedProject.getLastUpdated());

            return updatedProjectDTO;
        } else {
            throw new NotFoundException("Project with ID " + projectId + " not found");
        }
    }




    // Check if a project with the given ID exists and is soft-deleted
    @Override
    public ResponseEntity<String> deleteProject(Long id) {
        if (existsProjectById(id)) {
            boolean isDeleted = existsByIdIsDeleted(id);
            if (isDeleted) {
                return ResponseEntity.ok("Project doesn't exist");
            } else {
                boolean softDeleteResult = softDeleteProject(id);
                if (softDeleteResult) {
                    return ResponseEntity.ok("Deleted project successfully");
                } else {
                    throw new RuntimeException("Failed to delete project with ID " + id);
                }
            }
        } else {
            throw new NotFoundException("Invalid project ID: " + id);
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
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }

                project.getUsers().add(user);
                projectRepository.save(project);

                List<UserDTO> userDTOList = project.getUsers().stream()
                        .map(u -> new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getEnumRole()))
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

    @Override
    public ResponseEntity<String> removeUserFromProject(Long projectId, Long userId) {
            try {
                Optional<Project> optionalProject = projectRepository.findById(projectId);
                Optional<User> optionalUser = userRepository.findById(userId);

                if (optionalProject.isPresent() && optionalUser.isPresent()) {
                    Project project = optionalProject.get();
                    User user = optionalUser.get();
                    project.getUsers().remove(user);
                    projectRepository.save(project);

                    // Create userDTOList and projectUserDTO as needed

                    return ResponseEntity.ok("User removed");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to remove user");
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

                // Create userDTOList and projectUserDTO as needed

                boolean deleted = collaboratorService.deleteCollaborator(collaboratorDTO);
                if (!deleted) {
                    return ResponseEntity.ok("Unable to remove user");
                }
                return ResponseEntity.ok("User removed");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to remove user");
        }
    }



    @Override
    public boolean existsByIdIsDeleted(Long id) {
        return false;
    }

    // Soft delete a project by setting its deleted flag to true
    @Override
    public boolean softDeleteProject(Long id) {
        try {
            projectRepository.softDeleteProject(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Check if a project with the given ID exists
    @Override
    public boolean existsProjectById(Long id) {
        return projectRepository.existsById(id);
    }

    // Check if a user exists in a project by their IDs
    @Override
    public boolean existUserInProject(Long projectId, Long userId) {
        List<User> userList = projectRepository.existUserInProject(projectId, userId);
        if (userList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    // Get the count of all projects

    @Override
    public Integer getCountAllProjects() {
        Integer countProjects = projectRepository.countAllProjects();
        // Handle the case where countProjects is null or 0
        return countProjects != null ? countProjects : 0;
    }

    // Get the count of all projects by role
    @Override
    public Integer getCountAllProjectsByRole(EnumRole enumRole) {
        Integer countProjects = projectRepository.countAllProjectsByRole(enumRole);
        // Handle the case where countProjects is null or 0
        return countProjects != null ? countProjects : 0;
    }

    // Get the count of all projects by a user's ID
    @Override
    public Integer getCountAllProjectsByUserId(Long id) {
        Integer countProjects = projectRepository.countAllProjectsByUserId(id);
        // Handle the case where countProjects is null or 0
        return countProjects != null ? countProjects : 0;
    }
    // Get the count of all users associated with a project by its ID
    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        Integer countUsers = projectRepository.countAllUsersByProjectId(projectId);
        // Handle the case where countUsers is null or 0
        return countUsers != null ? countUsers : 0;
    }

    // Get a list of project names and their associated people count
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

    // Get the count of all users associated with a project by its ID and role
    @Override
    public Integer getCountAllUsersByProjectIdAndRole(Long projectId, EnumRole enumRole) {
        Integer countUsers = projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole);
        // Handle the case where countUsers is null or 0
        return countUsers != null ? countUsers : 0;
    }

    // Get the count of all active projects
    @Override
    public Integer getCountAllActiveProjects() {
        Integer countProjects = projectRepository.countAllActiveProjects();
        // Handle the case where countProjects is null or 0
        return countProjects != null ? countProjects : 0;
    }


    // Get the count of all inactive projects
    @Override
    public Integer getCountAllInActiveProjects() {
        Integer countProjects = projectRepository.countAllInActiveProjects();
        // Handle the case where countProjects is null or 0
        return countProjects != null ? countProjects : 0;
    }

    // Get a list of users associated with a project by its ID and role
    @Override
    public List<UserDTO> getUsersByProjectIdAndRole(Long projectId, String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        List<User> users = projectRepository.findUsersByProjectIdAndRole(projectId, userRole);
        List<UserDTO> userDTOList = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .collect(Collectors.toList());
        return userDTOList;
    }

    @Override
    public ResponseEntity<Object> addRepositoryToProject(Long projectId, Long repoId) {
        try {
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Get a list of ProjectDTOs for projects without Figma URLs
    @Override
    public List<ProjectDTO> getProjectsWithoutFigmaURL() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();
        for (Project project : projects) {
            // Check if the project has no Figma or Figma URL
            if (project.getFigma() == null || project.getFigma().getFigmaURL() == null) {
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        return projectDTOs;
    }
    // Get a list of ProjectDTOs for projects without Google Drive links
    @Override
    public List<ProjectDTO> getProjectsWithoutGoogleDriveLink() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();
        for (Project project : projects) {
            // Check if the project has no Google Drive or Drive link
            if (project.getGoogleDrive() == null || project.getGoogleDrive().getDriveLink() == null) {
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        return projectDTOs;
    }

    // Get project details by its ID
    @Override
    public ProjectDTO getProjectDetailsById(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            String projectName = project.getProjectName();
            String projectDescription = project.getProjectDescription();
            boolean status = project.getDeleted();
            List<User> users = project.getUsers();
            String pmName = null;
            for (User user : users) {
                // Find the Project Manager's name
                if (user.getEnumRole() == EnumRole.PROJECT_MANAGER) {
                    pmName = user.getName();
                    break;
                }
            }
            List<GitRepository> repositories = project.getRepositories();
            List<GitRepositoryDTO> repositoryDTOS = new ArrayList<>();
            for (GitRepository repository : repositories) {
                // Map GitRepository entities to GitRepositoryDTOs
                GitRepositoryDTO repositoryDTO = new GitRepositoryDTO();
                repositoryDTO.setName(repository.getName());
                repositoryDTO.setDescription(repository.getDescription());
                repositoryDTOS.add(repositoryDTO);
            }
            Figma figma = project.getFigma();
            String figmaURL = figma != null ? figma.getFigmaURL() : null;
            FigmaDTO figmaDTO = new FigmaDTO(figmaURL);
            GoogleDrive googleDrive = project.getGoogleDrive();
            String driveLink = googleDrive != null ? googleDrive.getDriveLink() : null;
            GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(driveLink);
            LocalDateTime lastUpdated = project.getLastUpdated();
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
            return new ProjectDTO(); // Return an empty ProjectDTO if the project doesn't exist
        }
    }

    // Map a Project entity to a ProjectDTO
    @Override
    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }
}