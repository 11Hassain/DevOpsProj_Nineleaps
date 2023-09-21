package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responseDto.*;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.dto.requestDto.UserCreationDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.service.JwtService;
import com.example.devopsproj.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/") // Save the user
    @Operation(
            description = "Save the user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User saved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveUser(@RequestBody UserCreationDTO userCreationDTO,
                                         @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            User savedUser = userService.saveUser(userCreationDTO);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/{user_id}") // Find user by user id
    @Operation(
            description = "Find user by user id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getUserById(@PathVariable Long userId,
                                               @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<User> optionalUser = userService.getUserById(userId);
            if(optionalUser.isPresent()){
                User user = optionalUser.get();
                UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), user.getLastUpdated(), user.getLastLogout());
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @PutMapping("/update/{id}") // Update user by id
    @Operation(
            description = "Update user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id,
                                             @RequestBody UserDTO userDTO,
                                             @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            UserDTO userDTOs = userService.updateUser(id, userDTO);
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @DeleteMapping("/delete/{user_id}") // Soft-deleting user
    @Operation(
            description = "Soft-delete user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully soft-deleted"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId,
                                                 @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            if(userService.existsById(userId)) {
                boolean checkIfDeleted = userService.existsByIdIsDeleted(userId); //check if deleted = true?
                if (checkIfDeleted) {
                    return ResponseEntity.ok("User doesn't exist");
                    //user is present in db but deleted=true(soft deleted)
                }
                boolean isDeleted = userService.softDeleteUser(userId); //soft deletes user with id (yes/no)
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/role/{role}") // Get list of users by role
    @Operation(
            description = "Get list of users by role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users by role found successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getUserByRoleId(@PathVariable("role") String role,
                                                       @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); //getting value of role(string)
            List<User> users = userService.getUsersByRole(userRole);
            List<UserDTO> userDTOList = users.stream()
                    .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), user.getLastUpdated(), user.getLastLogout()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/count") // Get count of all the users
    @Operation(
            description = "Get count of all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count of all users obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/count/{role}") // Get count of users by role
    @Operation(
            description = "Get count of users by role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count of users by role obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/count/project/{projectId}") // Get count of users by project ID
    @Operation(
            description = "Get count of users by project ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count of users by project ID obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/{id}/projects")
    @Operation(
            description = "Get all projects by user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getAllProjectsByUserId(@PathVariable Long id,
                                                         @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<ProjectDTO> projects = userService.getAllProjectsAndRepositoriesByUserId(id);
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("{id}/role/{role}/projects")
    @Operation(
            description = "Get projects by role and user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects obtained successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - No projects found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
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
                    .collect(Collectors.toList());
            return new ResponseEntity<>(projectDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/get")
    @Operation(
            description = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getAllUsers(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok(userService.getAllUsers());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/getAll")
    @Operation(
            description = "Get all users with projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users with projects obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getAllUsersWithProjects(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<UserProjectsDTO> userProjectsDTOs = userService.getAllUsersWithProjects();
            return ResponseEntity.ok(userProjectsDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/getMultiple")
    @Operation(
            description = "Get users with multiple projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users with multiple projects obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getUsersWithMultipleProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<UserProjectsDTO> usersWithMultipleProjects = userService.getUsersWithMultipleProjects();
            return ResponseEntity.ok(usersWithMultipleProjects);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
    @GetMapping("/withoutProject")
    @Operation(
            description = "Get users without a project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users without a project obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getUserWithoutProject(
            @RequestParam("role") String role,
            @RequestParam("projectId") Long projectId,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            List<UserDTO> userDTOList = userService.getAllUsersWithoutProjects(enumRole, projectId);
            return ResponseEntity.status(HttpStatus.OK).body(userDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @PostMapping("/{userId}/logout")
    @Operation(
            description = "Logout a user by user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged out successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<String> userLogout(@PathVariable("userId") Long id,
                                             @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            String response = userService.userLogout(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
}
