package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.JwtService;
import com.example.devopsproj.service.interfaces.ProjectService;
import com.example.devopsproj.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
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
 * The `UserServiceImpl` class provides services for managing user data, including creation, retrieval,
 * updating, soft deletion, and other user-related operations.
 *
 * @version 2.0
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService, UserService {

    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final JwtService jwtService;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * Saves a user based on the provided user creation data.
     *
     * @param userCreationDTO The DTO containing user creation data.
     * @return {@link UserCreationDTO} The saved UserDTO.
     */
    @Override
    public UserDTO saveUser(UserCreationDTO userCreationDTO) {
        logger.info("Saving user: {}", userCreationDTO.getName());

        User user = new User();
        user.setId(userCreationDTO.getId());
        user.setName(userCreationDTO.getName());
        user.setEmail(userCreationDTO.getEmail());
        user.setEnumRole(userCreationDTO.getEnumRole());
        user.setLastUpdated(LocalDateTime.now());
        user.setLastLogout(LocalDateTime.now());
        userRepository.save(user);
        logger.info("User saved successfully: {}", userCreationDTO.getName());

        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * Updates a user based on the provided user ID and DTO.
     *
     * @param id      The ID of the user to update.
     * @param userDTO The DTO containing updated user data.
     * @return The updated {@link UserDTO}.
     * @throws EntityNotFoundException If the user with the given ID is not found.
     */
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(userDTO.getName());
            existingUser.setEnumRole(userDTO.getEnumRole());
            existingUser.setLastUpdated(LocalDateTime.now());
            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully with ID: {}", id);

            return new UserDTO(updatedUser.getName(), updatedUser.getEnumRole(), updatedUser.getLastUpdated());
        } else {
            logger.error("Error updating user with ID: {}", id);
            throw new EntityNotFoundException("User not found" + id);
        }
    }

    /**
     * Retrieves a user based on the provided user ID.
     *
     * @param id The ID of the user to retrieve.
     * @return An Optional containing the user if found.
     */
    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Retrieving user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Checks if a user with the given ID is soft-deleted.
     *
     * @param id The ID of the user to check.
     * @return True if the user is soft-deleted, false otherwise.
     */
    @Override
    public boolean existsByIdIsDeleted(Long id) {
        logger.info("Checking if user with ID {} is soft-deleted", id);

        Optional<User> checkUser = userRepository.findById(id);
        if (checkUser.isEmpty()){
            logger.info("User with ID {} not found (soft-deleted)", id);
            return true;
        }
        User cuser = checkUser.get();
        logger.info("User with ID {} is not soft-deleted", id);
        return cuser.getDeleted(); //true if deleted=1, false otherwise
    }

    /**
     * Soft deletes a user based on the provided user ID.
     *
     * @param id The ID of the user to soft-delete.
     * @return True if soft-deletion is successful, false otherwise.
     */
    @Override
    public boolean softDeleteUser(Long id) {
        logger.info("Soft-deleting user with ID: {}", id);

        try {
            userRepository.softDelete(id);
            logger.info("User soft-deleted successfully with ID: {}", id);
            return true; //setting deleted=1 / true
        } catch (Exception e) {
            logger.error("Error soft-deleting user with ID: {}", id, e);
            return false; //keeping deleted false
        }
    }

    /**
     * Checks if a user with the given ID exists.
     *
     * @param id The ID of the user to check.
     * @return True if the user exists, false otherwise.
     */
    @Override
    public boolean existsById(Long id) {
        logger.info("Checking if user with ID {} exists", id);
        return userRepository.existsById(id);
    }

    /**
     * Retrieves users based on the provided role.
     *
     * @param enumRole The role of the users to retrieve.
     * @return A list of users with the specified role.
     */
    @Override
    public List<User> getUsersByRole(EnumRole enumRole) {
        logger.info("Retrieving users by role: {}", enumRole);
        return userRepository.findByRole(enumRole);
    }

    /**
     * Retrieves the total count of all users.
     *
     * @return The total count of all users.
     */
    @Override
    public Integer getCountAllUsers() {
        logger.info("Retrieving the total count of all users");
        return userRepository.countAllUsers();
    }

    /**
     * Retrieves the total count of users with the specified role.
     *
     * @param role The role of the users to count.
     * @return The total count of users with the specified role.
     */
    @Override
    public Integer getCountAllUsersByRole(EnumRole role) {
        logger.info("Retrieving the total count of users by role: {}", role);
        return userRepository.countAllUsersByRole(role);
    }

    /**
     * Retrieves the total count of users associated with a specific project.
     *
     * @param projectId The ID of the project to count users for.
     * @return The total count of users associated with the specified project.
     */
    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        logger.info("Retrieving the total count of users by project ID: {}", projectId);

        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent()) {
            logger.info("The project is present");
            return userRepository.countAllUsersByProjectId(projectId);
        } else {
            logger.warn("Project not found with ID: {}", projectId);
            return 0;
        }
    }

    /**
     * Retrieves a list of users with their associated projects.
     *
     * @return A list of UserProjectsDTO representing users and their associated projects.
     */
    @Override
    public List<UserProjectsDTO> getAllUsersWithProjects() {
        logger.info("Retrieving all users with projects");

        List<User> users = userRepository.findAllUsers();
        List<UserProjectsDTO> userProjectsDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            // Remove any projects that are marked as deleted
            List<Project> existingProjects = projects.stream()
                    .filter(project -> !project.getDeleted())
                    .toList();

            List<String> projectNames = existingProjects.stream()
                    .map(Project::getProjectName)
                    .toList();

            UserProjectsDTO userProjectsDTO = new UserProjectsDTO(user.getId(), user.getName(), projectNames);
            userProjectsDTOs.add(userProjectsDTO);
        }
        logger.info("Retrieved {} users with projects", userProjectsDTOs.size());

        return userProjectsDTOs;
    }

    /**
     * Retrieves a list of users with the specified role who are not associated with the given project ID.
     *
     * @param role      The role of the users to retrieve.
     * @param projectId The ID of the project to exclude.
     * @return A list of UserDTO representing users without the specified project.
     */
    @Override
    public List<UserDTO> getAllUsersWithoutProjects(EnumRole role, Long projectId) {
        logger.info("Retrieving all users without projects for role: {} and project ID: {}", role, projectId);

        List<User> users = userRepository.findAllUsersByRole(role);
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            // List of users who don't have the given project ID
            if (projects.stream().noneMatch(project -> Objects.equals(project.getProjectId(), projectId))) {
                UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole());
                userDTOs.add(userDTO);
            }
        }

        logger.info("Retrieved {} users without projects for role: {} and project ID: {}", userDTOs.size(), role, projectId);
        return userDTOs;
    }

    /**
     * Retrieves a list of users with multiple valid projects.
     *
     * @return A list of UserProjectsDTO representing users with multiple valid projects.
     */
    @Override
    public List<UserProjectsDTO> getUsersWithMultipleProjects() {
        logger.info("Retrieving users with multiple projects");

        List<UserProjectsDTO> allUsersWithProjects = getAllUsersWithProjects();
        List<UserProjectsDTO> usersWithMultipleProjects = new ArrayList<>();
        for (UserProjectsDTO userProjectsDTO : allUsersWithProjects) {
            List<String> projectNames = userProjectsDTO.getProjectNames();
            List<String> validProjectNames = new ArrayList<>();
            // Check if each project exists in the database
            for (String projectName : projectNames) {
                boolean flag = false;
                List<Project> projects = projectRepository.findAllProjects();
                for (Project project : projects) {
                    flag = projectName.equals(project.getProjectName());
                }
                if (flag) {
                    validProjectNames.add(projectName);
                }
            }
            // Update the UserProjectsDTO with valid project names
            userProjectsDTO.setProjectNames(validProjectNames);
            // Add the UserProjectsDTO to the list if it has multiple projects
            if (validProjectNames.size() > 1) {
                usersWithMultipleProjects.add(userProjectsDTO);
            }
        }

        logger.info("Retrieved {} users with multiple projects", usersWithMultipleProjects.size());
        return usersWithMultipleProjects;
    }

    /**
     * Retrieves a list of all users.
     *
     * @return A list of UserDTO representing all users.
     */
    @Override
    public List<UserDTO> getAllUsers() {
        logger.info("Retrieving all users");

        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} users", users.size());
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
    }

    /**
     * Retrieves a list of projects and their repositories for a given user.
     *
     * @param userId The ID of the user.
     * @return A list of ProjectDTO representing projects and their repositories.
     * @throws EntityNotFoundException If the user with the given ID is not found.
     */
    @Override
    public List<ProjectDTO> getAllProjectsAndRepositoriesByUserId(Long userId) {
        logger.info("Retrieving projects and repositories for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Project> projects = user.getProjects();

        List<ProjectDTO> projectDTOs = new ArrayList<>();
        for (Project project : projects) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(project.getProjectId());
            projectDTO.setProjectName(project.getProjectName());

            // Add list of repos to the repositories list
            List<GitRepository> repositories = project.getRepositories();
            List<GitRepositoryDTO> repositoryDTOs = new ArrayList<>();
            for (GitRepository repository : repositories) {
                GitRepositoryDTO repositoryDTO = new GitRepositoryDTO();
                repositoryDTO.setRepoId(repository.getRepoId());
                repositoryDTO.setName(repository.getName());
                repositoryDTOs.add(repositoryDTO);
            }
            projectDTO.setRepositories(repositoryDTOs);
            projectDTOs.add(projectDTO);
        }

        logger.info("Retrieved {} projects and repositories for user with ID: {}", projectDTOs.size(), userId);
        return projectDTOs;
    }

    /**
     * Retrieves projects based on user ID and role.
     *
     * @param userId The ID of the user.
     * @param role   The role of the user.
     * @return A ResponseEntity containing a list of ProjectDTO.
     */
    @Override
    public ResponseEntity<Object> getProjectsByRoleAndUserId(Long userId, String role){
        logger.info("Retrieving projects for user with ID: {} and role: {}", userId, role);

        EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); // Getting value of role(string)
        List<Project> projects = userRepository.findByRoleAndUserId(userId, userRole);
        if (projects.isEmpty()){
            logger.info("No projects found for user with ID: {} and role: {}", userId, role);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<ProjectDTO> projectDTOList = projects.stream()
                .map(project -> {
                    List<GitRepositoryDTO> repositoryDTOList = project.getRepositories().stream()
                            .map(repository -> new GitRepositoryDTO( repository.getName(), repository.getDescription()))
                            .toList();
                    Figma figma = project.getFigma();
                    String figmaURL = figma != null ? figma.getFigmaURL() : null; // Retrieve the Figma URL
                    FigmaDTO figmaDTO = new FigmaDTO(figmaURL);
                    GoogleDrive googleDrive = project.getGoogleDrive();
                    String driveLink = googleDrive != null ? googleDrive.getDriveLink() : null; // Retrieve the driveLink

                    return new ProjectDTO(
                            project.getProjectId(),
                            project.getProjectName(),
                            project.getProjectDescription(),
                            repositoryDTOList,
                            figmaDTO,
                            new GoogleDriveDTO(driveLink) // Pass the GoogleDriveDTO object with driveLink value
                    );
                })
                .toList();

        logger.info("Retrieved {} projects for user with ID: {} and role: {}", projectDTOList.size(), userId, role);
        return new ResponseEntity<>(projectDTOList, HttpStatus.OK);
    }

    /**
     * Verifies user login and generates an authentication token.
     *
     * @param email The email of the user.
     * @return A UserDTO containing user details and authentication token.
     */
    @Override
    public UserDTO loginVerification(String email){
        logger.info("Verifying login for user with email: {}", email);

        UserDTO userDTO = new UserDTO();
        User user = userRepository.existsByEmail(email);
        if(user == null){
            logger.warn("User with email {} not found. Login verification unsuccessful.", email);
            return null;
        }
        user.setLastUpdated(LocalDateTime.now());
        userRepository.save(user);
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setEnumRole(user.getEnumRole());
        userDTO.setLastUpdated(user.getLastUpdated());
        //generate token
        String jwtToken = jwtService.generateToken(user);
        userDTO.setToken(jwtToken);

        logger.info("Login verification successful for user with email: {}", email);
        return userDTO;
    }

    /**
     * Logs out the user and updates the last logout timestamp.
     *
     * @param id The ID of the user.
     * @return A message indicating the success or failure of the logout.
     */
    @Override
    public String userLogout(Long id){
        logger.info("Logging out user with ID: {}", id);

        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setLastLogout(LocalDateTime.now());
            userRepository.save(user);

            logger.info("User with ID {} logged out successfully.", id);
            return "User logged out successfully";
        }else{
            logger.warn("User with ID {} not found. Logout unsuccessful.", id);
            return "Log out unsuccessful";
        }
    }


    //-------------IUService-----------

    @Override
    public User getUserViaPhoneNumber(String phoneNumber){
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public User getUserByMail(String userMail) {
        return userRepository.findByEmail(userMail);
    }
}
