package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;


/**
 * Entity representing Figma details in the database.
 */
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

    @Column(name = "deleted")
    public Boolean deleted = false;

    @ElementCollection
    @CollectionTable(name = "figma_screenshots", joinColumns = @JoinColumn(name = "figma_id"))
    @MapKeyColumn(name = "user")  // Add this annotation to use 'user' as the map key
    @Column(name = "screenshot_image", length = 10485760)
    private Map<String, String> screenshotImagesByUser;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;



    public Figma(Long figmaId, String figmaURL, String user, Map<String, String> screenshotImagesByUser, Project project) {
        this.figmaId = figmaId;
        this.figmaURL = figmaURL;
        this.user = user;
        this.screenshotImagesByUser = screenshotImagesByUser;
        this.project = project;
    }
}


