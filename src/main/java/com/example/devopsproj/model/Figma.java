package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * The Figma class represents Figma-related information for a project. It stores data such as the Figma URL,
 * users associated with the Figma project, and screenshots of the Figma project by user.
 * .
 * This class is mapped to a database table named "figma" and is part of a model for a DevOps project.
 *
 * @version 2.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "figma")
public class Figma implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long figmaId;

    @Column(name = "figma_url", nullable = false)
    private String figmaURL;

    @Column(name = "users")
    private String user;

    @ElementCollection
    @CollectionTable(name = "figma_screenshots", joinColumns = @JoinColumn(name = "figma_id"))
    @MapKeyColumn(name = "user")  // Add this annotation to use 'user' as the map key
    @Column(name = "screenshot_image", length = 10485760)
    private Map<String, String> screenshotImagesByUser;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
}


