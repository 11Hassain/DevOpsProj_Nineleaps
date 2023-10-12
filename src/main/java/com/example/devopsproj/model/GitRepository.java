package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * The GitRepository class represents a GitHub repository associated with a project, usernames of users. It stores
 * information such as the repository name, description, the project it belongs to, and a list of usernames
 * related to the repository.
 * .
 * This class is mapped to a database table named "repositories" and is part of a model for a DevOps project.
 *
 * @version 2.0
 */

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "repositories")
public class GitRepository implements Serializable {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long repoId;

    @Column(name = "repo_name", nullable = false, length = 50)
    private String name;

    @Column(name = "repo_description")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id", referencedColumnName = "projectId")
    private Project project;

    @ManyToMany
    @JoinTable(name = "repository_username",
            joinColumns = @JoinColumn(name = "repository_id"),
            inverseJoinColumns = @JoinColumn(name = "username_id"))
    private List<UserNames> usernames;

}
