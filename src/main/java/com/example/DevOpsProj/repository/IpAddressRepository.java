package com.example.DevOpsProj.repository;

import com.example.DevOpsProj.model.IPAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpAddressRepository extends JpaRepository<IPAddress, Long> {
}
