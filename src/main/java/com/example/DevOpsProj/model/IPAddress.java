package com.example.DevOpsProj.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class IPAddress {

    @Id
    @GeneratedValue
    private Long ipAddressId;
    private String ipAddress;

}
