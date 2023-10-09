package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessRequestDTOTest {

    @Test
    void testAccessRequestIdPositive() {
        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setAccessRequestId(1L);
        assertEquals(1L, dto.getAccessRequestId());
    }

    @Test
    void testAccessRequestIdGetterSetter() {
        // Arrange
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();

        // Act
        accessRequestDTO.setAccessRequestId(1L);
        Long accessRequestId = accessRequestDTO.getAccessRequestId();

        // Assert
        assertEquals(1L, accessRequestId);
    }

    @Test
    void testPmNameGetterSetter() {
        // Arrange
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();

        // Act
        accessRequestDTO.setPmName("John Doe");
        String pmName = accessRequestDTO.getPmName();

        // Assert
        assertEquals("John Doe", pmName);
    }

    @Test
    void testUserDTOGetterSetter() {
        // Arrange
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Alice");

        // Act
        accessRequestDTO.setUser(userDTO);
        UserDTO retrievedUserDTO = accessRequestDTO.getUser();

        // Assert
        assertNotNull(retrievedUserDTO);
        assertEquals(1L, retrievedUserDTO.getId());
        assertEquals("Alice", retrievedUserDTO.getName());
    }

    @Test
    void testProjectDTOGetterSetter() {
        // Arrange
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(2L);
        projectDTO.setProjectName("Project A");

        // Act
        accessRequestDTO.setProject(projectDTO);
        ProjectDTO retrievedProjectDTO = accessRequestDTO.getProject();

        // Assert
        assertNotNull(retrievedProjectDTO);
        assertEquals(2L, retrievedProjectDTO.getProjectId());
        assertEquals("Project A", retrievedProjectDTO.getProjectName());
    }

    @Test
    void testRequestDescriptionGetterSetter() {
        // Arrange
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();

        // Act
        accessRequestDTO.setRequestDescription("Access request description");
        String requestDescription = accessRequestDTO.getRequestDescription();

        // Assert
        assertEquals("Access request description", requestDescription);
    }

    @Test
    void testAllowedGetterSetter() {
        // Arrange
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();

        // Act
        accessRequestDTO.setAllowed(true);
        boolean isAllowed = accessRequestDTO.isAllowed();

        // Assert
        assertTrue(isAllowed);
    }
    @Test
    void testToString() {
        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setAccessRequestId(2L);
        dto.setPmName("John Doe");
        dto.setRequestDescription("New request");
        dto.setAllowed(false);

        String expectedToString = "AccessRequestDTO(accessRequestId=2, pmName=John Doe, user=null, project=null, requestDescription=New request, allowed=false)";
        assertEquals(expectedToString, dto.toString());
    }
    @Test
    void testAllArgsConstructor() {
        AccessRequestDTO dto = new AccessRequestDTO(1L, "Jane", null, null, "Request", true);

        assertEquals(1L, dto.getAccessRequestId());
        assertEquals("Jane", dto.getPmName());
        assertNull(dto.getUser());
        assertNull(dto.getProject());
        assertEquals("Request", dto.getRequestDescription());
        assertTrue(dto.isAllowed());
    }
}