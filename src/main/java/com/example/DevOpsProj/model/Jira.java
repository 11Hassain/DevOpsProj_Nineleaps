package com.example.DevOpsProj.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
public class Jira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String jiraKey;
    private String projectTypeKey;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
}