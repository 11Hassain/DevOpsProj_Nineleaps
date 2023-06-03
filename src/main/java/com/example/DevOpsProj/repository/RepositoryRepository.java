package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RepositoryRepository extends JpaRepository<Repository, Long> {

    @Query("SELECT r FROM Repository r WHERE r.project = :project")
    List<Repository> findRepositoriesByProject(@Param("project") Project project);
}