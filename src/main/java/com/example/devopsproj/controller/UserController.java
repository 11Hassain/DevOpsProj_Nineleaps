package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestDto.UserCreationDTO;
import com.example.devopsproj.dto.responseDto.*;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

    // Endpoint to save a new user.
    @PostMapping("/")
    public ResponseEntity<Object> saveUser(@RequestBody UserCreationDTO userCreationDTO,
                                           @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            User savedUser = userService.saveUser(userCreationDTO);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to find a user by user id.
    @GetMapping("/{user_id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId,
                                              @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<User> optionalUser = userService.getUserById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), user.getLastUpdated(), user.getLastLogout());
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to update user by id.
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id,
                                             @RequestBody UserDTO userDTO,
                                             @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            UserDTO updatedUserDTO = userService.updateUser(id, userDTO);
            return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to soft-delete a user by user id.
    @DeleteMapping("/delete/{user_id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId,
                                                 @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            if (userService.existsById(userId)) {
                boolean checkIfDeleted = userService.existsByIdIsDeleted(userId); // Check if user is soft-deleted
                if (checkIfDeleted) {
                    return ResponseEntity.ok("User doesn't exist");
                }
                boolean isDeleted = userService.softDeleteUser(userId); // Soft delete the user
                if (isDeleted) {
                    return ResponseEntity.ok("User successfully deleted");
                } else {
                    return ResponseEntity.ok("404 Not found");
                }
            } else {
                return ResponseEntity.ok("Invalid user ID");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to get a list of users by role.
    @GetMapping("/role/{role}")
    public ResponseEntity<Object> getUserByRoleId(@PathVariable("role") String role,
                                                  @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); // Get the role value from the URL
            List<User> users = userService.getUsersByRole(userRole);
            List<UserDTO> userDTOList = users.stream()
                    .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), user.getLastUpdated(), user.getLastLogout()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to get the count of all users.
    @GetMapping("/count")
    public ResponseEntity<Object> getCountAllUsers(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countUsers = userService.getCountAllUsers();
            if (countUsers == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countUsers);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }




    // Endpoint to get the count of users by role.
    @GetMapping("/count/{role}")
    public ResponseEntity<Object> getCountAllUsersByRole(@PathVariable String role,
                                                         @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); // Convert role to EnumRole
            Integer countUsersByRole = userService.getCountAllUsersByRole(userRole);
            if (countUsersByRole == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countUsersByRole);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to get the count of users by project ID.
    @GetMapping("/count/project/{projectId}")
    public ResponseEntity<Object> getCountAllUsersByProjectId(@PathVariable Long projectId,
                                                              @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countUsersByProject = userService.getCountAllUsersByProjectId(projectId);
            if (countUsersByProject == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countUsersByProject);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to get all projects for a specific user by user ID.
    @GetMapping("/{id}/projects")
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

    // Endpoint to get a list of projects for a specific employee by role and user ID.
    @GetMapping("{id}/role/{role}/projects")
    public ResponseEntity<Object> getProjectsByRoleIdAndUserId(
            @PathVariable("id") Long userId,
            @PathVariable("role") String role,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); // Convert role to EnumRole
            List<Project> projects = userService.getUsersByRoleAndUserId(userId, userRole);
            if (projects.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<ProjectDTO> projectDTOList = projects.stream()
                    .map(project -> {
                        // Create GitRepositoryDTO objects for each repository in the project
                        List<GitRepositoryDTO> repositoryDTOList = project.getRepositories().stream()
                                .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
                                .collect(Collectors.toList());

                        // Retrieve Figma URL and create FigmaDTO
                        Figma figma = project.getFigma();
                        String figmaURL = figma != null ? figma.getFigmaURL() : null;
                        FigmaDTO figmaDTO = new FigmaDTO(figmaURL);

                        // Retrieve Google Drive link and create GoogleDriveDTO
                        GoogleDrive googleDrive = project.getGoogleDrive();
                        String driveLink = googleDrive != null ? googleDrive.getDriveLink() : null;
                        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(driveLink);

                        // Create and return ProjectDTO
                        return new ProjectDTO(
                                project.getProjectId(),
                                project.getProjectName(),
                                project.getProjectDescription(),
                                repositoryDTOList,
                                figmaDTO,
                                googleDriveDTO
                        );
                    })
                    .collect(Collectors.toList());
            return new ResponseEntity<>(projectDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }


    // Endpoint to get all users.
    @GetMapping("/get")
    public ResponseEntity<Object> getAllUsers(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok(userService.getAllUsers());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to get all users with their associated projects.
    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllUsersWithProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<UserProjectsDTO> userProjectsDTOs = userService.getAllUsersWithProjects();
            return ResponseEntity.ok(userProjectsDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to get users with multiple projects.
    @GetMapping("/getMultiple")
    public ResponseEntity<Object> getUsersWithMultipleProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<UserProjectsDTO> usersWithMultipleProjects = userService.getUsersWithMultipleProjects();
            return ResponseEntity.ok(usersWithMultipleProjects);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to get users without projects by role and project ID.
    @GetMapping("/withoutProject")
    public ResponseEntity<Object> getUserWithoutProject(
            @RequestParam("role") String role,
            @RequestParam("projectId") Long projectId,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase()); // Convert role to EnumRole
            List<UserDTO> userDTOList = userService.getAllUsersWithoutProjects(enumRole, projectId);
            return ResponseEntity.status(HttpStatus.OK).body(userDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Endpoint to log out a user by user ID.
    @PostMapping("/{userId}/logout")
    public ResponseEntity<String> userLogout(@PathVariable("userId") Long id,
                                             @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            String response = userService.userLogout(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
}
