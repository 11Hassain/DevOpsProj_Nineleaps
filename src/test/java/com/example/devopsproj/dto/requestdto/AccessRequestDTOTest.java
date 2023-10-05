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
    void testAccessRequestIdNegative() {
        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setAccessRequestId(-1L);
        assertEquals(-1L, dto.getAccessRequestId());
    }

    @Test
    void testPmNameNotBlank() {
        AccessRequestDTO dto1 = new AccessRequestDTO();
        AccessRequestDTO dto2 = new AccessRequestDTO();
        AccessRequestDTO dto3 = new AccessRequestDTO();
        dto1.setPmName("John");
        dto2.setPmName("");
        dto3.setPmName("ThisNameIsTooLongAndExceedsFiftyCharacters");
        assertEquals("John", dto1.getPmName());
        assertEquals("", dto2.getPmName());
        assertEquals("ThisNameIsTooLongAndExceedsFiftyCharacters", dto3.getPmName());
    }

    @Test
    void testUser() {
        AccessRequestDTO dto = new AccessRequestDTO();
        UserDTO userDTO = new UserDTO();
        userDTO.setName("testUser");
        dto.setUser(userDTO);
        assertEquals(userDTO, dto.getUser());
    }

    @Test
    void testProject() {
        AccessRequestDTO dto = new AccessRequestDTO();
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName("TestProject");
        dto.setProject(projectDTO);
        assertEquals(projectDTO, dto.getProject());
    }

    @Test
    void testRequestDescription() {
        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setRequestDescription("Request description.");
        assertEquals("Request description.", dto.getRequestDescription());
    }

    @Test
    void testAllowed() {
        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setAllowed(true);
        assertTrue(dto.isAllowed());
    }

    @Test
    void testAllArgsConstructor() {
        AccessRequestDTO dto = new AccessRequestDTO(1L, "John Doe", null, null, "Request", true);

        assertEquals(1L, dto.getAccessRequestId());
        assertEquals("John Doe", dto.getPmName());
        assertNull(dto.getUser());
        assertNull(dto.getProject());
        assertEquals("Request", dto.getRequestDescription());
        assertTrue(dto.isAllowed());
    }

    @Test
    void testToString() {
        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setAccessRequestId(2L);
        dto.setPmName("Jane Smith");
        dto.setRequestDescription("New request");
        dto.setAllowed(false);

        String expectedToString = "AccessRequestDTO(accessRequestId=2, pmName=Jane Smith, user=null, project=null, requestDescription=New request, allowed=false)";
        assertEquals(expectedToString, dto.toString());
    }
}
