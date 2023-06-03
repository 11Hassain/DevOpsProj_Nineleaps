package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.deleted=false")
    List<Project> findAllProjects();

    @Query("SELECT u FROM User u JOIN u.projects p WHERE p.id = :projectId")
    List<User> findAllUsersByProjectId(@Param("projectId") Long projectId); //get all users for particular project id

    @Query("SELECT u FROM User u JOIN u.projects p WHERE p.projectId=?1 AND u.id=?2")
    List<User> existUserInProject(Long projectId, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.deleted=true WHERE p.projectId=?1") //setting is_deleted to true
    void softDeleteProject(Long id);


}
