package com.example.devopsproj.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * The HelpDocuments class represents documents that help saving files, docs for a project. It stores information
 * such as the document's identifier, data (content), file name, category, file extension, and the project it is associated
 * with.
 * .
 * This class is mapped to a database table named "help_documents" and is part of a model for a DevOps project.
 *
 * @version 2.0
 */

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "help_documents")
public class HelpDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "help_document_id")
    private Long helpDocumentId;

    @Lob
    @Column(name = "data", columnDefinition = "BLOB")
    private byte[] data;

    @Column(name = "file_name", unique = true, length = 50)  // Add unique constraint annotation
    private String fileName;

    @Column(name = "category")
    private String category;

    @Column(name = "image")
    private String fileExtension;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}