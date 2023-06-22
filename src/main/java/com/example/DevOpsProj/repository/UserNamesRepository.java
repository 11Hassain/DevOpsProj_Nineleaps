package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.model.UserNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserNamesRepository extends JpaRepository<UserNames, Long> {

    @Query("SELECT us FROM UserNames us " +
            "JOIN us.user u " +
            "WHERE u.enumRole = :role")
    List<UserNames> findByUserRole(@Param("role") EnumRole role);
}