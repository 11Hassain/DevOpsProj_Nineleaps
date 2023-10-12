package com.example.devopsproj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The Project class represents a project within a DevOps system. It stores information such as the project's identifier,
 * name, description, associated Git repositories, access requests, deletion status, last update timestamp, users, Figma,
 * and Google Drive associations.
 * .
 * This class is mapped to a database table named "project" and is part of a model for a DevOps project.
 *
 * @version 2.0
 */

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "project")
public class Project implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Getter
    @Column(name = "project_name", nullable = false, length = 50)
    private String projectName;

    @Column(name = "project_description")
    private String projectDescription;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<GitRepository> repositories;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<AccessRequest> accessRequest;

    @Getter
    @Column(name = "is_deleted")
    private Boolean deleted=false;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastUpdated=LocalDateTime.now();

    //connecting project with user entity (project is owning side)
    @JsonIgnore
    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @OneToOne(mappedBy = "project")
    private Figma figma;

    @JsonIgnore
    @OneToOne(mappedBy = "project")
    private GoogleDrive googleDrive;

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
