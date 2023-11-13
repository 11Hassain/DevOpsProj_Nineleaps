package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.model.User;
import com.example.devopsproj.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * The UserController class provides endpoints for managing user-related operations.
 * These operations include saving a user and finding a user by their user ID and other functionalities performed on users.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Save the user.
     *
     * @param userCreationDTO The DTO containing user creation information.
     * @return ResponseEntity containing the saved user or an error message.
     */
    @PostMapping("/") // Save the user
    @Operation(
            description = "Save the user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User saved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserCreationDTO userCreationDTO){
        logger.info("Received a request to save a user");

        UserDTO savedUser = userService.saveUser(userCreationDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * Find user by user id.
     *
     * @param userId The ID of the user to retrieve.
     * @return ResponseEntity containing the user or an error message.
     */
    @GetMapping("/{user_id}") // Find user by user id
    @Operation(
            description = "Find user by user id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getUserById(@PathVariable Long userId){
        logger.info("Received a request to find a user by user id: {}", userId);

        Optional<User> optionalUser = userService.getUserById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), user.getLastUpdated(), user.getLastLogout());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        else {
            logger.warn("User not found with id: {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update user by user id.
     *
     * @param id      The ID of the user to update.
     * @param userDTO The updated user data.
     * @return ResponseEntity containing the updated user or an error message.
     */
    @PutMapping("/update/{id}") // Update user by id
    @Operation(
            description = "Update user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id,
                                             @Valid @RequestBody UserDTO userDTO){
        logger.info("Received a request to update a user with id: {}", id);

        UserDTO userDTOs = userService.updateUser(id, userDTO);
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }

    /**
     * Soft-delete user by user id.
     *
     * @param userId The ID of the user to soft-delete.
     * @return ResponseEntity containing a success message or an error response.
     */
    @DeleteMapping("/delete/{user_id}") // Soft-deleting user
    @Operation(
            description = "Soft-delete user by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully soft-deleted"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId){
        logger.info("Received a request to soft-delete a user with id: {}", userId);

        if(userService.existsById(userId)) {
            logger.info("User with id: {} exists", userId);
            boolean checkIfDeleted = userService.existsByIdIsDeleted(userId); //check if deleted = true?
            if (checkIfDeleted) {
                logger.info("User Not Present");
                return ResponseEntity.ok("User doesn't exist");
                //user is present in db but deleted=true(soft deleted)
            }
            boolean isDeleted = userService.softDeleteUser(userId); //soft deletes user with id (yes/no)
            if(isDeleted){
                return ResponseEntity.ok("User successfully deleted");
                //successfully deleting user (soft delete) (user exists in db)
            }
            else{
                logger.info("User not found when performing soft-delete in service");
                return ResponseEntity.notFound().build();
                //gives 404 Not Found error response
            }
        }
        else return ResponseEntity.notFound().build();
    }

    /**
     * Get list of users by role.
     *
     * @param role The role by which to filter users.
     * @return ResponseEntity containing a list of users or an error resposne.
     */
    @GetMapping("/role/{role}") // Get list of users by role
    @Operation(
            description = "Get list of users by role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users by role found successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getUserByRoleId(@PathVariable("role") String role){
        logger.info("Received a request to get users by role: {}", role);

        EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); //getting value of role(string)
        List<User> users = userService.getUsersByRole(userRole);
        List<UserDTO> userDTOList = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), user.getLastUpdated(), user.getLastLogout()))
                .toList();
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    /**
     * Get count of all users.
     *
     * @return ResponseEntity containing the count of all users or an error response.
     */
    @GetMapping("/count") // Get count of all the users
    @Operation(
            description = "Get count of all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count of all users obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getCountAllUsers(){
        logger.info("Received a request to get the count of all users.");

        Integer countUsers = userService.getCountAllUsers();
        if (countUsers == 0){
            logger.info("No users");
            return ResponseEntity.ok(0);
        }
        else {
            return ResponseEntity.ok(countUsers);
        }
    }

    /**
     * Get count of users by role.
     *
     * @param role The role by which to filter user.
     * @return ResponseEntity containing the count of users filtered by role.
     */
    @GetMapping("/count/{role}") // Get count of users by role
    @Operation(
            description = "Get count of users by role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count of users by role obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getCountAllUsersByRole(@PathVariable String role){
        logger.info("Received a request to get the count of users by role.");

        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        Integer countUsersByRole = userService.getCountAllUsersByRole(userRole);
        if(countUsersByRole == 0){
            logger.info("No users present");
            return ResponseEntity.ok(0);
        }
        else {
            return ResponseEntity.ok(countUsersByRole);
        }
    }

    /**
     * Get count of users by project ID
     *
     * @param projectId The ID of the project in which the users are counted.
     * @return ResponseEntity containing the count of users in a project or error response.
     */
    @GetMapping("/count/project/{projectId}") // Get count of users by project ID
    @Operation(
            description = "Get count of users by project ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Count of users by project ID obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getCountAllUsersByProjectId(@PathVariable Long projectId){
        logger.info("Received a request to get count of users by project ID.");

        Integer countUsersByProject = userService.getCountAllUsersByProjectId(projectId);
        if (countUsersByProject == 0){
            logger.info("No users");
            return ResponseEntity.ok(0);
        }
        else {
            return ResponseEntity.ok(countUsersByProject);
        }
    }

    /**
     * Get all projects by user ID
     *
     * @param id The ID of the user whose projects are needed.
     * @return ResponseEntity containing the count of all the projects by user ID or error response.
     */
    @GetMapping("/{id}/projects")
    @Operation(
            description = "Get all projects by user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getAllProjectsByUserId(@PathVariable Long id) {
        logger.info("Received a request to get all the projects of a user.");
        List<ProjectDTO> projects = userService.getAllProjectsAndRepositoriesByUserId(id);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects by role and user ID
     *
     * @param userId The ID of the user whose projects are retrieved.
     * @param role The role of the user whose projects are retrieved.
     * @return ResponseEntity containing list of projects based on role and ID of user or error response.
     */
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
            @PathVariable("role") String role) {
        logger.info("Received a request to get projects by role and user ID.");

        ResponseEntity<Object> response = userService.getProjectsByRoleAndUserId(userId, role);

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok().body(response.getBody());
        } else {
            logger.info("No projects are found for the user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Projects Found");
        }
    }

    /**
     * Get all the users
     *
     * @return ResponseEntity containing a list of users or error response.
     */
    @GetMapping("/get")
    @Operation(
            description = "Get all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getAllUsers(){
        logger.info("Received a request to get a list of all the users.");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Get all user having at least one project
     *
     * @return ResponseEntity containing a list of users who have at least one project or an error response.
     */
    @GetMapping("/getAll")
    @Operation(
            description = "Get all users with projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users with projects obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getAllUsersWithProjects(){
        logger.info("Received a list of users having project.");

        List<UserProjectsDTO> userProjectsDTOs = userService.getAllUsersWithProjects();
        return ResponseEntity.ok(userProjectsDTOs);
    }

    /**
     * Get users having equal to or more than two projects
     *
     * @return ResponseEntity containing a list of users who have more than 1 project or an error response.
     */
    @GetMapping("/getMultiple")
    @Operation(
            description = "Get users with multiple projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users with multiple projects obtained successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<Object> getUsersWithMultipleProjects() {
        logger.info("Received a request to get a list of users have more than one project.");

        List<UserProjectsDTO> usersWithMultipleProjects = userService.getUsersWithMultipleProjects();
        return ResponseEntity.ok(usersWithMultipleProjects);
    }

    /**
     * Get users without a project
     *
     * @param role The role of user who is not assigned to any project.
     * @param projectId The ID of the project to check if the user is assigned to this project or not.
     * @return ResponseEntity containing a list of users without a project.
     */
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
            @RequestParam("projectId") Long projectId) {
        logger.info("Received a request to get a list of users without a project.");

        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        List<UserDTO> userDTOList = userService.getAllUsersWithoutProjects(enumRole, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(userDTOList);
    }

    /**
     * Update the last logout time of the user based on user's ID.
     *
     * @param id The ID of the user whose last updated date is saved.
     * @return ResponseEntity containing a string value that says whether logout is successful.
     */
    @PostMapping("/{userId}/logout")
    @Operation(
            description = "Logout a user by user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged out successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failed")
            }
    )
    public ResponseEntity<String> userLogout(@PathVariable("userId") Long id){
        logger.info("Received a request to logout the user.");
        String response = userService.userLogout(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
