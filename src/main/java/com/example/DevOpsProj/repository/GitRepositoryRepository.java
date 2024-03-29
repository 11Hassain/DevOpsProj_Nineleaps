package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.model.GitRepository;
import com.example.DevOpsProj.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GitRepositoryRepository extends JpaRepository<GitRepository, Long> {

    @Query("SELECT r FROM GitRepository r WHERE r.project = :project")
    List<GitRepository> findRepositoriesByProject(@Param("project") Project project);

    @Query("SELECT r FROM GitRepository r " +
            "JOIN r.project p " +
            "JOIN p.users u " +
            "WHERE u.enumRole = :enumRole")
    List<GitRepository> findAllByRole(EnumRole enumRole);

    Optional<GitRepository> findByRepoId(Long repoId);

}