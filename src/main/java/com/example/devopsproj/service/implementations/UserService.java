package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestDto.UserCreationDTO;
import com.example.devopsproj.dto.responseDto.*;
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
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }


    //this function says whether id is soft-deleted or not
    public boolean existsByIdIsDeleted(Long id) {
        Optional<User> checkUser = userRepository.findById(id);
        User cuser = checkUser.get();
        return cuser.getDeleted(); //true if deleted=1, false otherwise
    }

    //Soft deleting the user
    public boolean softDeleteUser(Long id) {
        try {
            userRepository.softDelete(id);
            return true; //setting deleted=1 / true
        } catch (Exception e) {
            return false; //keeping deleted false
        }
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    //editing
    //get all user based on role id
    public List<User> getUsersByRole(EnumRole enumRole) {
        return userRepository.findByRole(enumRole);
    }

    private UserDTO convertToUserDto(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    public Integer getCountAllUsers() {
        return userRepository.countAllUsers();
    }

    public Integer getCountAllUsersByRole(EnumRole role) {
        return userRepository.countAllUsersByRole(role);
    }

    public Integer getCountAllUsersByProjectId(Long projectId) {
        Optional<Project> project = projectService.getProjectById(projectId);
        if (project.isPresent()) {
            return userRepository.countAllUsersByProjectId(projectId);
        } else {
            return 0;
        }
    }

    public List<UserProjectsDTO> getAllUsersWithProjects() {
        List<User> users = userRepository.findAllUsers();
        List<UserProjectsDTO> userProjectsDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            // Remove any projects that are marked as deleted
            List<Project> existingProjects = projects.stream()
                    .filter(project -> !project.getDeleted())
                    .collect(Collectors.toList());

            List<String> projectNames = existingProjects.stream()
                    .map(Project::getProjectName)
                    .collect(Collectors.toList());

            UserProjectsDTO userProjectsDTO = new UserProjectsDTO(user.getId(), user.getName(), projectNames);
            userProjectsDTOs.add(userProjectsDTO);
        }

        return userProjectsDTOs;
    }



    public List<UserDTO> getAllUsersWithoutProjects(EnumRole role, Long projectId) {
        List<User> users = userRepository.findAllUsersByRole(role);
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            if (projects.stream().noneMatch(project -> project.getProjectId() == projectId)) {
                UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole());
                userDTOs.add(userDTO);
            }
        }
        return userDTOs;
    }

    public List<UserProjectsDTO> getUsersWithMultipleProjects() {
        List<UserProjectsDTO> allUsersWithProjects = getAllUsersWithProjects();
        List<UserProjectsDTO> usersWithMultipleProjects = new ArrayList<>();

        for (UserProjectsDTO userProjectsDTO : allUsersWithProjects) {
            List<String> projectNames = userProjectsDTO.getProjectNames();
            List<String> validProjectNames = new ArrayList<>();

            for (String projectName : projectNames) {
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

    private boolean projectExists(String projectName) {
        List<Project> projects = projectRepository.findAllProjects();
        return projects.stream()
                .anyMatch(project -> project.getProjectName().equals(projectName));
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .toList();
        return userDTOList;
    }

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

    public List<Project> getUsersByRoleAndUserId(Long userId, EnumRole userRole) {
        return userRepository.findByRoleAndUserId(userId, userRole);
    }

    public UserDTO loginVerification(String email){
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
        String jwtToken = jwtService.generateToken(user);
        jwtUtils.saveUserToken(user,jwtToken);
        userDTO.setToken(jwtToken);
        return userDTO;
    }

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
