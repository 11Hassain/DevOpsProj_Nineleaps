package com.example.devopsproj.repository;

import com.example.devopsproj.model.HelpDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HelpDocumentsRepository extends JpaRepository<HelpDocuments, Long> {
    @Query("SELECT h FROM HelpDocuments h WHERE h.fileName = ?1")
    HelpDocuments findByFileName(String fileName);

    List<HelpDocuments> findByDeletedFalse();
}
