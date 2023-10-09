package com.example.devopsproj.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollaboratorTest {

    @Test
    void testCollaboratorGettersAndSetters() {
        Collaborator collaborator = new Collaborator();

        collaborator.setId(1L);
        collaborator.setOwner("John");
        collaborator.setRepo("SampleRepo");
        collaborator.setUsername("johnDoe");

        assertEquals(1L, collaborator.getId());
        assertEquals("John", collaborator.getOwner());
        assertEquals("SampleRepo", collaborator.getRepo());
        assertEquals("johnDoe", collaborator.getUsername());
    }
}
