package com.bar.gestiondesfichier.document.model;

import com.bar.gestiondesfichier.location.model.Section;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "common_doc_details")
@Getter
@Setter
public class CommonDocDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Long auto-increment
    private Long id;

    private String reference;
    private String description;
    private String status;
    private LocalDateTime dateTime;
    private String version;
    private LocalDateTime expirationDate;

    // Many CommonDocDetails can belong to one Section
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id") // maps to section table's PK
    private Section section;
}