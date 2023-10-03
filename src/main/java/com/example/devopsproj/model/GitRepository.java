package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "repositories")
public class GitRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long repoId;

    @Column(name = "repo_name", nullable = false)
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

    public Long getRepoId() {
        return repoId;
    }
    public GitRepository(String name) {
        this.name = name;
        // Initialize other properties as needed
    }
    public GitRepository(Long repoId, String name, String description) {
        this.repoId = repoId;
        this.name = name;
        this.description = description;
        // Initialize other properties as needed
    }


}

