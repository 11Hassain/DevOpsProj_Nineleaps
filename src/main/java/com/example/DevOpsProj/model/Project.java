package com.example.DevOpsProj.model;

import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "project_description")
    private String projectDescription;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<GitRepository> repositories;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<AccessRequest> accessRequest;

    @Column(name = "is_deleted")
    private Boolean deleted=false;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastUpdated=LocalDateTime.now();

    //connecting project with user entity (project is owning side)
@JsonIgnore
@ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users; //change into list

    @OneToOne(mappedBy = "project")
    private Figma figma;

    @OneToOne(mappedBy = "project")
    private GoogleDrive googleDrive;

    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

}
