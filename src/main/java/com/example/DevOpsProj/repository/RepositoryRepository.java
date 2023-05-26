package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RepositoryRepository extends JpaRepository<Repository, Long> {

}