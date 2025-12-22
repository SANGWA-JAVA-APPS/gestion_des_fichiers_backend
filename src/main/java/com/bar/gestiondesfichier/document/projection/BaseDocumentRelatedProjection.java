package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDate;
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
        String getFilePath();
        String getContentType();
        Long getFileSize();
        LocalDateTime getCreatedAt();
        LocalDateTime getUpdatedAt();
        Boolean getActive();
        String getStatus();
        String getVersion();
        LocalDateTime getExpirationDate();
        LocalDate getExpiryDate();
        Boolean getExpiryAlertSent();
        OwnerInfo getOwner();
    }
    
    public interface OwnerInfo {
        Long getId();
        String getFullName();
        String getUsername();
        String getEmail();
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