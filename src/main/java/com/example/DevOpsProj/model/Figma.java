package com.example.DevOpsProj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//
//import java.io.IOException;
//import java.sql.Blob;
//import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "figma")
public class Figma {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long figmaId;

//    @Column(name = "project_name", nullable = false)
//    private String projectName;

    @Column(name = "figma_url", nullable = false)
    private String figmaURL;

    @Column(name = "users")
    private String user;

    @Column(name = "screenshot_image", length = 10485760)
    private String screenshotImage;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
}


