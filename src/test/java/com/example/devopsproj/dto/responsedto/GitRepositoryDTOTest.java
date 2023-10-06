package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GitRepositoryDTOTest {

    @Test
    void testNoArgsConstructor() {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();

        assertNotNull(gitRepositoryDTO);

        assertNull(gitRepositoryDTO.getRepoId());
        assertNull(gitRepositoryDTO.getName());
        assertNull(gitRepositoryDTO.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO(1L, "RepoName", "Description");

        assertEquals(1L, gitRepositoryDTO.getRepoId());
        assertEquals("RepoName", gitRepositoryDTO.getName());
        assertEquals("Description", gitRepositoryDTO.getDescription());
    }

    @Test
    void testSetterGetter() {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();

        gitRepositoryDTO.setRepoId(2L);
        gitRepositoryDTO.setName("NewRepo");
        gitRepositoryDTO.setDescription("New Description");

        assertEquals(2L, gitRepositoryDTO.getRepoId());
        assertEquals("NewRepo", gitRepositoryDTO.getName());
        assertEquals("New Description", gitRepositoryDTO.getDescription());
    }

    @Test
    void testCustomConstructor() {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO("CustomRepo", "Custom Description");

        assertNull(gitRepositoryDTO.getRepoId());
        assertEquals("CustomRepo", gitRepositoryDTO.getName());
        assertEquals("Custom Description", gitRepositoryDTO.getDescription());
    }
}
