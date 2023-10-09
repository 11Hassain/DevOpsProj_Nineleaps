package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    void testConstructorWithIdNameEmailEnumRole() {
        // Arrange
        Long id = 1L;
        String name = "John Doe";
        String email = "john@example.com";
        EnumRole enumRole = EnumRole.USER;

        // Act
        UserDTO userDTO = new UserDTO(id, name, email, enumRole);

        // Assert
        assertEquals(id, userDTO.getId());
        assertEquals(name, userDTO.getName());
        assertEquals(email, userDTO.getEmail());
        assertEquals(enumRole, userDTO.getEnumRole());
    }
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
    void testConstructorWithIdName() {
        // Arrange
        Long id = 2L;
        String name = "Alice";

        // Act
        UserDTO userDTO = new UserDTO(id, name);

        // Assert
        assertEquals(id, userDTO.getId());
        assertEquals(name, userDTO.getName());
    }

    @Test
    void testConstructorWithNameEnumRoleLastUpdated() {
        // Arrange
        String name = "Bob";
        EnumRole enumRole = EnumRole.ADMIN;
        LocalDateTime lastUpdated = LocalDateTime.now();

        // Act
        UserDTO userDTO = new UserDTO(name, enumRole, lastUpdated);

        // Assert
        assertNull(userDTO.getId()); // Id should be null
        assertEquals(name, userDTO.getName());
        assertNull(userDTO.getEmail()); // Email should be null
        assertEquals(enumRole, userDTO.getEnumRole());
        assertEquals(lastUpdated, userDTO.getLastUpdated());
    }

    @Test
    void testConstructorWithIdNameEmailEnumRoleGitHubUsername() {
        // Arrange
        Long id = 3L;
        String name = "Carol";
        String email = "carol@example.com";
        EnumRole enumRole = EnumRole.USER;
        String gitHubUsername = "carol_github";

        // Act
        UserDTO userDTO = new UserDTO(id, name, email, enumRole, gitHubUsername);

        // Assert
        assertEquals(id, userDTO.getId());
        assertEquals(name, userDTO.getName());
        assertEquals(email, userDTO.getEmail());
        assertEquals(enumRole, userDTO.getEnumRole());
        assertEquals(gitHubUsername, userDTO.getGitHubUsername());
    }

    @Test
    void testConstructorWithIdNameEmail() {
        // Arrange
        Long id = 4L;
        String name = "David";
        String email = "david@example.com";

        // Act
        UserDTO userDTO = new UserDTO(id, name, email);

        // Assert
        assertEquals(id, userDTO.getId());
        assertEquals(name, userDTO.getName());
        assertEquals(email, userDTO.getEmail());
    }

    @Test
    void testConstructorWithIdNameEmailEnumRoleLastUpdatedLastLogout() {
        // Arrange
        Long id = 5L;
        String name = "Eve";
        String email = "eve@example.com";
        EnumRole enumRole = EnumRole.USER;
        LocalDateTime lastUpdated = LocalDateTime.now();
        LocalDateTime lastLogout = LocalDateTime.now().minusHours(1);

        // Act
        UserDTO userDTO = new UserDTO(id, name, email, enumRole, lastUpdated, lastLogout);

        // Assert
        assertEquals(id, userDTO.getId());
        assertEquals(name, userDTO.getName());
        assertEquals(email, userDTO.getEmail());
        assertEquals(enumRole, userDTO.getEnumRole());
        assertEquals(lastUpdated, userDTO.getLastUpdated());
        assertEquals(lastLogout, userDTO.getLastLogout());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Long id1 = 1L;
        String name1 = "John";
        String email1 = "john@example.com";
        EnumRole enumRole1 = EnumRole.USER;

        UserDTO userDTO1 = new UserDTO(id1, name1, email1, enumRole1);

        Long id2 = 2L;
        String name2 = "Alice";
        String email2 = "alice@example.com";
        EnumRole enumRole2 = EnumRole.ADMIN;

        UserDTO userDTO2 = new UserDTO(id2, name2, email2, enumRole2);

        // Act and Assert
        assertNotEquals(userDTO1, userDTO2);
        assertNotEquals(userDTO1.hashCode(), userDTO2.hashCode());

        // Create a userDTO3 with the same attributes as userDTO1
        UserDTO userDTO3 = new UserDTO(id1, name1, email1, enumRole1);

        // Act and Assert
        assertEquals(userDTO1, userDTO3);
        assertEquals(userDTO1.hashCode(), userDTO3.hashCode());
    }
    @Test
    void testEqualsWithEqualObjects() {
        // Create two UserDTO objects with the same attributes
        UserDTO userDTO1 = new UserDTO(1L, "John", "john@example.com", EnumRole.USER);
        UserDTO userDTO2 = new UserDTO(1L, "John", "john@example.com", EnumRole.USER);

        // Assert that they are equal
        assertTrue(userDTO1.equals(userDTO2));
    }

    @Test
    void testEqualsWithDifferentIds() {
        // Create two UserDTO objects with different IDs
        UserDTO userDTO1 = new UserDTO(1L, "John", "john@example.com", EnumRole.USER);
        UserDTO userDTO2 = new UserDTO(2L, "John", "john@example.com", EnumRole.USER);

        // Assert that they are not equal
        assertFalse(userDTO1.equals(userDTO2));
    }

    @Test
    void testEqualsWithDifferentNames() {
        // Create two UserDTO objects with different names
        UserDTO userDTO1 = new UserDTO(1L, "John", "john@example.com", EnumRole.USER);
        UserDTO userDTO2 = new UserDTO(1L, "Alice", "john@example.com", EnumRole.USER);

        // Assert that they are not equal
        assertFalse(userDTO1.equals(userDTO2));
    }

    @Test
    void testEqualsWithDifferentEmails() {
        // Create two UserDTO objects with different emails
        UserDTO userDTO1 = new UserDTO(1L, "John", "john@example.com", EnumRole.USER);
        UserDTO userDTO2 = new UserDTO(1L, "John", "alice@example.com", EnumRole.USER);

        // Assert that they are not equal
        assertFalse(userDTO1.equals(userDTO2));
    }

    @Test
    void testEqualsWithDifferentEnumRoles() {
        // Create two UserDTO objects with different enum roles
        UserDTO userDTO1 = new UserDTO(1L, "John", "john@example.com", EnumRole.USER);
        UserDTO userDTO2 = new UserDTO(1L, "John", "john@example.com", EnumRole.ADMIN);

        // Assert that they are not equal
        assertFalse(userDTO1.equals(userDTO2));
    }

    @Test
    void testEqualsWithNullObject() {
        UserDTO userDTO = new UserDTO(1L, "John", "john@example.com", EnumRole.USER);

        // Assert that it's not equal to null
        assertFalse(userDTO.equals(null));
    }

    @Test
    void testEqualsWithDifferentClass() {
        UserDTO userDTO = new UserDTO(1L, "John", "john@example.com", EnumRole.USER);

        // Assert that it's not equal to an object of a different class
        assertFalse(userDTO.equals("Not a UserDTO"));
    }
    @Test
    void testGetToken() {
        // Arrange
        String expectedToken = "sampleToken";
        UserDTO userDTO = new UserDTO();

        // Act
        userDTO.setToken(expectedToken);
        String actualToken = userDTO.getToken();

        // Assert
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void testSetToken() {
        // Arrange
        String newToken = "newToken";
        UserDTO userDTO = new UserDTO();

        // Act
        userDTO.setToken(newToken);
        String actualToken = userDTO.getToken();

        // Assert
        assertEquals(newToken, actualToken);
    }

    @Test
    void testSetNullToken() {
        // Arrange
        UserDTO userDTO = new UserDTO();

        // Act
        userDTO.setToken(null);
        String actualToken = userDTO.getToken();

        // Assert
        assertNull(actualToken);
    }

}
