package com.example.DevOpsProj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "google_drive")
public class GoogleDrive {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long driveId;

    @Column(name = "drive_link", nullable = false)
    private String driveLink;
    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
}