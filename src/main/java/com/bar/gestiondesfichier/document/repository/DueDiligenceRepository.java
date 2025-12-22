package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.DueDiligence;
import com.bar.gestiondesfichier.document.projection.DueDiligenceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DueDiligenceRepository extends JpaRepository<DueDiligence, Long> {

    Page<DueDiligence> findByActiveTrue(Pageable pageable);

    Page<DueDiligence> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);

    Page<DueDiligence> findByActiveTrueAndSection_Id(Long sectionId, Pageable pageable);

    Page<DueDiligence> findByActiveTrueAndAuditor(String auditor, Pageable pageable);

    @Query("SELECT d FROM DueDiligence d WHERE d.active = true AND "
            + "(LOWER(d.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(d.auditor) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<DueDiligence> findByActiveTrueAndReferenceOrDescriptionOrAuditorContaining(@Param("search") String search, Pageable pageable);

    @Query("SELECT d FROM DueDiligence d WHERE d.active = true AND "
            + "(LOWER(d.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(d.auditor) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<DueDiligence> findByActiveTrueAndSearchTerms(@Param("search") String search, Pageable pageable);

    @Query("SELECT d.id as id, d.dateTime as dateTime, d.reference as reference, "
            + "d.description as description, d.dateDueDiligence as dateDueDiligence, "
            + "d.auditor as auditor, d.creationDate as creationDate, d.completionDate as completionDate, "
            + "d.docAttach as docAttach, d.section as section, "
            + "doc.id as document_id, doc.fileName as document_fileName, doc.originalFileName as document_originalFileName, "
            + "doc.filePath as document_filePath, doc.contentType as document_contentType, doc.fileSize as document_fileSize, "
            + "doc.createdAt as document_createdAt, doc.updatedAt as document_updatedAt, doc.active as document_active, "
            + "doc.status as document_status, doc.version as document_version, doc.expirationDate as document_expirationDate, "
            + "doc.expiryDate as document_expiryDate, doc.expiryAlertSent as document_expiryAlertSent, "
            + "doc.owner.id as document_owner_id, doc.owner.fullName as document_owner_fullName, "
            + "doc.owner.username as document_owner_username, doc.owner.email as document_owner_email, "
            + "d.status.id as status_id, d.status.name as status_name, "
            + "d.doneBy.id as doneBy_id, d.doneBy.fullName as doneBy_fullName, d.doneBy.username as doneBy_username "
            + "  FROM DueDiligence d "
            + "  JOIN d.document doc "
            + "  JOIN doc.owner "
            + "WHERE d.active = true")
    Page<DueDiligenceProjection> findAllActiveProjections(Pageable pageable);

    @Query("SELECT d.id as id, d.dateTime as dateTime, d.reference as reference, "
            + "d.description as description, d.dateDueDiligence as dateDueDiligence, "
            + "d.auditor as auditor, d.creationDate as creationDate, d.completionDate as completionDate, "
            + "d.docAttach as docAttach, d.section as section, "
            + "doc.id as document_id, doc.fileName as document_fileName, doc.originalFileName as document_originalFileName, "
            + "doc.filePath as document_filePath, doc.contentType as document_contentType, doc.fileSize as document_fileSize, "
            + "doc.createdAt as document_createdAt, doc.updatedAt as document_updatedAt, doc.active as document_active, "
            + "doc.status as document_status, doc.version as document_version, doc.expirationDate as document_expirationDate, "
            + "doc.expiryDate as document_expiryDate, doc.expiryAlertSent as document_expiryAlertSent, "
            + "doc.owner.id as document_owner_id, doc.owner.fullName as document_owner_fullName, "
            + "doc.owner.username as document_owner_username, doc.owner.email as document_owner_email, "
            + "d.status.id as status_id, d.status.name as status_name, "
            + "d.doneBy.id as doneBy_id, d.doneBy.fullName as doneBy_fullName, d.doneBy.username as doneBy_username "
            + "  FROM DueDiligence d "
            + "  JOIN d.document doc "
            + "JOIN doc.owner "
            + "WHERE d.active = true AND d.status.id = :statusId")
    Page<DueDiligenceProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT d.id as id, d.dateTime as dateTime, d.reference as reference, "
            + "d.description as description, d.dateDueDiligence as dateDueDiligence, "
            + "d.auditor as auditor, d.creationDate as creationDate, d.completionDate as completionDate, "
            + "d.docAttach as docAttach, d.section as section, "
            + "doc.id as document_id, doc.fileName as document_fileName, doc.originalFileName as document_originalFileName, "
            + "doc.filePath as document_filePath, doc.contentType as document_contentType, doc.fileSize as document_fileSize, "
            + "doc.createdAt as document_createdAt, doc.updatedAt as document_updatedAt, doc.active as document_active, "
            + "doc.status as document_status, doc.version as document_version, doc.expirationDate as document_expirationDate, "
            + "doc.expiryDate as document_expiryDate, doc.expiryAlertSent as document_expiryAlertSent, "
            + "doc.owner.id as document_owner_id, doc.owner.fullName as document_owner_fullName, "
            + "doc.owner.username as document_owner_username, doc.owner.email as document_owner_email, "
            + "d.status.id as status_id, d.status.name as status_name, "
            + "d.doneBy.id as doneBy_id, d.doneBy.fullName as doneBy_fullName, d.doneBy.username as doneBy_username "
            + "FROM DueDiligence d "
            + "JOIN d.document doc "
            + "JOIN doc.owner "
            + "WHERE d.active = true AND d.section.id = :sectionId")
    Page<DueDiligenceProjection> findByActiveTrueAndSectionIdProjections(@Param("sectionId") Long sectionId, Pageable pageable);

    @Query("SELECT d.id as id, d.dateTime as dateTime, d.reference as reference, "
            + "d.description as description, d.dateDueDiligence as dateDueDiligence, "
            + "d.auditor as auditor, d.creationDate as creationDate, d.completionDate as completionDate, "
            + "d.docAttach as docAttach, d.section as section, "
            + "doc.id as document_id, doc.fileName as document_fileName, doc.originalFileName as document_originalFileName, "
            + "doc.filePath as document_filePath, doc.contentType as document_contentType, doc.fileSize as document_fileSize, "
            + "doc.createdAt as document_createdAt, doc.updatedAt as document_updatedAt, doc.active as document_active, "
            + "doc.status as document_status, doc.version as document_version, doc.expirationDate as document_expirationDate, "
            + "doc.expiryDate as document_expiryDate, doc.expiryAlertSent as document_expiryAlertSent, "
            + "doc.owner.id as document_owner_id, doc.owner.fullName as document_owner_fullName, "
            + "doc.owner.username as document_owner_username, doc.owner.email as document_owner_email, "
            + "d.status.id as status_id, d.status.name as status_name, "
            + "d.doneBy.id as doneBy_id, d.doneBy.fullName as doneBy_fullName, d.doneBy.username as doneBy_username "
            + "FROM DueDiligence d "
            + "JOIN d.document doc "
            + "JOIN doc.owner "
            + "WHERE d.active = true AND "
            + "(LOWER(d.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(d.auditor) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<DueDiligenceProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);

    Optional<DueDiligence> findByIdAndActiveTrue(Long id);
}
