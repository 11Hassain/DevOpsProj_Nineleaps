package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.Getter;


/**
 * The Collaborator class represents a record of a user (username) collaborating on a specific
 * repository (repo) owned by a user or organization (owner).
 * .
 * This class is mapped to a database table named "collaborators" and is part of a model for a DevOps project.
 *
 * @version 2.0
 */

@Getter
@Entity
@Table(name = "collaborators")
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner")
    private String owner;

    @Column(name = "repo")
    private String repo;

    @Column(name = "username")
    private String username;


    public void setId(Long id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

