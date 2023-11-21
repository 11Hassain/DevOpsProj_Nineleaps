package com.example.devopsproj.repository;

import com.example.devopsproj.model.GoogleDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for managing {@link GoogleDrive} entities, providing methods
 * for retrieving Google Drive records based on project ID and those not marked as deleted.
 */

@Repository
public interface GoogleDriveRepository extends JpaRepository<GoogleDrive, Long> {

    @Query("SELECT g FROM GoogleDrive g WHERE g.project.projectId = :projectId")
    Optional<GoogleDrive> findGoogleDriveByProjectId(@Param("projectId") Long projectId);

    List<GoogleDrive> findByDeletedFalse();
}
