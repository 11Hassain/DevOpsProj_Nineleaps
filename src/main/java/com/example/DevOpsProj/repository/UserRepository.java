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
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.deleted = true WHERE u.id=?1")
    void softDelete(Long id);

    @Query("SELECT u FROM User u WHERE u.enumRole=?1 AND u.deleted=false")
    List<User> findByRole(EnumRole enumRole);

    public User findByEmail(String email);

    @Query("SELECT count(u) FROM User u WHERE u.deleted=false")
    Integer countAllUsers();

    @Query("SELECT count(u) FROM User u WHERE u.deleted=false AND u.enumRole=?1")
    Integer countAllUsersByRole(EnumRole role);

    @Query("SELECT count(u) FROM User u " +
            "JOIN u.projects p WHERE p.id = :projectId " +
            "AND u.deleted=false")
    Integer countAllUsersByProjectId(Long projectId);

    @Query("SELECT p FROM Project as p " +
            "JOIN p.users as u " +
            "WHERE u.id = :userId " +
            "AND u.enumRole = :userRole")
    List<Project> findByRoleAndUserId(Long userId, EnumRole userRole);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User existsByEmail(String email);

    @Query("SELECT t.user FROM Token t WHERE t.token = :tokenValue")
    User findUserByToken(@Param("tokenValue") String tokenValue);
}
