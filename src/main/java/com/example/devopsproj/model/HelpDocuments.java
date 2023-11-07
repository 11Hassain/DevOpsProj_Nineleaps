package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.*;



@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "help_documents",
        uniqueConstraints = @UniqueConstraint(columnNames = {"file_name"}))
public class HelpDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "help_document_id")
    private Long helpDocumentId;

    @Lob
    @Column(name = "data", columnDefinition = "BLOB")
    private byte[] data;

    @Column(name = "file_name", unique = true)  // Add unique constraint annotation
    private String fileName;

    @Column(name = "category")
    private String category;

    @Column(name = "image")
    private String fileExtension;

    @Column(name = "deleted")
    private boolean deleted = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;


    public HelpDocuments(Long helpDocumentId, byte[] data, String fileName, String category, String fileExtension, Project project) {
        this.helpDocumentId = helpDocumentId;
        this.data = data;
        this.fileName = fileName;
        this.category = category;
        this.fileExtension = fileExtension;
        this.project = project;
    }
}