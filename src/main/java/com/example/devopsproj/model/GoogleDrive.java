package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * The GoogleDrive class represents a Google Drive link associated with a project. It stores information such as the
 * Google Drive link and the project to which it is related.
 * .
 * This class is mapped to a database table named "google_drive" and is part of a model for a DevOps project.
 *
 * @version 2.0
 */

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