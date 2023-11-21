package com.example.devopsproj.repository;

import com.example.devopsproj.model.Figma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Repository interface for managing {@link Figma} entities, providing methods
 * for retrieving figma records based on project ID and those not marked as deleted.
 */
@Repository
public interface FigmaRepository extends JpaRepository<Figma, Long> {
    @Query("SELECT f FROM Figma f WHERE f.project.id = :projectId")
    Optional<Figma> findFigmaByProjectId(@Param("projectId") Long projectId);

    List<Figma> findByDeletedFalse();
}
