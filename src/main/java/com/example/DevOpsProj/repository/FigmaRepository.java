package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.Figma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FigmaRepository extends JpaRepository<Figma, Long> {
}
