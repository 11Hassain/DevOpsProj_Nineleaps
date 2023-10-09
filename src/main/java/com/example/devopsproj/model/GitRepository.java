package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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
