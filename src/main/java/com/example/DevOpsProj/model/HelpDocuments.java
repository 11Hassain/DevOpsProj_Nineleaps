package com.example.DevOpsProj.model;

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

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "category")
    private String category;

    @Column(name = "image")
    private String fileExtension;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

//    public static final DocumentCategory PROJECT_FILE = createDefaultCategory(1L, "projectFile");
//
//    private static DocumentCategory createDefaultCategory(Long id, String category) {
//        DocumentCategory documentCategory = new DocumentCategory();
//        documentCategory.setId(id);
//        documentCategory.setCategory(category);
//        return documentCategory;
//    }
}