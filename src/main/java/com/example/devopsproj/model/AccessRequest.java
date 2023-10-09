package com.example.devopsproj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

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
