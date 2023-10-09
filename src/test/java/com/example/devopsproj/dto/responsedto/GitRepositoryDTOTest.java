package com.example.devopsproj.dto.responsedto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GitRepositoryDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidGitRepositoryDTO() {
        // Arrange
        Long repoId = 1L;
        String name = "MyRepo";
        String description = "A sample repository";

        // Act
        GitRepositoryDTO dto = new GitRepositoryDTO();
        dto.setRepoId(repoId);
        dto.setName(name);
        dto.setDescription(description);

        // Assert
        assertEquals(repoId, dto.getRepoId());
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
    }

    @Test
    void testNoArgConstructor() {
        GitRepositoryDTO dto = new GitRepositoryDTO();

        assertNotNull(dto);

        assertNull(dto.getRepoId());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        Long repoId = 2L;
        String name = "AnotherRepo";
        String description = "Another sample repository";

        GitRepositoryDTO dto = new GitRepositoryDTO(repoId, name, description);

        assertEquals(repoId, dto.getRepoId());
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
    }

    @Test
    void testSetterGetter() {
        GitRepositoryDTO dto = new GitRepositoryDTO();

        Long repoId = 3L;
        String name = "Repo3";
        String description = "Description for Repo3";

        dto.setRepoId(repoId);
        dto.setName(name);
        dto.setDescription(description);

        assertEquals(repoId, dto.getRepoId());
        assertEquals(name, dto.getName());
        assertEquals(description, dto.getDescription());
    }
    @Test
    void testCustomConstructor() {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO("CustomRepo", "Custom Description");

        assertNull(gitRepositoryDTO.getRepoId());
        assertEquals("CustomRepo", gitRepositoryDTO.getName());
        assertEquals("Custom Description", gitRepositoryDTO.getDescription());
    }

    @Test
    void testToString() {
        Long repoId = 4L;
        String name = "Repo4";
        String description = "Description for Repo4";

        GitRepositoryDTO dto = new GitRepositoryDTO(repoId, name, description);

        String expectedToString = "GitRepositoryDTO(repoId=4, name=Repo4, description=Description for Repo4)";
        assertEquals(expectedToString, dto.toString());
    }

    @Test
    void testPositiveRepoId() {
        GitRepositoryDTO dto = new GitRepositoryDTO(-1L, "InvalidRepo", "Invalid description");

        Set<ConstraintViolation<GitRepositoryDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("repoId should be a positive number", violations.iterator().next().getMessage());
    }

    @Test
    void testNameNotBlank() {
        GitRepositoryDTO dto = new GitRepositoryDTO(null, "", "Description");

        Set<ConstraintViolation<GitRepositoryDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name cannot be blank", violations.iterator().next().getMessage());
    }

}
