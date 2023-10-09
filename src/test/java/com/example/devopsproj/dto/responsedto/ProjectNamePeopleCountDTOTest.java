package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNamePeopleCountDTOTest {

    @Test
    void testValidProjectNamePeopleCountDTO() {
        // Arrange
        Long projectId = 1L;
        String projectName = "Project A";
        Integer countPeople = 5;

        // Act
        ProjectNamePeopleCountDTO dto = new ProjectNamePeopleCountDTO(projectId, projectName, countPeople);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(countPeople, dto.getCountPeople());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        ProjectNamePeopleCountDTO dto = new ProjectNamePeopleCountDTO();

        // Assert
        assertNull(dto.getProjectId());
        assertNull(dto.getProjectName());
        assertNull(dto.getCountPeople());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long projectId = 2L;
        String projectName = "Project B";
        Integer countPeople = 10;

        // Act
        ProjectNamePeopleCountDTO dto = new ProjectNamePeopleCountDTO(projectId, projectName, countPeople);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(countPeople, dto.getCountPeople());
    }

    @Test
    void testSetterGetter() {
        // Arrange
        Long projectId = 3L;
        String projectName = "Project C";
        Integer countPeople = 15;

        // Act
        ProjectNamePeopleCountDTO dto = new ProjectNamePeopleCountDTO();
        dto.setProjectId(projectId);
        dto.setProjectName(projectName);
        dto.setCountPeople(countPeople);

        // Assert
        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(countPeople, dto.getCountPeople());
    }

    @Test
    void testToString() {
        // Arrange
        Long projectId = 4L;
        String projectName = "Project D";
        Integer countPeople = 20;

        // Act
        ProjectNamePeopleCountDTO dto = new ProjectNamePeopleCountDTO(projectId, projectName, countPeople);

        // Assert
        String expectedToString = "ProjectNamePeopleCountDTO(projectId=4, projectName=Project D, countPeople=20)";
        assertEquals(expectedToString, dto.toString());
    }
}
