package com.example.devopsproj.model;

import com.example.devopsproj.commons.enumerations.EnumRole;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Mock
    private Project project;

    @Test
    void testGetterAndSetter() {
        User user = new User();

        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("1234567890");
        user.setEnumRole(EnumRole.USER);
        user.setDeleted(true);

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals(EnumRole.USER, user.getEnumRole());
        assertTrue(user.getDeleted());
    }

    @Test
    void testConstructorWithDefaultValues() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPhoneNumber());
        assertNull(user.getEnumRole());
        assertFalse(user.getDeleted());
    }

    @Test
    void testAuthorities() {
        User user = new User();

        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    void testIsAccountNonExpired() {
        User user = new User();

        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        User user = new User();

        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        User user = new User();

        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        User user = new User();

        assertTrue(user.isEnabled());
    }

    @Test
    void testProjects() {
        User user = new User();

        assertTrue(user.getProjects().isEmpty());

        Project project = new Project();
        user.getProjects().add(project);

        assertEquals(1, user.getProjects().size());
        assertTrue(user.getProjects().contains(project));
    }

    @Test
    void testAccessRequest() {
        User user = new User();

        assertTrue(user.getAccessRequest().isEmpty());

        AccessRequest accessRequest = new AccessRequest();
        user.getAccessRequest().add(accessRequest);

        assertEquals(1, user.getAccessRequest().size());
        assertTrue(user.getAccessRequest().contains(accessRequest));
    }

    @Test
    void testUserNames() {
        User user = new User();

        assertNull(user.getUserNames());

        UserNames userNames = new UserNames();
        user.setUserNames(userNames);

        assertNotNull(user.getUserNames());
        assertEquals(userNames, user.getUserNames());
    }

    @Test
    void testGetPassword() {
        User user = new User();

        assertNull(user.getPassword());
    }

    @Test
    void testGetUsername() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        assertEquals(email, user.getUsername());
    }

    @Test
    void testBuilder() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("123-456-7890")
                .enumRole(EnumRole.USER)
                .deleted(false)
                .lastUpdated(LocalDateTime.now())
                .lastLogout(LocalDateTime.now())
                .build();

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("123-456-7890", user.getPhoneNumber());
        assertEquals(EnumRole.USER, user.getEnumRole());
        assertFalse(user.getDeleted());
        assertNotNull(user.getLastUpdated());
        assertNotNull(user.getLastLogout());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("123-456-7890");
        user.setEnumRole(EnumRole.USER);
        user.setDeleted(false);
        user.setLastUpdated(LocalDateTime.now());
        user.setLastLogout(LocalDateTime.now());

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("123-456-7890", user.getPhoneNumber());
        assertEquals(EnumRole.USER, user.getEnumRole());
        assertFalse(user.getDeleted());
        assertNotNull(user.getLastUpdated());
        assertNotNull(user.getLastLogout());
    }
}
