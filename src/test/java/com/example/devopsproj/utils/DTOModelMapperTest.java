package com.example.devopsproj.utils;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.reflect.Constructor;

class DTOModelMapperTest {

    // ----- SUCCESS -----

    @Test
    void testMapProjectToProjectDTOSuccess(){
        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("JohnDoe");

        ProjectDTO projectDTO = DTOModelMapper.mapProjectToProjectDTO(project);

        Assertions.assertEquals(1L, projectDTO.getProjectId());
        Assertions.assertEquals("JohnDoe", projectDTO.getProjectName());
    }

    @Test
    void testMapProjectDTOToProjectSuccess(){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("JohnDoe");

        Project project = DTOModelMapper.mapProjectDTOToProject(projectDTO);

        Assertions.assertEquals(1L, project.getProjectId());
        Assertions.assertEquals("JohnDoe", project.getProjectName());
    }

    @Test
    void testPrivateConstructor() throws NoSuchMethodException {

        // Get the private constructor of DTOModelMapper
        Constructor<DTOModelMapper> constructor = DTOModelMapper.class.getDeclaredConstructor();

        // Ensure the constructor is accessible (even though it's private)
        constructor.setAccessible(true);

        // Attempt to create an instance of DTOModelMapper, and it should throw UnsupportedOperationException
        try {
            constructor.newInstance();
        } catch (Exception e) {
            Assertions.assertEquals(UnsupportedOperationException.class, e.getCause().getClass());
        }
    }

    // ----- FAILURE -----

    @Test
    void testMapProjectToProjectDTOFailure(){
        Project project = new Project();
        project.setProjectName("JohnDoe");

        ProjectDTO projectDTO = DTOModelMapper.mapProjectToProjectDTO(project);

        Assertions.assertNotEquals("Monroe", projectDTO.getProjectName());
    }

    @Test
    void testMapProjectDTOToProjectFailure(){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("JohnDoe");

        Project project = DTOModelMapper.mapProjectDTOToProject(projectDTO);

        Assertions.assertNotEquals("Monroe", project.getProjectName());
    }
}
