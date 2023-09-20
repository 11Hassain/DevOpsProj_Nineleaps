package com.example.devopsproj.repository;

import com.example.devopsproj.model.GoogleDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleDriveRepository extends JpaRepository<GoogleDrive, Long> {

    @Query("SELECT g FROM GoogleDrive g WHERE g.project.projectId = :projectId")
    Optional<GoogleDrive> findGoogleDriveByProjectId(@Param("projectId") Long projectId);
}
