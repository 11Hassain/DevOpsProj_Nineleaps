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
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.deleted = true WHERE u.id=?1")
    void softDelete(Long id);

    @Query("SELECT u FROM User u WHERE u.enumRole=?1 AND u.deleted=false")
    List<User> findByRole(EnumRole enumRole);

    public User findByEmail(String email);

}
