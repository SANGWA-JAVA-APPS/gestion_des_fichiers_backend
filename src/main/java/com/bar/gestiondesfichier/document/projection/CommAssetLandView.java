package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CommAssetLandView {

    Long getId();
    LocalDateTime getDateTime();
    String getDescription();
    String getReference();
    LocalDate getDateObtention();
    String getCoordonneesGps();
    String getEmplacement();

    Long getDocumentId();
    String getDocumentFileName();
    String getDocumentOriginalFileName();
    String getDocumentFilePath();
    String getDocumentContentType();
    Long getDocumentFileSize();
    LocalDateTime getDocumentCreatedAt();
    LocalDateTime getDocumentUpdatedAt();
    Boolean getDocumentActive();
    String getDocumentStatus();
    Integer getDocumentVersion();
    LocalDate getDocumentExpirationDate();
    LocalDate getDocumentExpiryDate();
    Boolean getDocumentExpiryAlertSent();

    Long getDocumentOwnerId();
    String getDocumentOwnerFullName();
    String getDocumentOwnerUsername();
    String getDocumentOwnerEmail();

    Long getStatusId();
    String getStatusName();

    Long getDoneById();
    String getDoneByFullName();
    String getDoneByUsername();

    Long getSectionId();
    String getSectionName();
}

