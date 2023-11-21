package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.commons.enumerations.EnumRole;

import java.util.List;
/**
 * Service interface for managing Git repository-related operations, including
 * creation, retrieval, deletion, and listing repositories by project or role.
 */

public interface GitRepositoryService {

    GitRepository createRepository(GitRepository gitRepository);

    List<GitRepositoryDTO> getAllRepositories();

    void deleteRepository(Long repoId);

    List<GitRepositoryDTO> getAllRepositoriesByProject(Long id);

    List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole);

   GitRepository getRepositoryById(Long id);


}
