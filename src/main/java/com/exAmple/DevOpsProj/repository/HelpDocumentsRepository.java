package com.exAmple.DevOpsProj.repository;

import com.exAmple.DevOpsProj.model.HelpDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HelpDocumentsRepository extends JpaRepository<HelpDocuments, Long> {
    @Query("SELECT h FROM HelpDocuments h WHERE h.fileName = ?1")
    HelpDocuments findByFileName(String fileName);
}
