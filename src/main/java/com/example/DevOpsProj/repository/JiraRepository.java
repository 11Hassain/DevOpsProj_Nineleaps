package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.Jira;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JiraRepository extends JpaRepository<Jira, Long> {
}

