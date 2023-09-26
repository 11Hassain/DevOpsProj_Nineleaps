package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.commons.enumerations.EnumRole;

import java.util.List;

public interface GitRepositoryService {

    GitRepository createRepository(GitRepository gitRepository);

    List<GitRepositoryDTO> getAllRepositories();

    void deleteRepository(Long repoId);

    List<GitRepositoryDTO> getAllRepositoriesByProject(Long id);

    List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole);

    GitRepository getRepositoryById(Long id);

    boolean isAccessTokenValid(String accessToken);
}
