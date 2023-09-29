package com.example.devopsproj.repository;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.deleted = true WHERE u.id=?1")
    void softDelete(Long id);

    @Query("SELECT u FROM User u WHERE u.deleted=false")
    List<User> findAllUsers();

    @Query("SELECT u FROM User u WHERE u.deleted=false AND u.enumRole=?1")
    List<User> findAllUsersByRole(EnumRole enumRole);

    @Query("SELECT u FROM User u WHERE u.enumRole=:enumRole AND u.deleted=false")
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
            "AND u.enumRole = :userRole " +
            "AND p.deleted = false")
    List<Project> findByRoleAndUserId(Long userId, EnumRole userRole);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User existsByEmail(String email);

    @Query("SELECT t.user FROM Token t WHERE t.token = :tokenValue")
    User findUserByToken(@Param("tokenValue") String tokenValue);

    User findByPhoneNumber(String phoneNumber);

    List<User> findByEnumRole(EnumRole enumRole);


}
