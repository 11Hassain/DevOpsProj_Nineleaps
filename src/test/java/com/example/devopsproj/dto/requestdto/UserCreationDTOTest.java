package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.commons.enumerations.EnumRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        UserCreationDTO userDTO1 = new UserCreationDTO(1L, "John Doe", "john@example.com", EnumRole.ADMIN);
        UserCreationDTO userDTO2 = new UserCreationDTO(1L, "John Doe", "john@example.com", EnumRole.ADMIN);

        assertTrue(userDTO1.equals(userDTO2));
        assertEquals(userDTO1.hashCode(), userDTO2.hashCode());
    }
}
