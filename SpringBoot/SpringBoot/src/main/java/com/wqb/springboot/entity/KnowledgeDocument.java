package com.wqb.springboot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_document")
public class KnowledgeDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String title;

    @Column(nullable = false, length = 32)
    private String category;

    @Column(nullable = false, length = 255)
    private String summary;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false, length = 255)
    private String tags;

    @Column(nullable = false, length = 32)
    private String sourceType;

    @Column(nullable = false)
    private Boolean ragReady;

    private LocalDateTime lastIndexedAt;

    public KnowledgeDocument() {
    }

    public KnowledgeDocument(
            String title,
            String category,
            String summary,
            String content,
            String tags,
            String sourceType,
            Boolean ragReady,
            LocalDateTime lastIndexedAt
    ) {
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.content = content;
        this.tags = tags;
        this.sourceType = sourceType;
        this.ragReady = ragReady;
        this.lastIndexedAt = lastIndexedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getSummary() {
        return summary;
    }

    public String getContent() {
        return content;
    }

    public String getTags() {
        return tags;
    }

    public String getSourceType() {
        return sourceType;
    }

    public Boolean getRagReady() {
        return ragReady;
    }

    public LocalDateTime getLastIndexedAt() {
        return lastIndexedAt;
    }
}
