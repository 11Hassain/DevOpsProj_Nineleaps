package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessResponseDTOTest {

    @Test
    void testAccessResponseDTOConstructorAndGetters() {
        Long accessRequestId = 1L;
        String pmName = "John";
        UserDTO user = new UserDTO();
        ProjectDTO project = new ProjectDTO();
        String accessDescription = "Access granted";
        boolean allowed = true;

        AccessResponseDTO accessResponseDTO = new AccessResponseDTO(
                accessRequestId, pmName, user, project, accessDescription, allowed
        );

        assertEquals(accessRequestId, accessResponseDTO.getAccessRequestId());
        assertEquals(pmName, accessResponseDTO.getPmName());
        assertEquals(user, accessResponseDTO.getUser());
        assertEquals(project, accessResponseDTO.getProject());
        assertEquals(accessDescription, accessResponseDTO.getAccessDescription());
        assertEquals(allowed, accessResponseDTO.isAllowed());
    }

    @Test
    void testAccessResponseDTOSetters() {
        AccessResponseDTO accessResponseDTO = new AccessResponseDTO();

        Long accessRequestId = 1L;
        String pmName = "John";
        UserDTO user = new UserDTO();
        ProjectDTO project = new ProjectDTO();
        String accessDescription = "Access granted";
        boolean allowed = true;

        accessResponseDTO.setAccessRequestId(accessRequestId);
        accessResponseDTO.setPmName(pmName);
        accessResponseDTO.setUser(user);
        accessResponseDTO.setProject(project);
        accessResponseDTO.setAccessDescription(accessDescription);
        accessResponseDTO.setAllowed(allowed);

        assertEquals(accessRequestId, accessResponseDTO.getAccessRequestId());
        assertEquals(pmName, accessResponseDTO.getPmName());
        assertEquals(user, accessResponseDTO.getUser());
        assertEquals(project, accessResponseDTO.getProject());
        assertEquals(accessDescription, accessResponseDTO.getAccessDescription());
        assertEquals(allowed, accessResponseDTO.isAllowed());
    }

    @Test
    void testAllArgsConstructor() {
        Long accessRequestId = 1L;
        String pmName = "John";
        UserDTO user = new UserDTO();
        ProjectDTO project = new ProjectDTO();
        String accessDescription = "Access granted";
        boolean allowed = true;
        String response = "Response message";
        boolean notified = true;

        AccessResponseDTO accessResponseDTO = new AccessResponseDTO(
                accessRequestId, pmName, user, project, accessDescription, allowed, response, notified
        );

        assertEquals(accessRequestId, accessResponseDTO.getAccessRequestId());
        assertEquals(pmName, accessResponseDTO.getPmName());
        assertEquals(user, accessResponseDTO.getUser());
        assertEquals(project, accessResponseDTO.getProject());
        assertEquals(accessDescription, accessResponseDTO.getAccessDescription());
        assertEquals(allowed, accessResponseDTO.isAllowed());
        assertEquals(response, accessResponseDTO.getResponse());
        assertEquals(notified, accessResponseDTO.isNotified());
    }
}
