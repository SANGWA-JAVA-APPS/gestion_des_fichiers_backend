package com.bar.gestiondesfichier.document.model;

/**
 * Enumeration representing the lifecycle status of a document
 * 
 * ACTIVE - Document is currently active and editable
 * ARCHIVED - Document is archived and not editable (stored for reference)
 * EXPIRED - Document has expired and is not editable
 */
public enum DocumentStatus {
    /**
     * Document is active and can be edited
     */
    ACTIVE("Active", "Document is currently active and editable"),
    
    /**
     * Document is archived and cannot be edited
     */
    ARCHIVED("Archived", "Document is archived and not editable"),

    /**
     * Document has expired and cannot be edited
     */
    EXPIRED("Expired", "Document has expired and is not editable");
    private final String displayName;
    private final String description;

    DocumentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
    /**
     * Check if documents with this status are editable
     * Only ACTIVE documents can be edited
     * 
     * @return true if the document is editable, false otherwise
     */
    public boolean isEditable() {
        return this == ACTIVE;
    }
}
