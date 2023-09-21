package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responseDto.*;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.interfaces.JwtService;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final IUserService userService;
    private final GitRepositoryRepository gitRepositoryRepository;
    private final JwtService jwtService;
    private final GitHubCollaboratorService collaboratorService;

    private static final String INVALID_TOKEN = "Invalid Token";

        // Save a new project.
        @PostMapping("/")
        public ResponseEntity<String> saveProject(@RequestBody ProjectDTO projectDTO,
                                                  @RequestHeader("AccessToken") String accessToken) {
            // Check if the provided access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                try {
                    // Save the project and return a success response.
                    Project savedProject = projectService.saveProject(projectDTO);
                    return ResponseEntity.ok("Project created successfully");
                } catch (DataIntegrityViolationException e) {
                    // Handle the case where the project already exists.
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Project already exists");
                }
            } else {
                // Respond with an unauthorized status if the access token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Create a new project and return its details.
        @PostMapping("/create")
        public ResponseEntity<Object> createProject(@RequestBody ProjectDTO projectDTO,
                                                    @RequestHeader("AccessToken") String accessToken) {
            // Check if the provided access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Create the project and return its details in the response.
                ProjectDTO createdProjectDTO = projectService.createProject(projectDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdProjectDTO);
            } else {
                // Respond with an unauthorized status if the access token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get project details by ID.
        @GetMapping("/{id}")
        public ResponseEntity<Object> getProjectById(@PathVariable("id") Long id,
                                                     @RequestHeader("AccessToken") String accessToken) {
            // Check if the provided access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                try {
                    // Retrieve project details by ID and return them in the response.
                    Optional<Project> checkProject = projectService.getProjectById(id);
                    Project project = checkProject.get();
                    if (project.getDeleted()) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                    ProjectDTO projectDTO = new ProjectDTO();
                    projectDTO.setProjectId(project.getProjectId());
                    projectDTO.setProjectName(project.getProjectName());
                    projectDTO.setProjectDescription(project.getProjectDescription());
                    return new ResponseEntity<>(projectDTO, HttpStatus.OK);
                } catch (NotFoundException e) {
                    // Handle the case where the project is not found.
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } catch (IllegalArgumentException e) {
                    // Handle the case where the request has invalid parameters.
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } catch (Exception e) {
                    // Handle internal server errors.
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // Respond with an unauthorized status if the access token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get a list of all projects.
        @GetMapping("/all")
        public ResponseEntity<Object> getAll(@RequestHeader("AccessToken") String accessToken) {
            // Check if the provided access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                try {
                    // Retrieve a list of all projects and return them in the response.
                    List<Project> projects = projectService.getAll();
                    List<ProjectDTO> projectDTOs = projects.stream()
                            .map(project -> new ProjectDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), project.getLastUpdated(), project.getDeleted()))
                            .collect(Collectors.toList());
                    return new ResponseEntity<>(projectDTOs, HttpStatus.OK);
                } catch (NotFoundException e) {
                    // Handle the case where no projects are found.
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                    // Handle internal server errors.
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // Respond with an unauthorized status if the access token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get a list of all projects along with associated users.
        @GetMapping("/allProjects")
        public ResponseEntity<Object> getAllProjectsWithUsers(@RequestHeader("AccessToken") String accessToken) {
            // Check if the provided access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                try {
                    // Retrieve a list of all projects and associated users, and return them in the response.
                    List<Project> projects = projectService.getAllProjects();
                    List<ProjectWithUsersDTO> projectsWithUsers = new ArrayList<>();

                    for (Project project : projects) {
                        List<User> userList = projectService.getAllUsersByProjectId(project.getProjectId());
                        if (userList == null) {
                            userList = new ArrayList<>();
                        }
                        List<UserDTO> userDTOList = userList.stream()
                                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                                .collect(Collectors.toList());

                        ProjectWithUsersDTO projectWithUsers = new ProjectWithUsersDTO(
                                project.getProjectId(),
                                project.getProjectName(),
                                project.getProjectDescription(),
                                project.getLastUpdated(), // Assuming 'lastUpdated' is a field in Project class
                                userDTOList
                        );

                        projectsWithUsers.add(projectWithUsers);
                    }

                    return new ResponseEntity<>(projectsWithUsers, HttpStatus.OK);
                } catch (NotFoundException e) {
                    // Handle the case where no projects are found.
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                    // Handle internal server errors.
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // Respond with an unauthorized status if the access token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get a list of users in a specific project.
        @GetMapping("/{projectId}/users")
        public ResponseEntity<Object> getAllUsersByProjectId(@PathVariable Long projectId,
                                                             @RequestHeader("AccessToken") String accessToken) {
            // Check if the provided access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                try {
                    // Retrieve a list of users in the specified project and return them in the response.
                    List<User> userList = projectService.getAllUsersByProjectId(projectId);
                    if (userList == null) {
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    }
                    List<UserDTO> userDTOList = userList.stream()
                            .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(userDTOList);
                } catch (NotFoundException e) {
                    // Handle the case where no users are found.
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                    // Handle internal server errors.
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // Respond with an unauthorized status if the access token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get a list of users in a specific project by their role.
        @GetMapping("/{projectId}/users/{role}")
        public ResponseEntity<Object> getAllUsersByProjectIdByRole(
                @PathVariable Long projectId,
                @PathVariable String role,
                @RequestHeader("AccessToken") String accessToken) {
            // Check if the provided access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                try {
                    // Convert the role string to an EnumRole.
                    EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
                    // Retrieve a list of users in the specified project by their role and return them in the response.
                    List<User> userList = projectService.getAllUsersByProjectIdAndRole(projectId, enumRole);
                    if (userList == null) {
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    }

                    List<UserDTO> userDTOList = userList.stream()
                            .map(user -> {
                                UserNames usernames = user.getUserNames();
                                String username = (usernames != null) ? usernames.getUsername() : null;
                                return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), username);
                            })
                            .collect(Collectors.toList());

                    return ResponseEntity.ok(userDTOList);
                } catch (NoSuchElementException e) {
                    // Handle the case where no users are found.
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } catch (Exception e) {
                    // Handle internal server errors.
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                // Respond with an unauthorized status if the access token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }



    // Update an existing project.
    @PutMapping("/update/{projectId}")
    public ResponseEntity<Object> updateProject(@PathVariable("projectId") Long projectId,
                                                @RequestBody ProjectDTO projectDTO,
                                                @RequestHeader("AccessToken") String accessToken) {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                // Retrieve the existing project by ID and update its details.
                Optional<Project> optionalProject = projectService.getProjectById(projectId);
                if (optionalProject.isPresent()) {
                    Project existingProject = optionalProject.get();
                    existingProject.setProjectName(projectDTO.getProjectName());
                    existingProject.setProjectDescription(projectDTO.getProjectDescription());
                    existingProject.setLastUpdated(LocalDateTime.now());
                    Project updatedProject = projectService.updateProject(existingProject);
                    ProjectDTO updatedProjectDTO = new ProjectDTO(updatedProject.getProjectId(), updatedProject.getProjectName(), updatedProject.getProjectDescription(), updatedProject.getLastUpdated());
                    return new ResponseEntity<>(updatedProjectDTO, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Soft delete a project.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id,
                                                @RequestHeader("AccessToken") String accessToken) {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            if (projectService.existsProjectById(id)) {
                boolean checkIfDeleted = projectService.existsByIdIsDeleted(id);
                if (checkIfDeleted) {
                    return ResponseEntity.ok("Project doesn't exist");
                }
                boolean isDeleted = projectService.softDeleteProject(id);
                if (isDeleted) {
                    return ResponseEntity.ok("Deleted project successfully");
                } else {
                    return ResponseEntity.ok("404 Not Found");
                }
            } else return ResponseEntity.ok("Invalid project id");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Add a user to a project.
    @PutMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Object> addUserToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @RequestHeader("AccessToken") String accessToken) {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                // Retrieve the project and user by their IDs.
                Optional<Project> optionalProject = projectRepository.findById(projectId);
                Optional<User> optionalUser = userRepository.findById(userId);
                if (optionalProject.isPresent() && optionalUser.isPresent()) {
                    Project project = optionalProject.get();
                    User user = optionalUser.get();
                    if (projectService.existUserInProject(project.getProjectId(), user.getId())) {
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                    project.getUsers().add(user);
                    projectRepository.save(project);
                    List<UserDTO> userDTOList = project.getUsers().stream()
                            .map(users -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                            .collect(Collectors.toList());
                    ProjectUserDTO projectUserDTO = new ProjectUserDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), userDTOList);
                    return new ResponseEntity<>(projectUserDTO, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Remove a user from a project.
    @DeleteMapping("/{projectId}/users/{userId}")
    public ResponseEntity<String> removeUserFromProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @RequestHeader("AccessToken") String accessToken) {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            // Check if the project and user with the given IDs exist.
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();
                project.getUsers().remove(user);
                projectRepository.save(project);

                List<UserDTO> userDTOList = project.getUsers().stream()
                        .map(users -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                        .toList();
                ProjectUserDTO projectUserDTO = new ProjectUserDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), userDTOList);

                return ResponseEntity.ok("User removed");
            } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Remove a user from a project and repository as well.
    @DeleteMapping("/{projectId}/users/{userId}/repo")
    public ResponseEntity<String> removeUserFromProjectAndRepo(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @RequestBody CollaboratorDTO collaboratorDTO,
            @RequestHeader("AccessToken") String accessToken) {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            // Check if the project and user with the given IDs exist.
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();
                project.getUsers().remove(user);
                projectRepository.save(project);

                List<UserDTO> userDTOList = project.getUsers().stream()
                        .map(users -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                        .toList();
                ProjectUserDTO projectUserDTO = new ProjectUserDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), userDTOList);

                boolean deleted = collaboratorService.deleteCollaborator(collaboratorDTO);
                if (!deleted) {
                    return ResponseEntity.ok("Unable to remove user");
                }
                return ResponseEntity.ok("User removed");
            } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }




    // Remove a role from a user in a project.
    @DeleteMapping("/{projectId}/users/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRoleFromUserInProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId, @PathVariable("roleId") Long roleId) {
        // Implementation missing; You should implement this method to remove a role from a user in a project.
        return null;
    }

    // Get users by project ID and role.
    @GetMapping("/{projectId}/users/role/{role}")
    public ResponseEntity<Object> getUsersByProjectIdAndRole(
            @PathVariable("projectId") Long projectId,
            @PathVariable("role") String role,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
            List<User> users = projectService.getUsersByProjectIdAndRole(projectId, userRole);
            List<UserDTO> userDTOList = users.stream()
                    .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Add a repository to a project.
    @PutMapping("/{projectId}/repository/{repoId}")
    public ResponseEntity<Object> addRepositoryToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("repoId") Long repoId,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                Optional<Project> optionalProject = projectRepository.findById(projectId);
                Optional<GitRepository> optionalGitRepository = gitRepositoryRepository.findById(repoId);
                if (optionalProject.isPresent() && optionalGitRepository.isPresent()) {
                    Project project = optionalProject.get();
                    GitRepository gitRepository = optionalGitRepository.get();

                    // Check if the project has been deleted
                    if (Boolean.FALSE.equals(project.getDeleted())) {
                        gitRepository.setProject(project);
                    } else {
                        gitRepository.setProject(null);
                    }
                    gitRepositoryRepository.save(gitRepository);
                    return ResponseEntity.ok("Stored successfully");
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Get projects without a Figma URL.
    @GetMapping("/without-figma-url")
    public ResponseEntity<Object> getProjectsWithoutFigmaURL(
            @RequestHeader("AccessToken") String accessToken) {
        // Implementation missing; You should implement this method to retrieve projects without a Figma URL.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Get projects without a Google Drive link.
    @GetMapping("/without-google-drive")
    public ResponseEntity<Object> getProjectsWithoutGoogleDriveLink(
            @RequestHeader("AccessToken") String accessToken) {
        // Implementation missing; You should implement this method to retrieve projects without a Google Drive link.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Count all people by project ID and name.
    @GetMapping("/countPeople")
    public ResponseEntity<Object> countAllPeopleByProjectIdAndName(@RequestHeader("AccessToken") String accessToken) {
        // Implementation missing; You should implement this method to count all people by project ID and name.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Count all projects.
    @GetMapping("/count")
    public ResponseEntity<Object> countAllProjects(@RequestHeader("AccessToken") String accessToken) {
        // Implementation missing; You should implement this method to count all projects.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Count all projects by role.
    @GetMapping("/count/role/{role}")
    public ResponseEntity<Object> countAllProjectsByRole(@PathVariable("role") String role,
                                                         @RequestHeader("AccessToken") String accessToken) {
        // Implementation missing; You should implement this method to count all projects by role.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Count all projects by user ID.
    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Object> countAllProjectsByUserId(@PathVariable("userId") Long id,
                                                           @RequestHeader("AccessToken") String accessToken) {
        // Implementation missing; You should implement this method to count all projects by user ID.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Count all users by project ID.
    @GetMapping("/{projectId}/count")
    public ResponseEntity<Object> countAllUsersByProjectId(@PathVariable Long projectId,
                                                           @RequestHeader("AccessToken") String accessToken) {
        // Implementation missing; You should implement this method to count all users by project ID.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }



    // Count all users by project ID and role.
    @GetMapping("/{projectId}/count/{role}")
    public ResponseEntity<Object> countAllUsersByProjectIdByRole(
            @PathVariable Long projectId,
            @PathVariable String role,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            Integer countUsers = projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole);
            if (countUsers == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countUsers);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Count all active projects.
    @GetMapping("/count/active")
    public ResponseEntity<Object> countAllActiveProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countProjects = projectService.getCountAllActiveProjects();
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Count all inactive projects.
    @GetMapping("/count/inactive")
    public ResponseEntity<Object> countAllInActiveProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countProjects = projectService.getCountAllInActiveProjects();
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Get project details by ID.
    @GetMapping("/{projectId}/details")
    public ResponseEntity<Object> getProjectDetailsById(@RequestHeader("AccessToken") String accessToken,
                                                        @PathVariable Long projectId) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                ProjectDTO projectDetails = projectService.getProjectDetailsById(projectId);
                return new ResponseEntity<>(projectDetails, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
}