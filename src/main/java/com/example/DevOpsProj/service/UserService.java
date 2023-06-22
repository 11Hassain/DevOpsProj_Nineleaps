package com.example.DevOpsProj.service;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.requestDto.UserCreationDTO;
import com.example.DevOpsProj.dto.responseDto.GitRepositoryDTO;
import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.dto.responseDto.UserProjectsDTO;
import com.example.DevOpsProj.model.GitRepository;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.repository.UserRepository;
import com.example.DevOpsProj.utils.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JwtUtils jwtUtils;

    private ModelMapper modelMapper;


    //implementing user creation using DTO pattern
    public User saveUser(@RequestBody UserCreationDTO userCreationDTO){
        User user = new User();
        user.setId(userCreationDTO.getId());
        user.setName(userCreationDTO.getName());
        user.setEmail(userCreationDTO.getEmail());
        user.setEnumRole(userCreationDTO.getEnumRole());
        return userRepository.save(user);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO){
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setId(userDTO.getId());
            existingUser.setName(userDTO.getName());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setEnumRole(userDTO.getEnumRole());
//            User updatedUser = userService.updateUser(existingUser);
            User updatedUser = userRepository.save(existingUser);
            UserDTO userDTOs = new UserDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getEnumRole());
            return userDTOs;
        }
        else {
            throw new EntityNotFoundException("User not found" + id);
        }
    }

    //find user by user id
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }


    //this function says whether id is soft-deleted or not
    public boolean existsByIdIsDeleted(Long id){
        Optional<User> checkUser = userRepository.findById(id);
        User cuser = checkUser.get();
        return cuser.getDeleted(); //true if deleted=1, false otherwise
    }

    //Soft deleting the user
    public boolean softDeleteUser(Long id){
        try{
            userRepository.softDelete(id);
            return true; //setting deleted=1 / true
        }catch (Exception e){
            return false; //keeping deleted false
        }
    }

    //check if the user is present in db
    public boolean existsById(Long id){
        return userRepository.existsById(id);
    }

    //updating the user details
//    public User updateUser(User updateUser){
//        return userRepository.save(updateUser);
//    }

    //editing
    //get all user based on role id
    public List<User> getUsersByRole(EnumRole enumRole){
        return userRepository.findByRole(enumRole);
    }

    private UserDTO convertToUserDto(User user){
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    public User getUserByEmail(String userEmail){
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
        if (project.isPresent()){
            return userRepository.countAllUsersByProjectId(projectId);
        }
        else {
            return 0;
        }
    }

    public List<UserProjectsDTO> getAllUsersWithProjects() {
        List<User> users = userRepository.findAll();
        List<UserProjectsDTO> userProjectsDTOs = new ArrayList<>();

        for (User user : users) {
            List<Project> projects = user.getProjects();

            List<String> projectNames = projects.stream()
                    .map(Project::getProjectName)
                    .collect(Collectors.toList());

            UserProjectsDTO userProjectsDTO = new UserProjectsDTO(user.getId(), user.getName(), projectNames);
            userProjectsDTOs.add(userProjectsDTO);
        }
        return userProjectsDTOs;
    }

    public List<UserProjectsDTO> getUsersWithMultipleProjects() {
        List<UserProjectsDTO> allUsersWithProjects = getAllUsersWithProjects();
        List<UserProjectsDTO> usersWithMultipleProjects = new ArrayList<>();

        for (UserProjectsDTO userProjectsDTO : allUsersWithProjects) {
            if (userProjectsDTO.getProjectNames().size() > 1) {
                usersWithMultipleProjects.add(userProjectsDTO);
            }
        }
        return usersWithMultipleProjects;
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
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setEnumRole(user.getEnumRole());
        //generate token
        String jwtToken = jwtService.generateToken(user);
        jwtUtils.saveUserToken(user,jwtToken);
        userDTO.setToken(jwtToken);
        return userDTO;
    }

}
