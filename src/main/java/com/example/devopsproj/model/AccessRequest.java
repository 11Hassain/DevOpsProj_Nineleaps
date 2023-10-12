package com.example.devopsproj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * The AccessRequest class represents a request for access to a project by a project manager (PM).
 * It stores information about the access request, including the PM's name, the associated user,
 * the project to which access is requested, a description of the request, and related status flags.
 * .
 * This class is mapped to a database table and is part of a model for a DevOps project.
 *
 * @version 2.0
 */

@Setter
@Getter
@ToString
@NoArgsConstructor
@Entity
public class AccessRequest implements Serializable{

    @Id
    @GeneratedValue
    private Long accessRequestId;

    @Column(nullable = false, length = 50)
    private String pmName;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "description")
    private String requestDescription;

    private boolean allowed = false;

    private boolean updated = false;

    private boolean pmNotified = false;

}
