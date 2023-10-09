package com.example.devopsproj.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNamesTest {

    @Test
    void testNoArgsConstructor() {
        UserNames userNames = new UserNames();
        assertNotNull(userNames);
    }

    @Test
    void testGettersAndSetters() {
        UserNames userNames = new UserNames();
        userNames.setId(1L);
        userNames.setUsername("johnDoe");

        assertEquals(1L, userNames.getId());
        assertEquals("johnDoe", userNames.getUsername());

        User user = new User();
        user.setId(2L);
        userNames.setUser(user);

        assertEquals(user, userNames.getUser());
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

    @Test
    void testRepositories() {
        UserNames userNames = new UserNames();
        userNames.setId(1L);
        userNames.setUsername("johnDoe");

        GitRepository gitRepository = new GitRepository();
        gitRepository.setRepoId(1L);
        gitRepository.setName("my-repo");

        userNames.getRepositories().add(gitRepository);

        assertTrue(userNames.getRepositories().contains(gitRepository));
    }
}
