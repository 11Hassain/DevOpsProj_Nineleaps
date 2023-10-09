package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccessResponseDTOTest {

    @Test
    void testConstructorAndGetters() {
        Long accessRequestId = 1L;
        String pmName = "John Smith";
        UserDTO userDTO = new UserDTO(2L, "Alice Johnson", "alice@example.com");
        ProjectDTO projectDTO = new ProjectDTO(3L, "Project X", "Description", null, null);
        String accessDescription = "Access request description";
        boolean allowed = true;

        AccessResponseDTO accessResponseDTO = new AccessResponseDTO(
                accessRequestId, pmName, userDTO, projectDTO, accessDescription, allowed
        );

        assertEquals(accessRequestId, accessResponseDTO.getAccessRequestId());
        assertEquals(pmName, accessResponseDTO.getPmName());
        assertEquals(userDTO, accessResponseDTO.getUser());
        assertEquals(projectDTO, accessResponseDTO.getProject());
        assertEquals(accessDescription, accessResponseDTO.getAccessDescription());
        assertEquals(allowed, accessResponseDTO.isAllowed());
    }

    @Test
    void testDefaultConstructor() {
        AccessResponseDTO accessResponseDTO = new AccessResponseDTO();

        assertNull(accessResponseDTO.getAccessRequestId());
        assertNull(accessResponseDTO.getPmName());
        assertNull(accessResponseDTO.getUser());
        assertNull(accessResponseDTO.getProject());
        assertNull(accessResponseDTO.getAccessDescription());
        assertFalse(accessResponseDTO.isAllowed());
        assertNull(accessResponseDTO.getResponse());
        assertFalse(accessResponseDTO.isNotified());
    }

    @Test
    void testSetters() {
        AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
        Long accessRequestId = 2L;
        String pmName = "Jane Doe";
        UserDTO userDTO = new UserDTO(3L, "Bob Johnson", "bob@example.com");
        ProjectDTO projectDTO = new ProjectDTO(4L, "Project Y", "Description Y", null, null);
        String accessDescription = "Another access request";
        boolean allowed = false;

        accessResponseDTO.setAccessRequestId(accessRequestId);
        accessResponseDTO.setPmName(pmName);
        accessResponseDTO.setUser(userDTO);
        accessResponseDTO.setProject(projectDTO);
        accessResponseDTO.setAccessDescription(accessDescription);
        accessResponseDTO.setAllowed(allowed);

        assertEquals(accessRequestId, accessResponseDTO.getAccessRequestId());
        assertEquals(pmName, accessResponseDTO.getPmName());
        assertEquals(userDTO, accessResponseDTO.getUser());
        assertEquals(projectDTO, accessResponseDTO.getProject());
        assertEquals(accessDescription, accessResponseDTO.getAccessDescription());
        assertEquals(allowed, accessResponseDTO.isAllowed());
    }

    @Test
    void testGeneratedMethods() {
        // Create an instance of AccessResponseDTO
        AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
        accessResponseDTO.setAccessRequestId(5L);
        accessResponseDTO.setPmName("Charlie Brown");
        accessResponseDTO.setUser(new UserDTO(6L, "Eve Smith", "eve@example.com"));
        accessResponseDTO.setProject(new ProjectDTO(7L, "Project Z", "Description Z", null, null));
        accessResponseDTO.setAccessDescription("Yet another access request");
        accessResponseDTO.setAllowed(true);

        // Test getters
        assertEquals(5L, accessResponseDTO.getAccessRequestId());
        assertEquals("Charlie Brown", accessResponseDTO.getPmName());
        assertEquals("eve@example.com", accessResponseDTO.getUser().getEmail());
        assertEquals("Project Z", accessResponseDTO.getProject().getProjectName());
        assertEquals("Yet another access request", accessResponseDTO.getAccessDescription());
        assertTrue(accessResponseDTO.isAllowed());
    }

    @Test
    void testConstructorWithNullValues() {
        AccessResponseDTO accessResponseDTO = new AccessResponseDTO(null, null, null, null, null, false);
        assertNull(accessResponseDTO.getAccessRequestId());
        assertNull(accessResponseDTO.getPmName());
        assertNull(accessResponseDTO.getUser());
        assertNull(accessResponseDTO.getProject());
        assertNull(accessResponseDTO.getAccessDescription());
        assertFalse(accessResponseDTO.isAllowed());
    }

    @Test
    void testConstructorWithNullValue() {
        // Create instances of UserDTO and ProjectDTO with appropriate values
        UserDTO userDTO = new UserDTO(1L, "User Name", "user@example.com");
        ProjectDTO projectDTO = new ProjectDTO(2L, "Project Name", "Project Description", null, null);

        AccessResponseDTO accessResponseDTO = new AccessResponseDTO(null, null, userDTO, projectDTO, null, false);

        assertNull(accessResponseDTO.getAccessRequestId());
        assertNull(accessResponseDTO.getPmName());
        assertEquals(userDTO, accessResponseDTO.getUser());
        assertEquals(projectDTO, accessResponseDTO.getProject());
        assertNull(accessResponseDTO.getAccessDescription());
        assertFalse(accessResponseDTO.isAllowed());
    }

}
