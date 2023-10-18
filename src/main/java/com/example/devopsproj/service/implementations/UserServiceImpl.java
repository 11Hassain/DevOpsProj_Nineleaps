package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;

import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.interfaces.UserService;
import com.example.devopsproj.utils.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

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
    private final JwtUtils jwtUtils;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;


    //implementing user creation using DTO pattern
    @Override
    public User saveUser(@RequestBody UserCreationDTO userCreationDTO) {
        User user = new User();
        user.setId(userCreationDTO.getId());
        user.setName(userCreationDTO.getName());
        user.setEmail(userCreationDTO.getEmail());
        user.setEnumRole(userCreationDTO.getEnumRole());
        user.setLastUpdated(LocalDateTime.now());
        user.setLastLogout(LocalDateTime.now());
        return userRepository.save(user);
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
            return new UserDTO(updatedUser.getName(), updatedUser.getEnumRole(), updatedUser.getLastUpdated());
        } else {
            throw new EntityNotFoundException("User not found" + id);
        }
    }

    //find user by user id
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean existsByIdIsDeleted(Long id) {
        Optional<User> checkUser = userRepository.findById(id);
        if (checkUser.isEmpty()) {
            return true;
        }
        User cuser = checkUser.get();
        return cuser.getDeleted(); // true if deleted=1, false otherwise
    }

    //Soft deleting the user
    @Override
    public boolean softDeleteUser(Long id) {
        try {
            userRepository.softDelete(id);
            return true; //setting deleted=1 / true
        } catch (Exception e) {
            return false; //keeping deleted false
        }
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    //get all user based on role id
    @Override
    public List<User> getUsersByRole(EnumRole enumRole) {
        return userRepository.findByRole(enumRole);
    }

    @Override
    public List<UserDTO> getUserDTOsByRole(EnumRole role) {
        List<User> users = userRepository.findByEnumRole(role);

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
        return userRepository.countAllUsers();
    }

    @Override
    public Integer getCountAllUsersByRole(EnumRole role) {
        return userRepository.countAllUsersByRole(role);
    }

    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        Integer countUsers = projectRepository.countAllUsersByProjectId(projectId);
        // Handle the case where countUsers is null or 0
        return countUsers != null ? countUsers : 0;
    }

    @Override
    public List<UserProjectsDTO> getAllUsersWithProjects() {
        List<User> users = userRepository.findAllUsers();
        List<UserProjectsDTO> userProjectsDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            // Remove any projects that are marked as deleted
            List<Project> existingProjects = projects.stream()
                    .filter(project -> !project.getDeleted())
                    .toList(); // Replace 'toList()' with 'Stream.toList()'

            List<String> projectNames = existingProjects.stream()
                    .map(Project::getProjectName)
                    .toList(); // Replace 'toList()' with 'Stream.toList()'

            UserProjectsDTO userProjectsDTO = new UserProjectsDTO(user.getId(), user.getName(), projectNames);
            userProjectsDTOs.add(userProjectsDTO);
        }

        return userProjectsDTOs;
    }

    @Override
    public List<UserDTO> getAllUsersWithoutProjects(EnumRole role, Long projectId) {
        List<User> users = userRepository.findAllUsersByRole(role);
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
        List<UserProjectsDTO> usersWithMultipleProjects = new ArrayList<>();

        for (UserProjectsDTO userProjectsDTO : allUsersWithProjects) {
            List<String> projectNames = userProjectsDTO.getProjectNames();
            List<String> validProjectNames = new ArrayList<>();

            // Check if each project exists in the database
            for (String projectName : projectNames) {
                if (projectExists(projectName)) {
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

        return usersWithMultipleProjects;
    }

    @Override
    public boolean projectExists(String projectName) {
        List<Project> projects = projectRepository.findAllProjects();
        return projects.stream()
                .anyMatch(project -> project.getProjectName().equals(projectName));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
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
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return "Invalid user ID";
        }
        User user = userOptional.get();
        if (Boolean.TRUE.equals(user.getDeleted())) {
            return "User doesn't exist";
        }

        if (softDeleteUser(userId)) {
            return "User successfully deleted";
        } else {
            return "404 Not found";
        }
    }


    @Override
    public List<Project> getUsersByRoleAndUserId(Long userId, EnumRole userRole) {
        return userRepository.findByRoleAndUserId(userId, userRole);
    }

    @Override
    public UserDTO loginVerification(String email) {
        UserDTO userDTO = new UserDTO();
        User user = userRepository.existsByEmail(email);
        if(user == null){
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
        jwtUtils.saveUserToken(user,jwtToken);
        userDTO.setToken(jwtToken);
        return userDTO;
    }



    @Override
    public String userLogout(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setLastLogout(LocalDateTime.now());
            userRepository.save(user);
            return "User logged out successfully";
        }else{
            return "Log out unsuccessful";
        }
    }



    @Override
    public List<ProjectDTO> getProjectsByRoleIdAndUserId(Long userId, String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); // Getting the value of role (string)
        List<Project> projects = getUsersByRoleAndUserId(userId, userRole);

        return projects.stream()
                .map(project -> {
                    List<GitRepositoryDTO> repositoryDTOList = project.getRepositories().stream()
                            .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
                            .toList(); // Replace 'Stream.collect(Collectors.toList())' with 'Stream.toList()'

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
                .toList(); // Replace 'Stream.collect(Collectors.toList())' with 'Stream.toList()'
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