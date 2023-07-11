package com.example.DevOpsProj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.management.relation.Role;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_names")
public class UserNames {

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
    private List<GitRepository> repositories;

}
