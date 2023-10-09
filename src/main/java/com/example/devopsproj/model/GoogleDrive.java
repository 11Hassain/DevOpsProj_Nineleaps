package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "google_drive")
public class GoogleDrive implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long driveId;

    @Column(name = "drive_link", nullable = false)
    private String driveLink;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
}