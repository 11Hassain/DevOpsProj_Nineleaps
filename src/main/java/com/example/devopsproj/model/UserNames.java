package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
