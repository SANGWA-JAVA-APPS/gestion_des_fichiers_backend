package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDateTime;

/**
 * DueDiligence projection for due diligence processes
 */
public interface DueDiligenceProjection extends BaseDocumentRelatedProjection {
    String getReference();
    String getDescription();
    LocalDateTime getDateDueDiligence();
    String getAuditor();
    LocalDateTime getCreationDate();
    LocalDateTime getCompletionDate();
    String getDocAttach();
    
    SectionInfo getSection();
    
    interface SectionInfo {
        Long getId();
        String getName();
    }
}