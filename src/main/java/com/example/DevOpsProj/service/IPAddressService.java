package com.example.DevOpsProj.service;

import com.example.DevOpsProj.model.IPAddress;
import com.example.DevOpsProj.repository.IpAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class IPAddressService {

    @Autowired
    private IpAddressRepository repository;

    public IPAddress saveIPAddress(@RequestBody IPAddress ipAddress){
        return repository.save(ipAddress);
    }
}
