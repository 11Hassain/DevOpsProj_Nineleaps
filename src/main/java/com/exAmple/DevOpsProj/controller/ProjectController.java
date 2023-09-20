package com.exAmple.DevOpsProj.controller;

import com.exAmple.DevOpsProj.commons.enumerations.EnumRole;
import com.exAmple.DevOpsProj.dto.responseDto.*;
import com.exAmple.DevOpsProj.model.GitRepository;
import com.exAmple.DevOpsProj.model.Project;
import com.exAmple.DevOpsProj.model.User;
import com.exAmple.DevOpsProj.model.UserNames;
import com.exAmple.DevOpsProj.repository.UserRepository;
import com.exAmple.DevOpsProj.service.GitHubCollaboratorService;
import com.exAmple.DevOpsProj.service.JwtService;
import com.exAmple.DevOpsProj.service.ProjectService;
import com.example.DevOpsProj.dto.responseDto.*;
import com.exAmple.DevOpsProj.exceptions.NotFoundException;
import com.example.DevOpsProj.model.*;
import com.exAmple.DevOpsProj.repository.GitRepositoryRepository;
import com.exAmple.DevOpsProj.repository.ProjectRepository;
import com.example.DevOpsProj.service.*;
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
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GitRepositoryRepository gitRepositoryRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private GitHubCollaboratorService collaboratorService;

    private static final String INVALID_TOKEN = "Invalid Token";


    @PostMapping("/") //Save the project
    public ResponseEntity<String> saveProject(@RequestBody ProjectDTO projectDTO,
                                              @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                projectService.saveProject(projectDTO);
                return ResponseEntity.ok("Project created successfully");

            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Project already exists");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createProject(@RequestBody ProjectDTO projectDTO,
                                                @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            ProjectDTO createdProjectDTO = projectService.createProject(projectDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProjectDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/{id}") //get project by id
    public ResponseEntity<Object> getProjectById(@PathVariable("id") Long id,
                                                 @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                Optional<Project> checkProject = projectService.getProjectById(id);
                if (checkProject.isPresent()) {
                    Project project = checkProject.get();
                    if (Boolean.TRUE.equals(project.getDeleted())) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                    ProjectDTO projectDTO = new ProjectDTO();
                    projectDTO.setProjectId(project.getProjectId());
                    projectDTO.setProjectName(project.getProjectName());
                    projectDTO.setProjectDescription(project.getProjectDescription());
                    return new ResponseEntity<>(projectDTO, HttpStatus.OK);
                }else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } catch (NotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/all") //retrieve list of all projects
    public ResponseEntity<Object> getAll(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                List<Project> projects = projectService.getAll();
                List<ProjectDTO> projectDTOs = projects.stream()
                        .map(project -> new ProjectDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), project.getLastUpdated(), project.getDeleted()))
                        .collect(Collectors.toList());
                return new ResponseEntity<>(projectDTOs, HttpStatus.OK);

            } catch (NotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/allProjects")
    public ResponseEntity<Object> getAllProjectsWithUsers(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
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
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }


    //get list of user in the project
    @GetMapping("/{projectId}/users")
    public ResponseEntity<Object> getAllUsersByProjectId(@PathVariable Long projectId,
                                                         @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                List<User> userList = projectService.getAllUsersByProjectId(projectId);
                if (userList == null) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
                List<UserDTO> userDTOList = userList.stream()
                        .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                        .collect(Collectors.toList());
                return ResponseEntity.ok(userDTOList);
            } catch (NotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/{projectId}/users/{role}")
    public ResponseEntity<Object> getAllUsersByProjectIdByRole(
            @PathVariable Long projectId,
            @PathVariable String role,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
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
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @PutMapping("/update/{projectId}") //update project
    public ResponseEntity<Object> updateProject(@PathVariable("projectId") Long projectId,
                                                @RequestBody ProjectDTO projectDTO,
                                                @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
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

    @DeleteMapping("/delete/{id}") //delete project (soft)
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id,
                                                @RequestHeader("AccessToken") String accessToken) {
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

    @PutMapping("/{projectId}/users/{userId}") //add user to project
    public ResponseEntity<Object> addUserToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
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

    @DeleteMapping("/{projectId}/users/{userId}") //remove user from the project
    public ResponseEntity<String> removeUserFromProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            //for checking if the project with given id exists
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            //for checking if the user with given id exists
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();
                project.getUsers().remove(user);
                projectRepository.save(project);


                return ResponseEntity.ok("User removed");
            } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @DeleteMapping("/{projectId}/users/{userId}/repo") //remove user from the project and repo as well
    public ResponseEntity<String> removeUserFromProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @RequestBody CollaboratorDTO collaboratorDTO,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            //for checking if the project with given id exists
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            //for checking if the user with given id exists
            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();
                project.getUsers().remove(user);
                projectRepository.save(project);


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


    @DeleteMapping("/{projectId}/users/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRoleFromUserInProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId, @PathVariable("roleId") Long roleId) {
        return null;
    }

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

    @PutMapping("/{projectId}/repository/{repoId}") // add repository to project
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

    @GetMapping("/without-figma-url")
    public ResponseEntity<Object> getProjectsWithoutFigmaURL(
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<ProjectDTO> projects = projectService.getProjectsWithoutFigmaURL();
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/without-google-drive")
    public ResponseEntity<Object> getProjectsWithoutGoogleDriveLink(
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<ProjectDTO> projects = projectService.getProjectsWithoutGoogleDriveLink();
            return ResponseEntity.ok(projects);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/countPeople")
    public ResponseEntity<Object> countAllPeopleByProjectIdAndName(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<ProjectNamePeopleCountDTO> peopleCountDTOs = projectService.getCountAllPeopleAndProjectName();
            if (peopleCountDTOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Empty");
            } else {
                return ResponseEntity.ok(peopleCountDTOs);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Object> countAllProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countProjects = projectService.getCountAllProjects();
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/count/role/{role}")
    public ResponseEntity<Object> countAllProjectsByRole(@PathVariable("role") String role,
                                                         @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            Integer countProjects = projectService.getCountAllProjectsByRole(enumRole);
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Object> countAllProjectsByUserId(@PathVariable("userId") Long id,
                                                           @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countProjects = projectService.getCountAllProjectsByUserId(id);
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/{projectId}/count")
    public ResponseEntity<Object> countAllUsersByProjectId(@PathVariable Long projectId,
                                                           @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Integer countUsers = projectService.getCountAllUsersByProjectId(projectId);
            if (countUsers == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countUsers);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

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

    @GetMapping("/{projectId}/details")
    public ResponseEntity<Object> getProjectDetailsById(@RequestHeader("AccessToken") String accessToken,
                                                        @PathVariable Long projectId) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if(isTokenValid){
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