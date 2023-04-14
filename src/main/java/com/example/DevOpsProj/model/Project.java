package com.example.DevOpsProj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "project")
public class Project {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long projectId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "project_description")
    private String projectDescription;

    @Column(name = "is_deleted")
    private Boolean deleted=false;
//
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



    //connecting project with user entity (project is owning side)
    @JsonIgnore
    @ManyToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinTable(name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users; //change into list

}
