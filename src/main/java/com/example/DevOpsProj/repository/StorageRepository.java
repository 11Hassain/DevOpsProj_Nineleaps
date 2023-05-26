package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<FileData, Long> {

    Optional<FileData> findFileById(Long fileId);
}
