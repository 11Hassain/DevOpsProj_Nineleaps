package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.model.IPAddress;
import com.example.DevOpsProj.service.IPAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ipAddress")
public class IPAddressController {

    @Autowired
    private IPAddressService service;

    @PostMapping("/")
    public ResponseEntity<Object> saveIPAddress(@RequestBody IPAddress ipAddress){
        IPAddress ip = service.saveIPAddress(ipAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(ip);
    }
}
