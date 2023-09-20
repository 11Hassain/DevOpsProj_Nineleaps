package com.exAmple.DevOpsProj.repository;

import com.exAmple.DevOpsProj.model.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {

    @Query("SELECT a FROM AccessRequest a WHERE a.updated=false")
    List<AccessRequest> findAllActiveRequests();

    @Query("SELECT a FROM AccessRequest a WHERE a.pmName = :pmName AND " +
            "a.updated = true AND a.pmNotified = false")
    List<AccessRequest> findAllUnreadPMRequestsByName(@Param("pmName") String pmName);

    @Query("SELECT a FROM AccessRequest a WHERE a.pmName = :pmName")
    List<AccessRequest> findAllPMRequestsByName(@Param("pmName") String pmName);


}
