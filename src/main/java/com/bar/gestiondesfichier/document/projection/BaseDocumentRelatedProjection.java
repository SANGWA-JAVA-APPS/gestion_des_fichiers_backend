package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDateTime;

/**
 * Base projection interface for common fields in document-related entities
 */
public interface BaseDocumentRelatedProjection {
    Long getId();
    LocalDateTime getDateTime();
    
    // Document information
    DocumentInfo getDocument();
    
    // Status information
    StatusInfo getStatus();
    
    // Done by information
    DoneByInfo getDoneBy();
    
    public interface DocumentInfo {
        Long getId();
        String getFileName();
        String getOriginalFileName();
    }
    
    public interface StatusInfo {
        Long getId();
        String getName();
    }
    
    public interface DoneByInfo {
        Long getId();
        String getFullName();
        String getUsername();
    }
    
    public interface SectionCategoryInfo {
        Long getId();
        String getName();
    }
}