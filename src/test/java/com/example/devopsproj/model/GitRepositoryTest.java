package com.example.devopsproj.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GitRepositoryTest {

    @Test
    void testGitRepositoryGettersAndSetters() {
        GitRepository gitRepository = new GitRepository();

        gitRepository.setRepoId(1L);
        gitRepository.setName("SampleRepo");
        gitRepository.setDescription("Sample description");

        assertEquals(1L, gitRepository.getRepoId());
        assertEquals("SampleRepo", gitRepository.getName());
        assertEquals("Sample description", gitRepository.getDescription());
    }

    @Test
    void testGitRepositoryProjectAssociation() {
        GitRepository gitRepository = new GitRepository();

        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Sample Project");

        gitRepository.setProject(project);

        assertEquals(project, gitRepository.getProject());
    }

    @Test
    void testGitRepositoryUsernamesAssociation() {
        GitRepository gitRepository = new GitRepository();

        List<UserNames> usernames = new ArrayList<>();
        UserNames user1 = new UserNames();
        user1.setUsername("user1");
        UserNames user2 = new UserNames();
        user2.setUsername("user2");
        usernames.add(user1);
        usernames.add(user2);

        gitRepository.setUsernames(usernames);

        assertEquals(usernames, gitRepository.getUsernames());
    }
}
