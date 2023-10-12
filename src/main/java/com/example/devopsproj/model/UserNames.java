package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The UserNames class represents a user's usernames, such as a GitHub username, associated with a DevOps user. It stores
 * information such as the unique identifier, the GitHub username, the associated user, and repositories associated with
 * these usernames.
 * .
 * This class is mapped to a database table named "user_names" and is part of the model for a DevOps project.
 *
 * @version 2.0
 */

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_names")
public class UserNames implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "username_id", nullable = false)
    private Long id;

    @Column(name = "github_username", nullable = false)
    private String username;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToMany(mappedBy = "usernames")
    private List<GitRepository> repositories = new ArrayList<>();

}
