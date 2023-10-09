package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserProjectsDTOTest {

    @Test
    void testGetAndSetUserId() {
        // Arrange
        Long expectedUserId = 123L;
        UserProjectsDTO userProjectsDTO = new UserProjectsDTO();

        // Act
        userProjectsDTO.setUserId(expectedUserId);
        Long actualUserId = userProjectsDTO.getUserId();

        // Assert
        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void testGetAndSetUserName() {
        // Arrange
        String expectedUserName = "john_doe";
        UserProjectsDTO userProjectsDTO = new UserProjectsDTO();

        // Act
        userProjectsDTO.setUserName(expectedUserName);
        String actualUserName = userProjectsDTO.getUserName();

        // Assert
        assertEquals(expectedUserName, actualUserName);
    }

    @Test
    void testGetAndSetProjectNames() {
        // Arrange
        List<String> expectedProjectNames = List.of("Project1", "Project2");
        UserProjectsDTO userProjectsDTO = new UserProjectsDTO();

        // Act
        userProjectsDTO.setProjectNames(expectedProjectNames);
        List<String> actualProjectNames = userProjectsDTO.getProjectNames();

        // Assert
        assertEquals(expectedProjectNames, actualProjectNames);
    }

    @Test
    void testNullUserId() {
        // Arrange
        UserProjectsDTO userProjectsDTO = new UserProjectsDTO();

        // Act
        userProjectsDTO.setUserId(null);

        // Assert
        assertNull(userProjectsDTO.getUserId());
    }

    @Test
    void testNullUserName() {
        // Arrange
        UserProjectsDTO userProjectsDTO = new UserProjectsDTO();

        // Act
        userProjectsDTO.setUserName(null);

        // Assert
        assertNull(userProjectsDTO.getUserName());
    }

    @Test
    void testNullProjectNames() {
        // Arrange
        UserProjectsDTO userProjectsDTO = new UserProjectsDTO();

        // Act
        userProjectsDTO.setProjectNames(null);

        // Assert
        assertNull(userProjectsDTO.getProjectNames());
    }
    @Test
    void testNoArgsConstructor() {
        UserProjectsDTO dto = new UserProjectsDTO();

        assertNull(dto.getUserId());
        assertNull(dto.getUserName());
        assertNull(dto.getProjectNames());
    }

    @Test
    void testAllArgsConstructor() {
        Long userId = 1L;
        String userName = "John";
        List<String> projectNames = new ArrayList<>();

        UserProjectsDTO dto = new UserProjectsDTO(userId, userName, projectNames);

        assertEquals(userId, dto.getUserId());
        assertEquals(userName, dto.getUserName());
        assertEquals(projectNames, dto.getProjectNames());
    }

    @Test
    void testSetterGetter() {
        UserProjectsDTO dto = new UserProjectsDTO();
        Long userId = 2L;
        String userName = "Alice";
        List<String> projectNames = new ArrayList<>();

        dto.setUserId(userId);
        dto.setUserName(userName);
        dto.setProjectNames(projectNames);

        assertEquals(userId, dto.getUserId());
        assertEquals(userName, dto.getUserName());
        assertEquals(projectNames, dto.getProjectNames());
    }
    @Test
    void testToString() {
        // Arrange
        Long userId = 123L;
        String userName = "john_doe";
        List<String> projectNames = List.of("Project1", "Project2");
        UserProjectsDTO userProjectsDTO = new UserProjectsDTO(userId, userName, projectNames);

        // Act
        String toStringResult = userProjectsDTO.toString();

        // Assert
        String expectedToString = "UserProjectsDTO(userId=123, userName=john_doe, projectNames=[Project1, Project2])";
        assertEquals(expectedToString, toStringResult);
    }
}
