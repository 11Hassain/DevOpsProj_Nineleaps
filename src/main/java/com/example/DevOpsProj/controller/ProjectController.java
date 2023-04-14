package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.dto.responseDto.ProjectUserDTO;
import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.exceptions.NotFoundException;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.repository.ProjectRepository;
import com.example.DevOpsProj.repository.UserRepository;
import com.example.DevOpsProj.service.ProjectService;
import com.example.DevOpsProj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/") //Save the project
    public ResponseEntity<Object> saveProject(@RequestBody ProjectDTO projectDTO){
        try{
//            if(!projectService.existsByIdIsDeleted(projectDTO.getProjectId())){
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("Project with the given ID already exists");
//            }
            Project savedProject = projectService.saveProject(projectDTO);
            return ResponseEntity.ok("Project created successfully");

        }catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project already exists");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create project");
        }
    }

    @GetMapping("/{id}") //get project by id
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable("id") Long id){
        try{
            Optional<Project> checkProject = projectService.getProjectById(id);
            Project project = checkProject.get();
            if(project.getDeleted()==true){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            ProjectDTO projectDTO = new ProjectDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription());
            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        }catch (NotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/allProjects") //retrieve list of projects
    public ResponseEntity<List<ProjectDTO>> getAllProjects(){
        try{
            List<Project> projects = projectService.getAllProjects();
            List<ProjectDTO> projectDTOs = projects.stream()
                    .map(project -> new ProjectDTO(project.getProjectId(),project.getProjectName(),project.getProjectDescription()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(projectDTOs, HttpStatus.OK);

        }catch (NotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //get list of user in the project
    @GetMapping("/{projectId}/users")
    public ResponseEntity<List<UserDTO>> getAllUsersByProjectId(@PathVariable Long projectId){
        try{
            List<User> userList = projectService.getAllUsersByProjectId(projectId);
            List<UserDTO> userDTOList = userList.stream()
                    .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDTOList);
        }catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{projectId}") //update project
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable("projectId") Long projectId, @RequestBody ProjectDTO projectDTO){
        try {
            Optional<Project> optionalProject = projectService.getProjectById(projectId);
            if (optionalProject.isPresent()) {
                Project existingProject = optionalProject.get();
//                existingProject.setProjectId(projectDTO.getProjectId());
                existingProject.setProjectName(projectDTO.getProjectName());
                existingProject.setProjectDescription(projectDTO.getProjectDescription());
                Project updatedProject = projectService.updateProject(existingProject);
                ProjectDTO updatedProjectDTO = new ProjectDTO(updatedProject.getProjectId(), updatedProject.getProjectName(), updatedProject.getProjectDescription());
                return new ResponseEntity<>(updatedProjectDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}") //delete project (soft)
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id){
        if(projectService.existsProjectById(id)){
            boolean checkIfDeleted = projectService.existsByIdIsDeleted(id);
            if(checkIfDeleted){
                return ResponseEntity.ok("Project doesn't exist");
            }
            boolean isDeleted = projectService.softDeleteProject(id);
            if(isDeleted){
                return ResponseEntity.ok("Deleted project successfully");
            }
            else{
                return ResponseEntity.ok("404 Not Found");
            }
        }
        else return ResponseEntity.ok("Invalid project id");


    }

    @PutMapping("/{projectId}/users/{userId}") //add user to project
    public ResponseEntity<ProjectUserDTO> addUserToProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        try {
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalProject.isPresent() && optionalUser.isPresent()) {
                Project project = optionalProject.get();
                User user = optionalUser.get();
                System.out.println("1");
                if(projectService.existUserInProject(project.getProjectId(), user.getId())){
                    System.out.println("2");
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                System.out.println("3");
                project.getUsers().add(user);
                projectRepository.save(project);
                List<UserDTO> userDTOList = project.getUsers().stream()
                        .map(users -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                        .toList();
                ProjectUserDTO projectUserDTO = new ProjectUserDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), userDTOList);
                return new ResponseEntity<>(projectUserDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{projectId}/users/{userId}") //remove user from the project
    public ResponseEntity<String> removeUserFromProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        //for checking if the project with given id exists
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        //for checking if the user with given id exists
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalProject.isPresent() && optionalUser.isPresent()){
            Project project = optionalProject.get();
            User user = optionalUser.get();
            project.getUsers().remove(user);
            projectRepository.save(project);
            List<UserDTO> userDTOList = project.getUsers().stream()
                    .map(users -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                    .toList();
            ProjectUserDTO projectUserDTO = new ProjectUserDTO(project.getProjectId(), project.getProjectName(), project.getProjectDescription(), userDTOList);
            return ResponseEntity.ok("User removed");
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
    }

//    @PutMapping("/{projectId}/users/{userId}/roles/{role}") //assign role to user in the project using their ids
//    public ResponseEntity<Object> assignRoleToUserInProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId, @PathVariable("role") String role){
//        try{
//            projectService.assignRoleToUserInProject(projectId,userId,role);
//            return ResponseEntity.ok("Role assigned");
//        }catch (IllegalArgumentException e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while assigning role");
//        }
//    }

    @DeleteMapping("/{projectId}/users/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRoleFromUserInProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId, @PathVariable("roleId") Long roleId){
        //check if the project with given id exists
        //check if the user with given id exists
        //check if the role with given id exists
        //remove role from user in the project in the database
        //return success/failure response
        return null;
    }

}
