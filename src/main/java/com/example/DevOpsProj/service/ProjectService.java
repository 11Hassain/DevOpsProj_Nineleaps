package com.example.DevOpsProj.service;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.dto.responseDto.RepositoryDTO;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.Repository;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.repository.ProjectRepository;
import com.example.DevOpsProj.repository.RepositoryRepository;
import com.example.DevOpsProj.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RepositoryRepository repositoryRepository;


    //implementing DTO pattern for project for saving project
    public Project saveProject(ProjectDTO projectDTO){ //save project
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());
        List<User> users = projectDTO.getUsers().stream()
                .map(userDTO -> modelMapper.map(userDTO, User.class))
                .collect(Collectors.toList());
        project.setUsers(users);
        return projectRepository.save(project);
    }


    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
//        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());

//        List<Repository> repositories = new ArrayList<>();
//        for (RepositoryDTO repositoryDTO: projectDTO.getRepositories()){
//            Repository repository = new Repository();
//            repository.setName(repositoryDTO.getName());
//            repository.setProject(project);
//            repositories.add(repository);
//        }


//        List<RepositoryDTO> repositoryDTOs = projectDTO.getRepositories();
//        List<Repository> repositories = new ArrayList<>();
//        for (RepositoryDTO repositoryDTO : repositoryDTOs) {
//            Optional<Repository> optionalRepository = repositoryRepository.findById(repositoryDTO.getId());
//            if (optionalRepository.isPresent()) {
//                Repository repository = optionalRepository.get();
//                repositories.add(repository);
//            } else {
//                throw new RuntimeException("Repository not found for ID: " + repositoryDTO.getId());
//            }
//        }
//        System.out.println(project);
//        System.out.println(repositoryDTOs);
//        System.out.println(repositories);
//        project.setRepositories(repositories);
//        projectRepository.save(project);
//
//        return modelMapper.map(project, ProjectDTO.class);
//    }


        List<RepositoryDTO> repositoryDTOs = projectDTO.getRepositories();
        List<Repository> repositories = repositoryDTOs.stream()
                .map(repositoryDTO -> modelMapper.map(repositoryDTO, Repository.class))
                .collect(Collectors.toList());
        project.setRepositories(repositories);
        project.setLastUpdated(LocalDateTime.now());
        projectRepository.save(project);

        return modelMapper.map(project, ProjectDTO.class);
    }

    public Optional<Project> getProjectById(Long id){
        return projectRepository.findById(id);
    }

    public List<Project> getAll(){
        return projectRepository.findAll();
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
            return users;
        }
    }

    public List<User> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role){
        List<User> users = projectRepository.findAllUsersByProjectIdAndRole(projectId, role);
        if(users.isEmpty()){
            return null;
        }
        else {
            return users;
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

    public Integer getCountAllProjects(){
        return projectRepository.countAllProjects();
    }

    public Integer getCountAllProjectsByRole(EnumRole enumRole) {
        return projectRepository.countAllProjectsByRole(enumRole);
    }

    public Integer getCountAllProjectsByUserId(Long id) {
        return projectRepository.countAllProjectsByUserId(id);
    }

    public Integer getCountAllUsersByProjectId(Long projectId) {
        return projectRepository.countAllUsersByProjectId(projectId);
    }

    public Integer getCountAllUsersByProjectIdAndRole(Long projectId, EnumRole enumRole) {
        return projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole);
    }

    public Integer getCountAllActiveProjects(){
        return projectRepository.countAllActiveProjects();
    }

    public Integer getCountAllInActiveProjects(){
        return projectRepository.countAllInActiveProjects();
    }
}
