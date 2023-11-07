package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;

import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService, UserService {

    private final UserRepository userRepository;
    private final JwtServiceImpl jwtServiceImpl;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    // Implementing user creation using DTO pattern
    @Override
    public User saveUser(@RequestBody UserCreationDTO userCreationDTO) {
        User user = new User();
        user.setId(userCreationDTO.getId());
        user.setName(userCreationDTO.getName());
        user.setEmail(userCreationDTO.getEmail());
        user.setEnumRole(userCreationDTO.getEnumRole());
        user.setLastUpdated(LocalDateTime.now());
        user.setLastLogout(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        logger.info("User created with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(userDTO.getName());
            existingUser.setEnumRole(userDTO.getEnumRole());
            existingUser.setLastUpdated(LocalDateTime.now());

            User updatedUser = userRepository.save(existingUser);
            logger.info("User updated with ID: {}", updatedUser.getId());

            return new UserDTO(updatedUser.getName(), updatedUser.getEnumRole(), updatedUser.getLastUpdated());
        } else {
            logger.warn(USER_NOT_FOUND_WITH_ID, id);
            throw new EntityNotFoundException("User not found" + id);
        }
    }

    // Find user by user id
    @Override
    public Optional<User> getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("User found with ID: {}", id);
        } else {
            logger.warn(USER_NOT_FOUND_WITH_ID, id);
        }
        return user;
    }

    @Override
    public boolean existsByIdIsDeleted(Long id) {
        Optional<User> checkUser = userRepository.findById(id);
        if (checkUser.isPresent()) {
            User cuser = checkUser.get();
            if (cuser.getDeleted()) {
                logger.info("User found and deleted with ID: {}", id);
                return true;
            } else {
                logger.info("User found and not deleted with ID: {}", id);
            }
        } else {
            logger.info(USER_NOT_FOUND_WITH_ID, id);
        }
        return false;
    }

    // Soft deleting the user
    @Override
    public boolean softDeleteUser(Long id) {
        try {
            userRepository.softDelete(id);
            logger.info("User soft-deleted with ID: {}", id);
            return true; // setting deleted=true
        } catch (Exception e) {
            logger.error("Error while soft-deleting user with ID: {}", id, e);
            return false; // keeping deleted false
        }
    }

    @Override
    public boolean existsById(Long id) {
        boolean exists = userRepository.existsById(id);
        if (exists) {
            logger.info("User exists with ID: {}", id);
        } else {
            logger.warn("User does not exist with ID: {}", id);
        }
        return exists;
    }

    // Get all users based on role ID
    @Override
    public List<User> getUsersByRole(EnumRole enumRole) {
        List<User> users = userRepository.findByRole(enumRole);
        logger.info(FOUND_USERS_WITH_ROLE, users.size(), enumRole);
        return users;
    }

    private static final String FOUND_USERS_WITH_ROLE = "Found {} users with role: {}";
    private static final String USER_NOT_FOUND_WITH_ID = "User not found with ID: {}";



    @Override
    public List<UserDTO> getUserDTOsByRole(EnumRole role) {
        List<User> users = userRepository.findByEnumRole(role);
        logger.info(FOUND_USERS_WITH_ROLE, users.size(), role);

        return users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getEnumRole(),
                        user.getLastUpdated(),
                        user.getLastLogout()))
                .toList();
    }

    @Override
    public Integer getCountAllUsers() {
        Integer countUsers = userRepository.countAllUsers();
        logger.info("Count of all users: {}", countUsers);
        return countUsers;
    }

    @Override
    public Integer getCountAllUsersByRole(EnumRole role) {
        Integer countUsers = userRepository.countAllUsersByRole(role);
        logger.info("Count of users with role {}: {}", role, countUsers);
        return countUsers;
    }

    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        Integer countUsers = projectRepository.countAllUsersByProjectId(projectId);
        logger.info("Count of users for project with ID {}: {}", projectId, countUsers);
        return countUsers != null ? countUsers : 0;
    }

    @Override
    public List<UserProjectsDTO> getAllUsersWithProjects() {
        List<User> users = userRepository.findAllUsers();
        logger.info("Found {} users", users.size());

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

        return userProjectsDTOs;
    }

    @Override
    public List<UserDTO> getAllUsersWithoutProjects(EnumRole role, Long projectId) {
        List<User> users = userRepository.findAllUsersByRole(role);
        logger.info(FOUND_USERS_WITH_ROLE, users.size(), role);

        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            if (projects.stream().noneMatch(project -> Objects.equals(project.getProjectId(), projectId))) {
                UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole());
                userDTOs.add(userDTO);
            }
        }
        return userDTOs;
    }
    @Override
    public List<UserProjectsDTO> getUsersWithMultipleProjects() {
        List<UserProjectsDTO> allUsersWithProjects = getAllUsersWithProjects();
        logger.info("Retrieved {} users with projects.", allUsersWithProjects.size());

        List<UserProjectsDTO> usersWithMultipleProjects = new ArrayList<>();

        for (UserProjectsDTO userProjectsDTO : allUsersWithProjects) {
            List<String> projectNames = userProjectsDTO.getProjectNames();
            List<String> validProjectNames = new ArrayList<>();

            for (String projectName : projectNames) {
                if (projectExists(projectName)) {
                    validProjectNames.add(projectName);
                }
            }

            // Log project name filtering results
            logger.info("User {} has {} valid projects out of {} total projects.",
                    userProjectsDTO.getUserId(), validProjectNames.size(), projectNames.size());

            userProjectsDTO.setProjectNames(validProjectNames);

            if (validProjectNames.size() > 1) {
                usersWithMultipleProjects.add(userProjectsDTO);
            }
        }

        logger.info("Found {} users with multiple projects.", usersWithMultipleProjects.size());
        return usersWithMultipleProjects;
    }

    @Override
    public boolean projectExists(String projectName) {
        List<Project> projects = projectRepository.findAllProjects();
        boolean exists = projects.stream()
                .anyMatch(project -> project.getProjectName().equals(projectName));
        logger.info("Project '{}' exists: {}", projectName, exists);
        return exists;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} users from the database.", users.size());
        return users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
    }
    @Override
    public List<ProjectDTO> getAllProjectsAndRepositoriesByUserId(Long userId) {
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
        return projectDTOs;
    }


    @Override
    public String deleteUserById(Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            logger.warn(USER_NOT_FOUND_WITH_ID, userId);
            return "Invalid user ID";
        }
        User user = userOptional.get();
        if (Boolean.TRUE.equals(user.getDeleted())) {
            logger.warn("User with ID {} is already deleted.", userId);
            return "User doesn't exist";
        }

        if (softDeleteUser(userId)) {
            logger.info("User with ID {} successfully deleted", userId);
            return "User successfully deleted";
        } else {
            logger.error("Failed to delete user with ID: {}", userId);
            return "404 Not found";
        }
    }

    @Override
    public List<Project> getUsersByRoleAndUserId(Long userId, EnumRole userRole) {
        logger.info("Fetching users with role {} and ID: {}", userRole, userId);
        return userRepository.findByRoleAndUserId(userId, userRole);
    }

    @Override
    public UserDTO loginVerification(String email) {
        logger.info("Verifying login for email: {}", email);
        UserDTO userDTO = new UserDTO();
        User user = userRepository.existsByEmail(email);
        if (user == null) {
            logger.warn("User not found for email: {}", email);
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
        String jwtToken = jwtServiceImpl.generateToken(user);
        userDTO.setToken(jwtToken);
        logger.info("Login verification successful for user with email: {}", email);
        return userDTO;
    }



    @Override
    public String userLogout(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLogout(LocalDateTime.now());
            userRepository.save(user);
            logger.info("User with ID {} logged out successfully.", id);
            return "User logged out successfully";
        } else {
            logger.warn("User logout unsuccessful for ID: {}", id);
            return "Log out unsuccessful";
        }
    }

    @Override
    public List<ProjectDTO> getProjectsByRoleIdAndUserId(Long userId, String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); // Getting the value of the role (string)
        List<Project> projects = getUsersByRoleAndUserId(userId, userRole);

        logger.info("Retrieved {} projects for User ID {} with role: {}", projects.size(), userId, role);

        return projects.stream()
                .map(this::mapProjectToDTO)
                .toList(); // Use 'Stream.toList()' to collect the results into a List
    }

    private ProjectDTO mapProjectToDTO(Project project) {
        List<GitRepositoryDTO> repositoryDTOList = mapRepositoriesToDTO(project.getRepositories());
        FigmaDTO figmaDTO = mapFigmaToDTO(project.getFigma());
        GoogleDriveDTO googleDriveDTO = mapGoogleDriveToDTO(project.getGoogleDrive());

        return new ProjectDTO(
                project.getProjectId(),
                project.getProjectName(),
                project.getProjectDescription(),
                repositoryDTOList,
                figmaDTO,
                googleDriveDTO
        );
    }

    private List<GitRepositoryDTO> mapRepositoriesToDTO(List<GitRepository> repositories) {
        return repositories.stream()
                .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
                .toList(); // Use 'Stream.toList()' to collect the results into a List
    }

    private FigmaDTO mapFigmaToDTO(Figma figma) {
        String figmaURL = (figma != null) ? figma.getFigmaURL() : null;
        return new FigmaDTO(figmaURL);
    }

    private GoogleDriveDTO mapGoogleDriveToDTO(GoogleDrive googleDrive) {
        String driveLink = (googleDrive != null) ? googleDrive.getDriveLink() : null;
        return new GoogleDriveDTO(driveLink);
    }





    //-------------IUService-----------


    @Override
    public User getUserViaPhoneNumber(String phoneNumber) {
        logger.info("Getting user by phone number: {}", phoneNumber);
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if (user != null) {
            logger.info("User found for phone number: {}", phoneNumber);
        } else {
            logger.warn("User not found for phone number: {}", phoneNumber);
        }
        return user;
    }

    @Override
    public User getUserByMail(String userMail) {
        logger.info("Getting user by email: {}", userMail);
        User user = userRepository.findByEmail(userMail);
        if (user != null) {
            logger.info("User found for email: {}", userMail);
        } else {
            logger.warn("User not found for email: {}", userMail);
        }
        return user;
    }


}