package com.example.DevOpsProj.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "document_category")
public class DocumentCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category")
    private String category;

    // Default categories
    public static DocumentCategory additionalDetailsFile() {
        DocumentCategory documentCategory = new DocumentCategory();
        documentCategory.setCategory("additionalDetailsFile");
        return documentCategory;
    }

    public static DocumentCategory executiveSummaryFile() {
        DocumentCategory documentCategory = new DocumentCategory();
        documentCategory.setCategory("executiveSummaryFile");
        return documentCategory;
    }

    public static DocumentCategory financialDetailsFile() {
        DocumentCategory documentCategory = new DocumentCategory();
        documentCategory.setCategory("financialDetailsFile");
        return documentCategory;
    }

    public static DocumentCategory teamDetailsFile() {
        DocumentCategory documentCategory = new DocumentCategory();
        documentCategory.setCategory("teamDetailsFile");
        return documentCategory;
    }

    public static DocumentCategory businessModelFile() {
        DocumentCategory documentCategory = new DocumentCategory();
        documentCategory.setCategory("businessModelFile");
        return documentCategory;
    }
}