package com.example.DevOpsProj.model;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

//    @Column(name = "password")
//    private String password;

    @Enumerated(EnumType.STRING)
    private EnumRole enumRole;

    @Column(name = "is_deleted")
    private Boolean deleted = false;

    public Boolean getDeleted() { //getter for deleted
        return deleted;
    }
    public void setDeleted(boolean deleted) { //setter for deleted
        this.deleted = deleted;
    }


    //linking project entity with user
    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private List<Project> projects;
}
