package com.bar.gestiondesfichier.document.model;

import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.location.model.Section;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "common_doc_details")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class CommonDocDetails  extends DocumentRelatedEntity {

    private String reference;
    private String description;
    private LocalDateTime expirationDate;
    public CommonDocDetails() {
        super();
    }
    // Many CommonDocDetails can belong to one Section
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_category_id", nullable = false)
    private SectionCategory sectionCategory;


}