package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.requestDto.UserCreationDTO;
import com.example.DevOpsProj.dto.responseDto.*;
import com.example.DevOpsProj.model.Figma;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.service.JwtService;
import com.example.DevOpsProj.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    private ModelMapper modelMapper;


    @PostMapping("/") //Save the user
    public ResponseEntity<Object> saveUser(@RequestBody UserCreationDTO userCreationDTO,
                                         @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            User savedUser = userService.saveUser(userCreationDTO);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/{user_id}") //find user by user id
    public ResponseEntity<Object> getUserById(@PathVariable Long user_id,
                                               @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<User> optionalUser = userService.getUserById(user_id);
            if(optionalUser.isPresent()){
                User user = optionalUser.get();
                UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole());
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @PutMapping("/update/{id}")//update user by id
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id,
                                             @RequestBody UserDTO userDTO,
                                             @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            UserDTO userDTOs = userService.updateUser(id, userDTO);
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @DeleteMapping("/delete/{user_id}") //soft-deleting user
    public ResponseEntity<String> deleteUserById(@PathVariable Long user_id,
                                                 @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            if(userService.existsById(user_id)) {
                boolean checkIfDeleted = userService.existsByIdIsDeleted(user_id); //check if deleted = true?
                if (checkIfDeleted) {
                    return ResponseEntity.ok("User doesn't exist");
                    //user is present in db but deleted=true(soft deleted)
                }
                boolean isDeleted = userService.softDeleteUser(user_id); //soft deletes user with id (yes/no)
                if(isDeleted){
                    return ResponseEntity.ok("User successfully deleted");
                    //successfully deleting user (soft delete) (user exists in db)
                }
                else{
                    return ResponseEntity.ok("404 Not found");
                    //gives 404 Not Found error response
                }
            }
            else return ResponseEntity.ok("Invalid user ID");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/role/{role}") //get list of user by role
    public ResponseEntity<Object> getUserByRoleId(@PathVariable("role") String role,
                                                       @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); //getting value of role(string)
            List<User> users = userService.getUsersByRole(userRole);
            List<UserDTO> userDTOList = users.stream()
                    .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/count") //get count of all the users
    public ResponseEntity<Object> getCountAllUsers(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countUsers = userService.getCountAllUsers();
            if (countUsers == 0){
                return ResponseEntity.ok(0);
            }
            else {
                return ResponseEntity.ok(countUsers);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/count/{role}")
    public ResponseEntity<Object> getCountAllUsersByRole(@PathVariable String role,
                                          @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
            Integer countUsersByRole = userService.getCountAllUsersByRole(userRole);
            if(countUsersByRole == 0){
                return ResponseEntity.ok(0);
            }
            else {
                return ResponseEntity.ok(countUsersByRole);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/count/project/{projectId}")
    public ResponseEntity<Object> getCountAllUsersByProjectId(@PathVariable Long projectId,
                                                              @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countUsersByProject = userService.getCountAllUsersByProjectId(projectId);
            if (countUsersByProject == 0){
                return ResponseEntity.ok(0);
            }
            else {
                return ResponseEntity.ok(countUsersByProject);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<Object> getAllProjectsByUserId(@PathVariable Long id,
                                                         @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<ProjectDTO> projects = userService.getAllProjectsAndRepositoriesByUserId(id);
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("{id}/role/{role}/projects") // Get list of project for particular employee
    public ResponseEntity<Object> getProjectsByRoleIdAndUserId(
            @PathVariable("id") Long userId,
            @PathVariable("role") String role,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); // Getting value of role(string)
            List<Project> projects = userService.getUsersByRoleAndUserId(userId, userRole);
            if (projects.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<ProjectDTO> projectDTOList = projects.stream()
                    .map(project -> {
                        List<GitRepositoryDTO> repositoryDTOList = project.getRepositories().stream()
                                .map(repository -> new GitRepositoryDTO( repository.getName(), repository.getDescription()))
                                .collect(Collectors.toList());
                        Figma figma = project.getFigma();
                        String figmaURL = figma != null ? figma.getFigmaURL() : null; // Retrieve the Figma URL
                        FigmaDTO figmaDTO = new FigmaDTO(figmaURL);
                        return new ProjectDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), project.getUsers(), repositoryDTOList, figmaDTO);
                    })
                    .collect(Collectors.toList());


            return new ResponseEntity<>(projectDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getAllUsers(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok(userService.getAllUsers());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllUsersWithProjects(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<UserProjectsDTO> userProjectsDTOs = userService.getAllUsersWithProjects();
            return ResponseEntity.ok(userProjectsDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/getMultiple")
    public ResponseEntity<Object> getUsersWithMultipleProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<UserProjectsDTO> usersWithMultipleProjects = userService.getUsersWithMultipleProjects();
            return ResponseEntity.ok(usersWithMultipleProjects);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }
}
