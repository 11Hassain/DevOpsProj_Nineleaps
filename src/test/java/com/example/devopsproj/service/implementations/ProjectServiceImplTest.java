//package com.example.devopsproj.service.implementations;
//
//import com.example.devopsproj.dto.responsedto.ProjectDTO;
//import com.example.devopsproj.model.Project;
//import com.example.devopsproj.repository.ProjectRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.modelmapper.ModelMapper;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ProjectServiceImplTest {
//
//    @Mock
//    private ProjectRepository projectRepository;
//    @Mock
//    private ModelMapper modelMapper;
//
//    @InjectMocks
//    private ProjectServiceImpl projectService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testCreateProject() {
//        // Arrange
//        ProjectDTO projectDTO = new ProjectDTO();
//        projectDTO.setProjectId(1L);
//        projectDTO.setProjectName("Test Project");
//        projectDTO.setProjectDescription("Description");
//
//        Project projectEntity = new Project();
//        when(modelMapper.map(projectDTO, Project.class)).thenReturn(projectEntity);
//        when(projectRepository.save(projectEntity)).thenReturn(projectEntity);
//
//        // Act
//        ProjectDTO result = projectService.createProject(projectDTO);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(projectDTO.getProjectId(), result.getProjectId());
//        assertEquals(projectDTO.getProjectName(), result.getProjectName());
//        assertEquals(projectDTO.getProjectDescription(), result.getProjectDescription());
//    }
//
//    // Add more test cases as needed
//}
