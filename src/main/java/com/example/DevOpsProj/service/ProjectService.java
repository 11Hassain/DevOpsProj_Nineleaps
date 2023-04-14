package com.example.DevOpsProj.service;

import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.repository.ProjectRepository;
import com.example.DevOpsProj.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper;



    //implementing DTO pattern for project for saving project
    public Project saveProject(ProjectDTO projectDTO){ //save project
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(Long id){
        return projectRepository.findById(id);
    }

    public List<Project> getAllProjects(){
        return projectRepository.findAllProjects();
    }

    public Project updateProject(Project updatedProject){
        return projectRepository.save(updatedProject);
    }


    //get all users in the project based on project id
    public List<User> getAllUsersByProjectId(Long projectId){
        List<User> users = projectRepository.findAllUsersByProjectId(projectId);
        if(users.isEmpty()){
            return null;
        }
        else {
            return projectRepository.findAllUsersByProjectId(projectId);
        }
    }

    //to check if the project has already been deleted
    public boolean existsByIdIsDeleted(Long id){
        Optional<Project> checkProject = projectRepository.findById(id);
        Project cproject = checkProject.get();
        return cproject.getDeleted(); //true if deleted=1, else false
    }

    //soft deleting project
    public boolean softDeleteProject(Long id){
        try{
            projectRepository.softDeleteProject(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    public boolean existsProjectById(Long id){
        return projectRepository.existsById(id);
    }

    public boolean existUserInProject(Long projectId, Long userId){
        List<User> userList = projectRepository.existUserInProject(projectId, userId);
        if(userList.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }


}
