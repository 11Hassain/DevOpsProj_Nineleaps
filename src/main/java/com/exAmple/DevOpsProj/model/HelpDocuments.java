package com.exAmple.DevOpsProj.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
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