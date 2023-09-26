package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.interfaces.JwtService;
import com.example.devopsproj.service.interfaces.ProjectService;
import com.example.devopsproj.utils.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final ProjectService projectService;

    private final JwtService jwtService;

    private final JwtUtils jwtUtils;

    private final ProjectRepository projectRepository;

    private final ModelMapper modelMapper;


    // Save a new user to the repository.
    public User saveUser(@RequestBody UserCreationDTO userCreationDTO) {
        // Create a new User instance.
        User user = new User();

        // Set user properties from the DTO.
        user.setId(userCreationDTO.getId());
        user.setName(userCreationDTO.getName());
        user.setEmail(userCreationDTO.getEmail());
        user.setEnumRole(userCreationDTO.getEnumRole());

        // Set the last updated and last logout timestamps.
        user.setLastUpdated(LocalDateTime.now());
        user.setLastLogout(LocalDateTime.now());

        // Save the user to the repository.
        return userRepository.save(user);
    }

    // Update an existing user's information.
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        // Attempt to find the user by ID.
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            // User found, update their information.
            User existingUser = optionalUser.get();
            existingUser.setName(userDTO.getName());
            existingUser.setEnumRole(userDTO.getEnumRole());
            existingUser.setLastUpdated(LocalDateTime.now());

            // Save the updated user to the repository.
            User updatedUser = userRepository.save(existingUser);

            // Create and return a UserDTO with the updated information.
            return new UserDTO(updatedUser.getName(), updatedUser.getEnumRole(), updatedUser.getLastUpdated());
        } else {
            // User not found, throw an exception.
            throw new EntityNotFoundException("User not found" + id);
        }
    }

    // Find a user by their user ID.
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Check if a user with the given ID has been soft-deleted.
    public boolean existsByIdIsDeleted(Long id) {
        Optional<User> checkUser = userRepository.findById(id);
        User cuser = checkUser.get();
        return cuser.getDeleted(); // true if deleted=1, false otherwise
    }

    // Soft delete a user by setting their deleted status to true.
    public boolean softDeleteUser(Long id) {
        try {
            userRepository.softDelete(id);
            return true; // Setting deleted=1 / true
        } catch (Exception e) {
            return false; // Keeping deleted false
        }
    }

    // Check if a user with the given ID exists.
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    // Get a list of users with a specific role.
    public List<User> getUsersByRole(EnumRole enumRole) {
        return userRepository.findByRole(enumRole);
    }

    // Convert a User object to a UserDTO.
    private UserDTO convertToUserDto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    // Find a user by their email address.
    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    // Get the count of all users.
    public Integer getCountAllUsers() {
        return userRepository.countAllUsers();
    }

    // Get the count of users with a specific role.
    public Integer getCountAllUsersByRole(EnumRole role) {
        return userRepository.countAllUsersByRole(role);
    }

    // Get the count of users associated with a specific project by project ID.
    public Integer getCountAllUsersByProjectId(Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent()) {
            return userRepository.countAllUsersByProjectId(projectId);
        } else {
            return 0;
        }
    }

    // Get a list of users with associated project names.
    public List<UserProjectsDTO> getAllUsersWithProjects() {
        List<User> users = userRepository.findAllUsers();
        List<UserProjectsDTO> userProjectsDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            // Remove any projects that are marked as deleted.
            List<Project> existingProjects = projects.stream()
                    .filter(project -> !project.getDeleted())
                    .collect(Collectors.toList());

            // Extract project names.
            List<String> projectNames = existingProjects.stream()
                    .map(Project::getProjectName)
                    .collect(Collectors.toList());

            // Create a UserProjectsDTO for the user.
            UserProjectsDTO userProjectsDTO = new UserProjectsDTO(user.getId(), user.getName(), projectNames);
            userProjectsDTOs.add(userProjectsDTO);
        }

        return userProjectsDTOs;
    }




    // Get a list of users with a specific role and not associated with a specific project.
    public List<UserDTO> getAllUsersWithoutProjects(EnumRole role, Long projectId) {
        List<User> users = userRepository.findAllUsersByRole(role);
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            // Check if the user is not associated with the specified project.
            if (projects.stream().noneMatch(project -> project.getProjectId() == projectId)) {
                UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole());
                userDTOs.add(userDTO);
            }
        }
        return userDTOs;
    }

    // Get a list of users with multiple associated projects.
    public List<UserProjectsDTO> getUsersWithMultipleProjects() {
        List<UserProjectsDTO> allUsersWithProjects = getAllUsersWithProjects();
        List<UserProjectsDTO> usersWithMultipleProjects = new ArrayList<>();

        for (UserProjectsDTO userProjectsDTO : allUsersWithProjects) {
            List<String> projectNames = userProjectsDTO.getProjectNames();
            List<String> validProjectNames = new ArrayList<>();

            for (String projectName : projectNames) {
                // Check if the project exists.
                if (projectExists(projectName)) {
                    validProjectNames.add(projectName);
                }
            }

            userProjectsDTO.setProjectNames(validProjectNames);

            if (validProjectNames.size() > 1) {
                usersWithMultipleProjects.add(userProjectsDTO);
            }
        }

        return usersWithMultipleProjects;
    }

    // Check if a project with the given name exists.
    private boolean projectExists(String projectName) {
        List<Project> projects = projectRepository.findAllProjects();
        return projects.stream()
                .anyMatch(project -> project.getProjectName().equals(projectName));
    }

    // Get a list of all users.
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
        return userDTOList;
    }

    // Get a list of all projects and repositories associated with a user by user ID.
    public List<ProjectDTO> getAllProjectsAndRepositoriesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Project> projects = user.getProjects();

        List<ProjectDTO> projectDTOs = new ArrayList<>();
        for (Project project : projects) {
            ProjectDTO projectDTO = modelMapper.map(project, ProjectDTO.class);
            List<GitRepository> repositories = project.getRepositories();
            List<GitRepositoryDTO> repositoryDTOs = new ArrayList<>();
            for (GitRepository repository : repositories) {
                GitRepositoryDTO repositoryDTO = modelMapper.map(repository, GitRepositoryDTO.class);
                repositoryDTOs.add(repositoryDTO);
            }
            projectDTO.setRepositories(repositoryDTOs);
            projectDTOs.add(projectDTO);
        }

        return projectDTOs;
    }

    // Get a list of projects with a specific role and associated with a user by user ID.
    public List<Project> getUsersByRoleAndUserId(Long userId, EnumRole userRole) {
        return userRepository.findByRoleAndUserId(userId, userRole);
    }

    // Verify user login and generate a JWT token.
    public UserDTO loginVerification(String email) {
        UserDTO userDTO = new UserDTO();
        User user = userRepository.existsByEmail(email);
        if (user == null) {
            return null;
        }
        user.setLastUpdated(LocalDateTime.now());
        userRepository.save(user);
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setEnumRole(user.getEnumRole());
        userDTO.setLastUpdated(user.getLastUpdated());

        // Generate a JWT token and save it.
        String jwtToken = jwtService.generateToken(user);
        jwtUtils.saveUserToken(user, jwtToken);
        userDTO.setToken(jwtToken);
        return userDTO;
    }

    // Log out a user by updating their last logout timestamp.
    public String userLogout(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLogout(LocalDateTime.now());
            userRepository.save(user);
            return "User logged out successfully";
        } else {
            return "Log out unsuccessful";
        }
    }


    //-------------IUService-----------

    @Override
    public User getUserViaPhoneNumber(String phoneNumber){
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public User getUserByMail(String userMai) {
        return userRepository.findByEmail(userMai);
    }


}
