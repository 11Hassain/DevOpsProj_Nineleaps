package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    @Query("SELECT a FROM AccessRequest a WHERE a.updated=false")
    List<AccessRequest> findAllActiveRequests();
}
