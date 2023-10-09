package com.example.devopsproj.model;

import com.example.devopsproj.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProjectTest {

    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAndGetProject() {
        Project project = new Project();
        project.setProjectName("Test Project");
        project.setProjectDescription("Description");

        projectRepository.save(project);

        when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));

        Optional<Project> retrievedProjectOptional = projectRepository.findById(project.getProjectId());

        assertTrue(retrievedProjectOptional.isPresent());

        Project retrievedProject = retrievedProjectOptional.get();

        assertNotNull(retrievedProject);
        assertEquals("Test Project", retrievedProject.getProjectName());
        assertEquals("Description", retrievedProject.getProjectDescription());
    }

    @Test
    void testProjectAssociations() {
        User user1 = new User();
        user1.setName("User 1");
        User user2 = new User();
        user2.setName("User 2");

        Project project = new Project();
        project.setProjectName("Test Project");
        project.setUsers(List.of(user1, user2));

        projectRepository.save(project);

        when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));

        Optional<Project> retrievedProjectOptional = projectRepository.findById(project.getProjectId());

        assertTrue(retrievedProjectOptional.isPresent());
        Project retrievedProject = retrievedProjectOptional.get();
        assertEquals(2, retrievedProject.getUsers().size());
    }

    @Test
    void testAccessRequestField() {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setPmName("John Doe");

        Project project = new Project();
        project.setProjectName("Test Project");
        project.setAccessRequest(Collections.singletonList(accessRequest));

        when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));

        Project retrievedProject = projectRepository.findById(project.getProjectId()).orElse(null);

        assertNotNull(retrievedProject);

        AccessRequest retrievedAccessRequest = retrievedProject.getAccessRequest().get(0);
        assertNotNull(retrievedAccessRequest);
        assertEquals("John Doe", retrievedAccessRequest.getPmName());
    }
}

