package com.example.devopsproj.dto.requestdto;

import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.commons.enumerations.EnumRole;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserCreationDTOTest {

    @Test
    void testConstructorAndGetters() {
        Long id = 1L;
        String name = "John Doe";
        String email = "johndoe@example.com";
        EnumRole enumRole = EnumRole.USER;

        UserCreationDTO userCreationDTO = new UserCreationDTO(id, name, email, enumRole);

        assertEquals(id, userCreationDTO.getId());
        assertEquals(name, userCreationDTO.getName());
        assertEquals(email, userCreationDTO.getEmail());
        assertEquals(enumRole, userCreationDTO.getEnumRole());
    }

    @Test
    void testDefaultConstructor() {
        UserCreationDTO userCreationDTO = new UserCreationDTO();

        assertNull(userCreationDTO.getId());
        assertNull(userCreationDTO.getName());
        assertNull(userCreationDTO.getEmail());
        assertNull(userCreationDTO.getEnumRole());
    }

    @Test
    void testSetters() {
        UserCreationDTO userCreationDTO = new UserCreationDTO();
        Long id = 2L;
        String name = "Jane Smith";
        String email = "janesmith@example.com";
        EnumRole enumRole = EnumRole.ADMIN;

        userCreationDTO.setId(id);
        userCreationDTO.setName(name);
        userCreationDTO.setEmail(email);
        userCreationDTO.setEnumRole(enumRole);

        assertEquals(id, userCreationDTO.getId());
        assertEquals(name, userCreationDTO.getName());
        assertEquals(email, userCreationDTO.getEmail());
        assertEquals(enumRole, userCreationDTO.getEnumRole());
    }

    @Test
    void testGeneratedMethods() {
        // Create an instance of UserCreationDTO
        UserCreationDTO userCreationDTO = new UserCreationDTO();
        userCreationDTO.setId(3L);
        userCreationDTO.setName("Alice Johnson");
        userCreationDTO.setEmail("alice@example.com");
        userCreationDTO.setEnumRole(EnumRole.USER);

        // Test getters
        assertEquals(3L, userCreationDTO.getId());
        assertEquals("Alice Johnson", userCreationDTO.getName());
        assertEquals("alice@example.com", userCreationDTO.getEmail());
        assertEquals(EnumRole.USER, userCreationDTO.getEnumRole());
    }

    @Test
    void testConstructorWithNullValues() {
        UserCreationDTO userCreationDTO = new UserCreationDTO(null, null, null, null);
        assertNull(userCreationDTO.getId());
        assertNull(userCreationDTO.getName());
        assertNull(userCreationDTO.getEmail());
        assertNull(userCreationDTO.getEnumRole());
    }

    @Test
    void testConstructorWithEmptyStringValues() {
        UserCreationDTO userCreationDTO = new UserCreationDTO(1L, "", "", EnumRole.USER);
        assertEquals(1L, userCreationDTO.getId());
        assertEquals("", userCreationDTO.getName());
        assertEquals("", userCreationDTO.getEmail());
        assertEquals(EnumRole.USER, userCreationDTO.getEnumRole());
    }
}
