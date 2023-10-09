package com.example.devopsproj.model;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserNamesTest {

    private UserNames userNames;

    @BeforeEach
    void setUp() {
        userNames = new UserNames();
    }



    @Test
    void testUserNamesSetterGetter() {
        // Arrange
        Long id = 1L;
        String username = "johndoe";

        // Act
        userNames.setId(id);
        userNames.setUsername(username);

        // Assert
        assertEquals(id, userNames.getId());
        assertEquals(username, userNames.getUsername());
    }




    @Test
    void testEquals() {
        // Arrange
        UserNames userNames1 = new UserNames();
        userNames1.setId(1L);

        UserNames userNames2 = new UserNames();
        userNames2.setId(2L);

        // Assert
        assertNotEquals(userNames1, userNames2);
    }

    @Test
    void testHashCode() {
        // Arrange
        UserNames userNames1 = new UserNames();
        userNames1.setId(1L);

        UserNames userNames2 = new UserNames();
        userNames2.setId(2L);

        // Assert
        assertNotEquals(userNames1.hashCode(), userNames2.hashCode());
    }

    @Test
    void testToString() {
        // Assert
        assertNotNull(userNames.toString());
    }

    @Test
    void testNoArgsConstructor() {
        UserNames userNames = new UserNames();
        assertNotNull(userNames);
    }



    @Test
    void testUserNamesProperties() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        UserNames userNames = new UserNames();
        userNames.setId(1L);
        userNames.setUsername("johnDoe");
        userNames.setUser(user);

        assertEquals(1L, userNames.getId());
        assertEquals("johnDoe", userNames.getUsername());
        assertEquals(user, userNames.getUser());
    }


}
