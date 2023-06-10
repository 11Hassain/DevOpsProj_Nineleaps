package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
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
    List<User> findAllUsersByProjectId(@Param("projectId") Long projectId);


    @Query("SELECT u FROM User u JOIN u.projects p WHERE p.projectId=?1 AND u.id=?2")
    List<User> existUserInProject(Long projectId, Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.deleted=true WHERE p.projectId=?1") //setting is_deleted to true
    void softDeleteProject(Long id);


    @Query("SELECT count(p) FROM Project p WHERE p.deleted=false")
    Integer countAllProjects();

    @Query("SELECT count(p) FROM Project p " +
            "JOIN p.users u WHERE u.enumRole = :role " +
            "AND p.deleted=false")
    Integer countAllProjectsByRole(EnumRole role);

    @Query("SELECT count(p) FROM Project p JOIN p.users u WHERE u.id = :userId")
    Integer countAllProjectsByUserId(Long userId);

    @Query("SELECT u FROM User u JOIN u.projects p " +
            "WHERE p.id = :projectId " +
            "AND u.enumRole = :role")
    List<User> findAllUsersByProjectIdAndRole(@Param("projectId") Long projectId, @Param("role") EnumRole role);

    @Query("SELECT count(u) FROM User u JOIN u.projects p " +
            "WHERE p.id = :projectId " +
            "AND u.enumRole = :role")
    Integer countAllUsersByProjectIdAndRole(@Param("projectId") Long projectId, @Param("role") EnumRole role);

    @Query("SELECT count(u) FROM User u JOIN u.projects p WHERE p.id = :projectId")
    Integer countAllUsersByProjectId(Long projectId);

    @Query("SELECT count(p) FROM Project p WHERE p.deleted=false")
    Integer countAllActiveProjects();

    @Query("SELECT count(p) FROM Project p WHERE p.deleted=true")
    Integer countAllInActiveProjects();
}
