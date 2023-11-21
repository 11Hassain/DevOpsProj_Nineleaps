package com.example.devopsproj.repository;

import com.example.devopsproj.model.HelpDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
/**
 * Repository interface for managing {@link HelpDocuments} entities, providing methods
 * for retrieving Help Documents records based on file name and those not marked as deleted.
 */

public interface HelpDocumentsRepository extends JpaRepository<HelpDocuments, Long> {
    @Query("SELECT h FROM HelpDocuments h WHERE h.fileName = ?1")
    HelpDocuments findByFileName(String fileName);

    List<HelpDocuments> findByDeletedFalse();
}
