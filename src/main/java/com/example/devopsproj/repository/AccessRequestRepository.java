package com.example.devopsproj.repository;

import com.example.devopsproj.model.AccessRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Repository interface for managing {@link AccessRequest} entities, providing methods
 * for retrieving access requests with various criteria such as active, unread by a project manager,
 * all by a project manager, and those not marked as deleted.
 */
@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    @Query("SELECT a FROM AccessRequest a WHERE a.updated = false")
    Page<AccessRequest> findAllActiveRequests(Pageable pageable);

    @Query("SELECT a FROM AccessRequest a WHERE a.pmName = :pmName AND " +
            "a.updated = true AND a.pmNotified = false")
    List<AccessRequest> findAllUnreadPMRequestsByName(@Param("pmName") String pmName);

    @Query("SELECT a FROM AccessRequest a WHERE a.pmName = :pmName")
    List<AccessRequest> findAllPMRequestsByName(@Param("pmName") String pmName);

    List<AccessRequest> findByDeletedFalse();
}
