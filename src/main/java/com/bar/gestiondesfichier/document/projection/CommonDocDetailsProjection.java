package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDateTime;

/**
 * CommonDocDetails projection for common document details
 */
public interface CommonDocDetailsProjection extends BaseDocumentRelatedProjection {



    String getReference();
    String getDescription();
    
    String getVersion();
    LocalDateTime getExpirationDate();
    SectionCategoryInfo getSectionCategory();
}