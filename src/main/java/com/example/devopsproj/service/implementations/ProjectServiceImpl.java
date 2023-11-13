package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.model.*;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * The `ProjectServiceImpl` class provides services related to projects, including creation,
 * retrieval, updating, and management of project-related data.
 *
 * @version 2.0
 */

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final GitRepositoryRepository gitRepositoryRepository;
    private final ModelMapper modelMapper;
    private final GitHubCollaboratorService collaboratorService;
    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    /**
     * Creates a new project based on the provided ProjectDTO.
     *
     * @param projectDTO The ProjectDTO containing project information.
     * @return The ProjectDTO of the newly created project.
     */
    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        logger.info("Creating project: {}", projectDTO.getProjectName());

        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());
        projectRepository.save(project);

        logger.info("Project created successfully. Project Name: {}", project.getProjectName());

        return modelMapper.map(project, ProjectDTO.class);
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param id The ID of the project.
     * @return An Optional containing the Project if found.
     */
    @Override
    public Optional<Project> getProjectById(Long id){
        logger.info("Fetching project by ID: {}", id);
        return projectRepository.findById(id);
    }

    /**
     * Retrieves project information by its ID for HTTP response.
     *
     * @param id The ID of the project.
     * @return ResponseEntity containing the ProjectDTO and appropriate HTTP status.
     */
    @Override
    public ResponseEntity<Object> getProject(Long id) {
        try {
            logger.info("Fetching project information for HTTP response. Project ID: {}", id);

            Optional<Project> checkProject = projectRepository.findById(id);

            if (checkProject.isEmpty()) {
                logger.warn("Project with ID {} not found.", id);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Project project = checkProject.get();

            if (Boolean.TRUE.equals(project.getDeleted())) {
                logger.warn("Project with ID {} is marked as deleted.", id);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(project.getProjectId());
            projectDTO.setProjectName(project.getProjectName());
            projectDTO.setProjectDescription(project.getProjectDescription());
            logger.info("Project information retrieved successfully. Project Name: {}", projectDTO.getProjectName());

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.error("Exception while fetching project information. {}", e.getMessage());

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Internal server error while fetching project information. {}", e.getMessage());

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of projects with associated users.
     *
     * @return A list of ProjectWithUsersDTO representing projects with associated users.
     */
    @Override
    public List<ProjectWithUsersDTO> getAllProjectsWithUsers() {
        logger.info("Fetching all projects with associated users");

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
        logger.info("Fetched {} projects with associated users", projectsWithUsers.size());

        return projectsWithUsers;
    }

    /**
     * Adds a repository to a project.
     *
     * @param projectId The ID of the project.
     * @param repoId    The ID of the Git repository.
     * @return ResponseEntity indicating success or failure.
     */
    @Override
    public ResponseEntity<Object> addRepositoryToProject(Long projectId, Long repoId){
        logger.info("Adding repository with ID {} to project with ID {}", repoId, projectId);

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
            logger.info("Repository added to project successfully");
            return ResponseEntity.ok("Stored successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all projects.
     *
     * @return A list of Project entities representing all projects.
     */
    @Override
    public List<Project> getAll(){
        logger.info("Fetching all projects");
        return projectRepository.findAll();
    }

    /**
     * Retrieves all active projects.
     *
     * @return A list of Project entities representing all active projects.
     */
    @Override
    public List<Project> getAllProjects(){
        logger.info("Fetching all active projects");
        return projectRepository.findAllProjects();
    }

    /**
     * Updates a project.
     *
     * @param updatedProject The updated Project entity.
     * @return The updated Project entity.
     */
    @Override
    public Project updateProject(Project updatedProject){
        logger.info("Updating project with ID {}", updatedProject.getProjectId());
        return projectRepository.save(updatedProject);
    }

    /**
     * Retrieves all users associated with a project.
     *
     * @param projectId The ID of the project.
     * @return A list of UserDTO representing all users associated with the project.
     */
    @Override
    public List<UserDTO> getAllUsersByProjectId(Long projectId) {
        logger.info("Fetching all users for project with ID {}", projectId);

        List<User> users = projectRepository.findAllUsersByProjectId(projectId);

        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
    }

    /**
     * Retrieves users associated with a project and a specific role.
     *
     * @param projectId The ID of the project.
     * @param role      The role of the users.
     * @return A list of User entities representing users with the specified role in the project.
     */
    @Override
    public List<User> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role){
        logger.info("Fetching users for project with ID {} and role {}", projectId, role);

        List<User> users = projectRepository.findAllUsersByProjectIdAndRole(projectId, role);
        if(users.isEmpty()){
            logger.info("No users found for project with ID {} and role {}", projectId, role);
            return Collections.emptyList();
        }
        else {
            logger.info("Fetched {} users for project with ID {} and role {}", users.size(), projectId, role);
            return users;
        }
    }

    /**
     * Checks if a project with the specified ID is marked as deleted.
     *
     * @param id The ID of the project.
     * @return True if the project is marked as deleted, false otherwise.
     */
    @Override
    public boolean existsByIdIsDeleted(Long id){
        logger.info("Checking if project with ID {} exists and is marked as deleted", id);

        Optional<Project> checkProject = projectRepository.findById(id);
        if (checkProject.isEmpty()){
            logger.info("Project with ID {} does not exist", id);

            return true;
        }
        Project cproject = checkProject.get();
        logger.info("Project with ID {} exists, marked as deleted: {}", id, cproject.getDeleted());

        return cproject.getDeleted(); //true if deleted=1, else false
    }

    /**
     * Soft deletes a project.
     *
     * @param id The ID of the project to be soft-deleted.
     * @return True if the soft deletion is successful, false otherwise.
     */
    @Override
    public boolean softDeleteProject(Long id){
        logger.info("Soft deleting project with ID {}", id);
        try{
            projectRepository.softDeleteProject(id);
            logger.info("Soft deletion of project with ID {} successful", id);
            return true;
        }catch (Exception e){
            logger.error("Error during soft deletion of project with ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a project with the specified ID exists.
     *
     * @param id The ID of the project.
     * @return True if the project exists, false otherwise.
     */
    @Override
    public boolean existsProjectById(Long id){
        logger.info("Checking if project with ID {} exists", id);
        return projectRepository.existsById(id);
    }

    /**
     * Checks if a user exists in a project.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user.
     * @return True if the user is present in the project, false otherwise.
     */
    @Override
    public boolean existUserInProject(Long projectId, Long userId){
        logger.info("Checking if user with ID {} exists in project with ID {}", userId, projectId);
        List<User> userList = projectRepository.existUserInProject(projectId, userId);
        // False if user is present in project
        return !userList.isEmpty();
    }

    /**
     * Gets the count of all projects.
     *
     * @return The count of all projects.
     */
    @Override
    public Integer getCountAllProjects(){
        logger.info("Getting the count of all projects");
        return projectRepository.countAllProjects();
    }

    /**
     * Gets the count of all projects by role.
     *
     * @param enumRole The role for which the count is calculated.
     * @return The count of all projects with the specified role.
     */
    @Override
    public Integer getCountAllProjectsByRole(EnumRole enumRole) {
        logger.info("Getting the count of all projects by role: {}", enumRole);
        return projectRepository.countAllProjectsByRole(enumRole);
    }

    /**
     * Gets the count of all projects by user ID.
     *
     * @param id The ID of the user.
     * @return The count of all projects associated with the user.
     */
    @Override
    public Integer getCountAllProjectsByUserId(Long id) {
        logger.info("Getting the count of all projects by user ID: {}", id);
        return projectRepository.countAllProjectsByUserId(id);
    }

    /**
     * Gets the count of all users in a project.
     *
     * @param projectId The ID of the project.
     * @return The count of all users in the project.
     */
    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        logger.info("Getting the count of all users in project with ID: {}", projectId);
        return projectRepository.countAllUsersByProjectId(projectId);
    }

    /**
     * Gets a list of DTOs containing the count of people and project names for all projects.
     *
     * @return List of {@link ProjectNamePeopleCountDTO}.
     */
    @Override
    public List<ProjectNamePeopleCountDTO> getCountAllPeopleAndProjectName() {
        logger.info("Getting count of people and project names for all projects");

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

    /**
     * Gets the count of users in a project with a specific role.
     *
     * @param projectId The ID of the project.
     * @param enumRole   The role for which the count is calculated.
     * @return The count of users in the project with the specified role.
     */
    @Override
    public Integer getCountAllUsersByProjectIdAndRole(Long projectId, EnumRole enumRole) {
        logger.info("Getting count of users in project with ID {} and role: {}", projectId, enumRole);
        return projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole);
    }

    /**
     * Gets the count of all active projects.
     *
     * @return The count of all active projects.
     */
    @Override
    public Integer getCountAllActiveProjects(){
        logger.info("Getting count of all active projects");
        return projectRepository.countAllActiveProjects();
    }

    /**
     * Gets the count of all inactive projects.
     *
     * @return The count of all inactive projects.
     */
    @Override
    public Integer getCountAllInActiveProjects(){
        logger.info("Getting count of all inactive projects");
        return projectRepository.countAllInActiveProjects();
    }

    /**
     * Gets a list of users in a project with a specific role.
     *
     * @param projectId The ID of the project.
     * @param role      The role for which the users are retrieved.
     * @return List of {@link UserDTO}.
     */
    @Override
    public List<UserDTO> getUsersByProjectIdAndRole(Long projectId, String role) {
        logger.info("Getting users in project with ID {} and role: {}", projectId, role);

        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        List<User> users = projectRepository.findUsersByProjectIdAndRole(projectId, userRole);
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
    }

    /**
     * Gets a list of projects without a Figma URL.
     *
     * @return List of {@link ProjectDTO}.
     */
    @Override
    public List<ProjectDTO> getProjectsWithoutFigmaURL() {
        logger.info("Getting projects without a Figma URL");

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

        logger.info("Fetched {} projects", projectDTOs.size());
        return projectDTOs;
    }

    /**
     * Gets a list of projects without a Google Drive link.
     *
     * @return List of {@link ProjectDTO}.
     */
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

        logger.info("{} Projects fetched", projectDTOs.size());
        return projectDTOs;
    }

    /**
     * Gets the details of a project by its ID.
     *
     * @param projectId The ID of the project.
     * @return A {@link ProjectDTO} containing project details.
     */
    @Override
    public ProjectDTO getProjectDetailsById(Long projectId) {
        logger.info("Getting details for project with ID: {}", projectId);

        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            logger.info("Project is present");
            Project project = optionalProject.get();

            // Retrieve the required details from the project object
            String projectName = project.getProjectName();
            String projectDescription = project.getProjectDescription();

            List<User> users = project.getUsers();
            String pmName = null;
            for (User user : users){
                if(user.getEnumRole()==EnumRole.PROJECT_MANAGER){
                    pmName = user.getName();
                    break;
                }
            }

            // Setting and adding the repos to the list
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

            logger.info("Fetched information from project");
            // Create and return a ProjectDTO object with the required details
            return new ProjectDTO(
                    projectName,
                    projectDescription,
                    pmName,
                    repositoryDTOS,
                    figmaDTO,
                    new GoogleDriveDTO(driveLink),
                    lastUpdated
            );
        } else {
            logger.warn("Project with ID {} not found.", projectId);
            return new ProjectDTO();
        }
    }

    /**
     * Adds a user to a project by their ID.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user to be added to the project.
     * @return A {@link ResponseEntity} containing the project details with the added user.
     */
    @Override
    public ResponseEntity<Object> addUserToProjectByUserIdAndProjectId(Long projectId, Long userId){
        logger.info("Adding user with ID {} to project with ID: {}", userId, projectId);

        try {
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();
                List<User> userList = projectRepository.existUserInProject(projectId, userId);
                if (!userList.isEmpty()) {
                    logger.warn("User with ID {} is already in project with ID {}.", userId, projectId);
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                project.getUsers().add(user);
                projectRepository.save(project);
                List<UserDTO> userDTOList = project.getUsers().stream()
                        .map(users -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                        .toList();
                ProjectUserDTO projectUserDTO = new ProjectUserDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), userDTOList);
                logger.info("User with ID {} added to project with ID: {}", userId, projectId);
                return new ResponseEntity<>(projectUserDTO, HttpStatus.OK);
            } else {
                logger.warn("Project with ID {} or User with ID {} is not found.", projectId, userId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error adding user with ID {} to project with ID: {}", userId, projectId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes a user from a project by their ID.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user to be removed from the project.
     * @return A {@link ResponseEntity} containing a message indicating the result of the removal operation.
     */
    @Override
    public ResponseEntity<String> removeUserFromProjectByUserIdAndProjectId(Long projectId, Long userId) {
        logger.info("Removing user with ID {} from project with ID: {}", userId, projectId);

        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalProject.isPresent() && optionalUser.isPresent()) {
            Project project = optionalProject.get();
            User user = optionalUser.get();

            if (!project.getUsers().contains(user)) {
                logger.warn("User with ID {} is not part of project with ID: {}", userId, projectId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not part of the project");
            }

            project.getUsers().remove(user);
            projectRepository.save(project);
            logger.info("User with ID {} removed from project with ID: {}", userId, projectId);

            return ResponseEntity.ok("User removed");
        } else {
            logger.warn("Project with ID {} or User with ID {} not found.", projectId, userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
        }
    }

    /**
     * Removes a user from a project and deletes their collaboration in a repository.
     *
     * @param projectId      The ID of the project.
     * @param userId         The ID of the user to be removed from the project.
     * @param collaboratorDTO The collaborator information for deleting collaboration in a repository.
     * @return A {@link ResponseEntity} containing a message indicating the result of the removal operation.
     */
    @Override
    public ResponseEntity<String> removeUserFromProjectAndRepo(Long projectId, Long userId, CollaboratorDTO collaboratorDTO){
        logger.info("Removing user with ID {} from project with ID: {}", userId, projectId);

        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalProject.isPresent() && optionalUser.isPresent()) {
            Project project = optionalProject.get();
            User user = optionalUser.get();
            project.getUsers().remove(user);
            projectRepository.save(project);
            logger.info("User with ID {} removed from project with ID: {}", userId, projectId);

            boolean deleted = collaboratorService.deleteCollaborator(collaboratorDTO);
            if (!deleted) {
                logger.warn("Collaborator deletion failed for user with ID: {}", userId);
                return ResponseEntity.badRequest().build();
            }

            logger.info("Collaborator deleted for user with ID: {}", userId);
            return ResponseEntity.ok("User removed");
        } else {
            logger.warn("Project with ID {} or User with ID {} not found.", projectId, userId);
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
