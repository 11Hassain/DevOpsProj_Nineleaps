package com.example.devopsproj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class AccessRequest {

    @Id
    @GeneratedValue
    private Long accessRequestId;

    @Column(nullable = false)
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
