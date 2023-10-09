package com.example.devopsproj.model;

import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.UserNames;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GitRepositoryTest {

    @Test
    public void testAllArgsConstructor() {
        Long repoId = 1L;
        String name = "Repository1";
        String description = "Description1";
        Project project = new Project(); // You can create a Project instance here
        List<UserNames> usernames = new ArrayList<>(); // You can create UserNames instances here

        GitRepository gitRepository = new GitRepository(repoId, name, description, project, usernames);

        assertEquals(repoId, gitRepository.getRepoId());
        assertEquals(name, gitRepository.getName());
        assertEquals(description, gitRepository.getDescription());
        assertEquals(project, gitRepository.getProject());
        assertEquals(usernames, gitRepository.getUsernames());
    }

    @Test
    public void testNoArgsConstructor() {
        GitRepository gitRepository = new GitRepository();

        assertNull(gitRepository.getRepoId());
        assertNull(gitRepository.getName());
        assertNull(gitRepository.getDescription());
        assertNull(gitRepository.getProject());
        assertNull(gitRepository.getUsernames());
    }

    @Test
    public void testGetterSetter() {
        GitRepository gitRepository = new GitRepository();

        Long repoId = 1L;
        String name = "Repository1";
        String description = "Description1";
        Project project = new Project(); // You can create a Project instance here
        List<UserNames> usernames = new ArrayList<>(); // You can create UserNames instances here

        gitRepository.setRepoId(repoId);
        gitRepository.setName(name);
        gitRepository.setDescription(description);
        gitRepository.setProject(project);
        gitRepository.setUsernames(usernames);

        assertEquals(repoId, gitRepository.getRepoId());
        assertEquals(name, gitRepository.getName());
        assertEquals(description, gitRepository.getDescription());
        assertEquals(project, gitRepository.getProject());
        assertEquals(usernames, gitRepository.getUsernames());
    }
    @Test
    void testGitRepositoryWithProject() {
        GitRepository gitRepository = new GitRepository();

        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Sample Project");

        gitRepository.setProject(project);

        assertEquals(project, gitRepository.getProject());
    }

    @Test
    void testGitRepositoryWithUsername() {
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
