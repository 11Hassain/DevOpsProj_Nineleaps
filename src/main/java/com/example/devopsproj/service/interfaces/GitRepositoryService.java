package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GitRepositoryService {
    @Transactional
    GitRepository createRepository(GitRepository gitRepository);

    @Transactional(readOnly = true)
    List<GitRepositoryDTO> getAllRepositories();

    @Transactional
    void deleteRepository(Long repoId);

    List<GitRepositoryDTO> getAllRepositoriesByProject(Long id);

    List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole);

    @Transactional(readOnly = true)
    GitRepository getRepositoryById(Long id);
}
