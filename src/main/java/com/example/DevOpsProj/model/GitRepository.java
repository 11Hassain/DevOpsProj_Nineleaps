package com.example.DevOpsProj.model;

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

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "projectId")
    private Project project;
    public Long getRepoId() {
        return repoId;
    }
//    @ManyToMany(mappedBy = "repositories")
//    private List<UserNames> usernames;

    @ManyToMany
    @JoinTable(name = "repository_username",
            joinColumns = @JoinColumn(name = "repository_id"),
            inverseJoinColumns = @JoinColumn(name = "username_id"))
    private List<UserNames> usernames;

}
