package com.bar.gestiondesfichier.document.model;

import com.bar.gestiondesfichier.common.entity.BaseEntity;
import com.bar.gestiondesfichier.entity.Account;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Base class for all document-related entities Contains common fields:
 * date_time, doneby, docId
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DocumentRelatedEntity extends BaseEntity {

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doneby", nullable = false)
    private Account doneBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doc_id", nullable = true)
    private Document document;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "statut_id", nullable = true)
    private DocStatus status;

    // Default constructor
    public DocumentRelatedEntity() {
        super();
        this.dateTime = LocalDateTime.now();
    }

    // Constructor with required fields (document and status are optional)
    public DocumentRelatedEntity(Account doneBy, Document document, DocStatus status) {
        this();
        this.doneBy = doneBy;
        this.document = document; // Can be null
        this.status = status; // Can be null
    }
}
