package com.example.devopsproj.model;
import com.example.devopsproj.model.Collaborator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class CollaboratorTest {

    @Test
     void testCollaboratorInitialization() {
        Collaborator collaborator = new Collaborator();

        assertNull(collaborator.getId());
        assertNull(collaborator.getOwner());
        assertNull(collaborator.getRepo());
        assertNull(collaborator.getUsername());
    }

    @Test
     void testCollaboratorSetterGetter() {
        Long id = 1L;
        String owner = "John";
        String repo = "exampleRepo";
        String username = "johndoe";

        Collaborator collaborator = new Collaborator();
        collaborator.setId(id);
        collaborator.setOwner(owner);
        collaborator.setRepo(repo);
        collaborator.setUsername(username);

        assertEquals(id, collaborator.getId());
        assertEquals(owner, collaborator.getOwner());
        assertEquals(repo, collaborator.getRepo());
        assertEquals(username, collaborator.getUsername());
    }


}
