package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responseDto.*;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final GitRepositoryRepository gitRepositoryRepository;


    // Save a project from a ProjectDTO
    @Override
    public Project saveProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());

        // Map UserDTOs to User entities and set them in the project
        List<User> users = projectDTO.getUsers().stream()
                .map(userDTO -> modelMapper.map(userDTO, User.class))
                .collect(Collectors.toList());
        project.setUsers(users);

        return projectRepository.save(project);
    }

    // Create a project from a ProjectDTO and return the mapped DTO
    @Override
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        project.setProjectDescription(projectDTO.getProjectDescription());
        project.setLastUpdated(LocalDateTime.now());

        // Save the project and return it as a mapped DTO
        projectRepository.save(project);

        return modelMapper.map(project, ProjectDTO.class);
    }

    // Get a project by its ID
    @Override
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    // Get a list of all projects
    @Override
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    // Get a list of all projects, including inactive ones
    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAllProjects();
    }

    // Update a project
    @Override
    public Project updateProject(Project updatedProject) {
        return projectRepository.save(updatedProject);
    }

    // Get all users associated with a project by its ID
    @Override
    public List<User> getAllUsersByProjectId(Long projectId) {
        List<User> users = projectRepository.findAllUsersByProjectId(projectId);
        if (users.isEmpty()) {
            return null;
        } else {
            return users;
        }
    }

    // Get all users associated with a project by its ID and role
    @Override
    public List<User> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role) {
        List<User> users = projectRepository.findAllUsersByProjectIdAndRole(projectId, role);
        if (users.isEmpty()) {
            return null;
        } else {
            return users;
        }
    }

    // Check if a project with the given ID exists and is soft-deleted
    @Override
    public boolean existsByIdIsDeleted(Long id) {
        Optional<Project> checkProject = projectRepository.findById(id);
        Project cproject = checkProject.get();
        return cproject.getDeleted();
    }

    // Soft delete a project by setting its deleted flag to true
    @Override
    public boolean softDeleteProject(Long id) {
        try {
            projectRepository.softDeleteProject(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Check if a project with the given ID exists
    @Override
    public boolean existsProjectById(Long id) {
        return projectRepository.existsById(id);
    }

    // Check if a user exists in a project by their IDs
    @Override
    public boolean existUserInProject(Long projectId, Long userId) {
        List<User> userList = projectRepository.existUserInProject(projectId, userId);
        if (userList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    // Get the count of all projects
    @Override
    public Integer getCountAllProjects() {
        return projectRepository.countAllProjects();
    }

    // Get the count of all projects by role
    @Override
    public Integer getCountAllProjectsByRole(EnumRole enumRole) {
        return projectRepository.countAllProjectsByRole(enumRole);
    }

    // Get the count of all projects by a user's ID
    @Override
    public Integer getCountAllProjectsByUserId(Long id) {
        return projectRepository.countAllProjectsByUserId(id);
    }

    // Get the count of all users associated with a project by its ID
    @Override
    public Integer getCountAllUsersByProjectId(Long projectId) {
        return projectRepository.countAllUsersByProjectId(projectId);
    }

    // Get a list of project names and their associated people count
    @Override
    public List<ProjectNamePeopleCountDTO> getCountAllPeopleAndProjectName() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectNamePeopleCountDTO> peopleCountDTOS = new ArrayList<>();
        for (Project project : projects) {
            ProjectNamePeopleCountDTO peopleCountDTO = new ProjectNamePeopleCountDTO();
            Integer count = projectRepository.countAllUsersByProjectId(project.getProjectId());
            peopleCountDTO.setProjectId(project.getProjectId());
            peopleCountDTO.setProjectName(project.getProjectName());
            peopleCountDTO.setCountPeople(count);
            peopleCountDTOS.add(peopleCountDTO);
        }
        return peopleCountDTOS;
    }

    // Get the count of all users associated with a project by its ID and role
    @Override
    public Integer getCountAllUsersByProjectIdAndRole(Long projectId, EnumRole enumRole) {
        return projectRepository.countAllUsersByProjectIdAndRole(projectId, enumRole);
    }

    // Get the count of all active projects
    @Override
    public Integer getCountAllActiveProjects() {
        return projectRepository.countAllActiveProjects();
    }

    // Get the count of all inactive projects
    @Override
    public Integer getCountAllInActiveProjects() {
        return projectRepository.countAllInActiveProjects();
    }

    // Get a list of users associated with a project by its ID and role
    @Override
    public List<User> getUsersByProjectIdAndRole(Long projectId, EnumRole role) {
        return projectRepository.findUsersByProjectIdAndRole(projectId, role);
    }

    // Get a list of ProjectDTOs for projects without Figma URLs
    @Override
    public List<ProjectDTO> getProjectsWithoutFigmaURL() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();
        for (Project project : projects) {
            // Check if the project has no Figma or Figma URL
            if (project.getFigma() == null || project.getFigma().getFigmaURL() == null) {
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        return projectDTOs;
    }

    // Get a list of ProjectDTOs for projects without Google Drive links
    @Override
    public List<ProjectDTO> getProjectsWithoutGoogleDriveLink() {
        List<Project> projects = projectRepository.findAllProjects();
        List<ProjectDTO> projectDTOs = new ArrayList<>();
        for (Project project : projects) {
            // Check if the project has no Google Drive or Drive link
            if (project.getGoogleDrive() == null || project.getGoogleDrive().getDriveLink() == null) {
                ProjectDTO projectDTO = mapProjectToProjectDTO(project);
                projectDTOs.add(projectDTO);
            }
        }
        return projectDTOs;
    }

    // Get project details by its ID
    @Override
    public ProjectDTO getProjectDetailsById(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            String projectName = project.getProjectName();
            String projectDescription = project.getProjectDescription();
            boolean status = project.getDeleted();
            List<User> users = project.getUsers();
            String pmName = null;
            for (User user : users) {
                // Find the Project Manager's name
                if (user.getEnumRole() == EnumRole.PROJECT_MANAGER) {
                    pmName = user.getName();
                    break;
                }
            }
            List<GitRepository> repositories = project.getRepositories();
            List<GitRepositoryDTO> repositoryDTOS = new ArrayList<>();
            for (GitRepository repository : repositories) {
                // Map GitRepository entities to GitRepositoryDTOs
                GitRepositoryDTO repositoryDTO = new GitRepositoryDTO();
                repositoryDTO.setName(repository.getName());
                repositoryDTO.setDescription(repository.getDescription());
                repositoryDTOS.add(repositoryDTO);
            }
            Figma figma = project.getFigma();
            String figmaURL = figma != null ? figma.getFigmaURL() : null;
            FigmaDTO figmaDTO = new FigmaDTO(figmaURL);
            GoogleDrive googleDrive = project.getGoogleDrive();
            String driveLink = googleDrive != null ? googleDrive.getDriveLink() : null;
            GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(driveLink);
            LocalDateTime lastUpdated = project.getLastUpdated();
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
            return new ProjectDTO(); // Return an empty ProjectDTO if the project doesn't exist
        }
    }

    // Map a Project entity to a ProjectDTO
    @Override
    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }
}