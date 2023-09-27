package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responseDto.*;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.model.*;
import com.example.devopsproj.service.interfaces.ProjectService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GitRepositoryRepository gitRepositoryRepository;


    //implementing DTO pattern for project for saving project
    @Override
    public void saveProject(ProjectDTO projectDTO){ //save project
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());
        List<User> users = projectDTO.getUsers().stream()
                .map(userDTO -> modelMapper.map(userDTO, User.class))
                .collect(Collectors.toList());
        project.setUsers(users);
        projectRepository.save(project);
    }

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());
        projectRepository.save(project);

        return modelMapper.map(project, ProjectDTO.class);
    }

    @Override
    public Optional<Project> getProjectById(Long id){
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> getAll(){
        return projectRepository.findAll();
    }

    @Override
    public List<Project> getAllProjects(){
        return projectRepository.findAllProjects();
    }

    @Override
    public Project updateProject(Project updatedProject){
        return projectRepository.save(updatedProject);
    }

    //get all users in the project based on project id
    @Override
    public List<User> getAllUsersByProjectId(Long projectId){
        List<User> users = projectRepository.findAllUsersByProjectId(projectId);
        if(users.isEmpty()){
            return Collections.emptyList();
        }
        else {
            return users;
        }
    }

    @Override
    public List<User> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role){
        List<User> users = projectRepository.findAllUsersByProjectIdAndRole(projectId, role);
        if(users.isEmpty()){
            return Collections.emptyList();
        }
        else {
            return users;
        }
    }

    //to check if the project has already been deleted
    @Override
    public boolean existsByIdIsDeleted(Long id){
        Optional<Project> checkProject = projectRepository.findById(id);
        if (checkProject.isEmpty()){
            return true;
        }
        Project cproject = checkProject.get();
        return cproject.getDeleted(); //true if deleted=1, else false
    }

    //soft deleting project
    @Override
    public boolean softDeleteProject(Long id){
        try{
            projectRepository.softDeleteProject(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean existsProjectById(Long id){
        return projectRepository.existsById(id);
    }

    @Override
    public boolean existUserInProject(Long projectId, Long userId){
        List<User> userList = projectRepository.existUserInProject(projectId, userId);
        if(userList.isEmpty()){
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public Integer getCountAllProjects(){
        return projectRepository.countAllProjects();
    }

    @Override
    public Integer getCountAllProjectsByRole(EnumRole enumRole) {
        return projectRepository.countAllProjectsByRole(enumRole);
    }

    @Override
    public Integer getCountAllProjectsByUserId(Long id) {
        return projectRepository.countAllProjectsByUserId(id);
    }

    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        return projectRepository.countAllUsersByProjectId(projectId);
    }

    @Override
    public List<ProjectNamePeopleCountDTO> getCountAllPeopleAndProjectName() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectNamePeopleCountDTO> peopleCountDTOS = new ArrayList<>();
        for (Project project : projects){
            ProjectNamePeopleCountDTO peopleCountDTO = new ProjectNamePeopleCountDTO();

            Integer count = projectRepository.countAllUsersByProjectId(project.getProjectId());

            peopleCountDTO.setProjectId(project.getProjectId());
            peopleCountDTO.setProjectName(project.getProjectName());
            peopleCountDTO.setCountPeople(count);
            peopleCountDTOS.add(peopleCountDTO);
        }
        return peopleCountDTOS;
    }

    @Override
    public Integer getCountAllUsersByProjectIdAndRole(Long projectId, EnumRole enumRole) {
        return projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole);
    }

    @Override
    public Integer getCountAllActiveProjects(){
        return projectRepository.countAllActiveProjects();
    }

    @Override
    public Integer getCountAllInActiveProjects(){
        return projectRepository.countAllInActiveProjects();
    }

    @Override
    public List<User> getUsersByProjectIdAndRole(Long projectId, EnumRole role) {
        return projectRepository.findUsersByProjectIdAndRole(projectId, role);
    }

    @Override
    public List<ProjectDTO> getProjectsWithoutFigmaURL() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();

        for (Project project : projects) {
            // Check if the project has a Figma URL set
            if (project.getFigma() == null || project.getFigma().getFigmaURL() == null) {
                // Create a ProjectDTO and add it to the list
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        return projectDTOs;
    }

    @Override
    public List<ProjectDTO> getProjectsWithoutGoogleDriveLink() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();

        for (Project project : projects) {
            if (project.getGoogleDrive() == null || project.getGoogleDrive().getDriveLink() == null) {
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        return projectDTOs;
    }

    @Override
    public ProjectDTO getProjectDetailsById(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            // Retrieve the required details from the project object
            String projectName = project.getProjectName();
            String projectDescription = project.getProjectDescription();
            boolean status = project.getDeleted();

            List<User> users = project.getUsers();
            String pmName = null;
            for (User user : users){
                if(user.getEnumRole()==EnumRole.PROJECT_MANAGER){
                    pmName = user.getName();
                    break;
                }
            }

            List<GitRepository> repositories = project.getRepositories();
            List<GitRepositoryDTO> repositoryDTOS = new ArrayList<>();
            for(GitRepository repository : repositories){
                GitRepositoryDTO repositoryDTO = new GitRepositoryDTO();
                repositoryDTO.setName(repository.getName());
                repositoryDTO.setDescription(repository.getDescription());
                repositoryDTOS.add(repositoryDTO);
            }

            Figma figma = project.getFigma();
            String figmaURL = figma != null ? figma.getFigmaURL() : null; // Retrieve the Figma URL
            FigmaDTO figmaDTO = new FigmaDTO(figmaURL);

            GoogleDrive googleDrive = project.getGoogleDrive();
            String driveLink = googleDrive != null ? googleDrive.getDriveLink() : null; // Retrieve the driveLink
            GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(driveLink);

            LocalDateTime lastUpdated = project.getLastUpdated();

            // Create and return a ProjectDTO object with the required details
            return new ProjectDTO(
                    projectName,
                    projectDescription,
                    status,
                    pmName,
                    repositoryDTOS,
                    figmaDTO,
                    new GoogleDriveDTO(driveLink),
                    lastUpdated
            );
        } else {
            return new ProjectDTO();
        }
    }

    // ----- Other methods ------

    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

}