package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectNamePeopleCountDTOTest {

    @Test
    void testAllArgsConstructor() {
        Long projectId = 1L;
        String projectName = "Project 1";
        Integer countPeople = 5;

        ProjectNamePeopleCountDTO dto = new ProjectNamePeopleCountDTO(projectId, projectName, countPeople);

        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(countPeople, dto.getCountPeople());
    }

    @Test
    void testNoArgsConstructor() {
        ProjectNamePeopleCountDTO dto = new ProjectNamePeopleCountDTO();

        assertNull(dto.getProjectId());
        assertNull(dto.getProjectName());
        assertNull(dto.getCountPeople());
    }

    @Test
    void testSetterGetter() {
        ProjectNamePeopleCountDTO dto = new ProjectNamePeopleCountDTO();
        Long projectId = 1L;
        String projectName = "Project 1";
        Integer countPeople = 5;

        dto.setProjectId(projectId);
        dto.setProjectName(projectName);
        dto.setCountPeople(countPeople);

        assertEquals(projectId, dto.getProjectId());
        assertEquals(projectName, dto.getProjectName());
        assertEquals(countPeople, dto.getCountPeople());
    }
}
