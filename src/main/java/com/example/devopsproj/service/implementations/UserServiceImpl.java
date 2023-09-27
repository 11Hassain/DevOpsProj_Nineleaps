package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.dto.responsedto.UserProjectsDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.otp.otpservice.IUserService;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.UserService;
import com.example.devopsproj.utils.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService, UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectServiceImpl projectServiceImpl;
    @Autowired
    private JwtServiceImpl jwtServiceImpl;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ProjectRepository projectRepository;

    private ModelMapper modelMapper;


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

    //this function says whether id is soft-deleted or not
    @Override
    public boolean existsByIdIsDeleted(Long id) {
        Optional<User> checkUser = userRepository.findById(id);
        if (checkUser.isEmpty()){
            return true;
        }
        User cuser = checkUser.get();
        return cuser.getDeleted(); //true if deleted=1, false otherwise
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
    public Integer getCountAllUsers() {
        return userRepository.countAllUsers();
    }

    @Override
    public Integer getCountAllUsersByRole(EnumRole role) {
        return userRepository.countAllUsersByRole(role);
    }

    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        Optional<Project> project = projectServiceImpl.getProjectById(projectId);
        if (project.isPresent()) {
            return userRepository.countAllUsersByProjectId(projectId);
        } else {
            return 0;
        }
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
                    .toList();

            List<String> projectNames = existingProjects.stream()
                    .map(Project::getProjectName)
                    .collect(Collectors.toList());

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

    @Override
    public List<Project> getUsersByRoleAndUserId(Long userId, EnumRole userRole) {
        return userRepository.findByRoleAndUserId(userId, userRole);
    }

    @Override
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
