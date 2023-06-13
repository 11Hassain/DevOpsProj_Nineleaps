package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.GitRepository;
import com.example.DevOpsProj.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface GitRepositoryRepository extends JpaRepository<GitRepository, Long> {

    @Query("SELECT r FROM GitRepository r WHERE r.project = :project")
    List<GitRepository> findRepositoriesByProject(@Param("project") Project project);
}