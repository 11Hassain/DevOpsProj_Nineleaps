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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/projects")
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
//        try{
            Project savedProject = projectService.saveProject(projectDTO);
            return ResponseEntity.ok("Project created successfully");
//
//        }catch (DataIntegrityViolationException e){
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project already exists");
//        }catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to create project");
//        }
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO){
        ProjectDTO createdProjectDTO = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProjectDTO);
    }

    @GetMapping("/{id}") //get project by id
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable("id") Long id){
        try{
            Optional<Project> checkProject = projectService.getProjectById(id);
            Project project = checkProject.get();
            if(project.getDeleted()==true){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(project.getProjectId());
            projectDTO.setProjectName(project.getProjectName());
            projectDTO.setProjectDescription(project.getProjectDescription());
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
                if(projectService.existUserInProject(project.getProjectId(), user.getId())){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
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

}
