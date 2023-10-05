package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserCreationDTOTest {

    @Test
    void testGetterSetter() {
        UserCreationDTO userDTO = new UserCreationDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setEnumRole(EnumRole.ADMIN);

        assertEquals(1L, userDTO.getId());
        assertEquals("John Doe", userDTO.getName());
        assertEquals("john@example.com", userDTO.getEmail());
        assertEquals(EnumRole.ADMIN, userDTO.getEnumRole());
    }

    @Test
    void testRequiredArgsConstructor() {
        UserCreationDTO userDTO = new UserCreationDTO(1L, "John Doe", "john@example.com", EnumRole.ADMIN);

        assertEquals(1L, userDTO.getId());
        assertEquals("John Doe", userDTO.getName());
        assertEquals("john@example.com", userDTO.getEmail());
        assertEquals(EnumRole.ADMIN, userDTO.getEnumRole());
    }

    @Test
    void testToString() {
        UserCreationDTO userDTO = new UserCreationDTO(1L, "John Doe", "john@example.com", EnumRole.ADMIN);

        String expectedToString = "UserCreationDTO(id=1, name=John Doe, email=john@example.com, enumRole=ADMIN)";
        assertEquals(expectedToString, userDTO.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        UserCreationDTO user1 = new UserCreationDTO(1L, "John", "john@example.com", EnumRole.ADMIN);
        UserCreationDTO user2 = new UserCreationDTO(1L, "John", "john@example.com", EnumRole.ADMIN);
        UserCreationDTO user3 = new UserCreationDTO(2L, "Alice", "alice@example.com", EnumRole.USER);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);

        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }
}
