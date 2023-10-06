package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
class UserDTOTest {

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        String name = "John Doe";
        String email = "john@example.com";
        EnumRole enumRole = EnumRole.USER;

        UserDTO dto = new UserDTO(id, name, email, enumRole);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(email, dto.getEmail());
        assertEquals(enumRole, dto.getEnumRole());
    }

    @Test
    void testNoArgsConstructor() {
        UserDTO dto = new UserDTO();

        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getEnumRole());
    }

    @Test
    void testSetterGetter() {
        UserDTO dto = new UserDTO();
        Long id = 1L;
        String name = "John Doe";
        String email = "john@example.com";
        EnumRole enumRole = EnumRole.USER;
        String gitHubUsername = "johnGitHub";
        LocalDateTime lastUpdated = LocalDateTime.now();
        LocalDateTime lastLogout = LocalDateTime.now().minusHours(1);

        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        dto.setEnumRole(enumRole);
        dto.setGitHubUsername(gitHubUsername);
        dto.setLastUpdated(lastUpdated);
        dto.setLastLogout(lastLogout);

        assertEquals(id, dto.getId());
        assertEquals(name, dto.getName());
        assertEquals(email, dto.getEmail());
        assertEquals(enumRole, dto.getEnumRole());
        assertEquals(gitHubUsername, dto.getGitHubUsername());
        assertEquals(lastUpdated, dto.getLastUpdated());
        assertEquals(lastLogout, dto.getLastLogout());
    }

    @Test
    void testAdditionalConstructors() {
        Long id = 1L;
        String name = "John Doe";
        String email = "john@example.com";
        EnumRole enumRole = EnumRole.USER;
        String gitHubUsername = "johnGitHub";
        LocalDateTime lastUpdated = LocalDateTime.now();
        LocalDateTime lastLogout = LocalDateTime.now().minusHours(1);

        UserDTO dto1 = new UserDTO(id, name);
        UserDTO dto2 = new UserDTO(name, enumRole, lastUpdated);
        UserDTO dto3 = new UserDTO(id, name, email, enumRole, gitHubUsername);
        UserDTO dto4 = new UserDTO(id, name, email, enumRole, lastUpdated);
        UserDTO dto5 = new UserDTO(id, name, email, enumRole, lastUpdated, lastLogout);

        assertEquals(id, dto1.getId());
        assertEquals(name, dto1.getName());

        assertEquals(name, dto2.getName());
        assertEquals(enumRole, dto2.getEnumRole());
        assertEquals(lastUpdated, dto2.getLastUpdated());

        assertEquals(id, dto3.getId());
        assertEquals(name, dto3.getName());
        assertEquals(email, dto3.getEmail());
        assertEquals(enumRole, dto3.getEnumRole());
        assertEquals(gitHubUsername, dto3.getGitHubUsername());

        assertEquals(id, dto4.getId());
        assertEquals(name, dto4.getName());
        assertEquals(email, dto4.getEmail());
        assertEquals(enumRole, dto4.getEnumRole());
        assertEquals(lastUpdated, dto4.getLastUpdated());

        assertEquals(id, dto5.getId());
        assertEquals(name, dto5.getName());
        assertEquals(email, dto5.getEmail());
        assertEquals(enumRole, dto5.getEnumRole());
        assertEquals(lastUpdated, dto5.getLastUpdated());
        assertEquals(lastLogout, dto5.getLastLogout());
    }
}
