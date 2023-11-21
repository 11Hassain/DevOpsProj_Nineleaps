package com.example.devopsproj.repository;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.UserNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Repository interface for managing {@link UserNames} entities, providing methods
 * for retrieving user names based on user roles.
 */

@Repository
public interface UserNamesRepository extends JpaRepository<UserNames, Long> {

    @Query("SELECT us FROM UserNames us " +
            "JOIN us.user u " +
            "WHERE u.enumRole = :role")
    List<UserNames> findByUserRole(@Param("role") EnumRole role);
}