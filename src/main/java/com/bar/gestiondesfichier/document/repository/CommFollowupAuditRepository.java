package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.CommFollowupAudit;
import com.bar.gestiondesfichier.document.projection.CommFollowupAuditProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommFollowupAuditRepository extends JpaRepository<CommFollowupAudit, Long> {

    Page<CommFollowupAudit> findByActiveTrue(Pageable pageable);
    Page<CommFollowupAudit> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<CommFollowupAudit> findByActiveTrueAndSection_Id(Long sectionId, Pageable pageable);
    Page<CommFollowupAudit> findByActiveTrueAndAuditor(String auditor, Pageable pageable);
    
    @Query("SELECT a FROM CommFollowupAudit a WHERE a.active = true AND " +
           "(LOWER(a.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.auditor) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommFollowupAudit> findByActiveTrueAndSearchTerms(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT a FROM CommFollowupAudit a WHERE a.active = true AND " +
           "(LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.reference) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommFollowupAudit> findByActiveTrueAndAuditTitleOrFindingsContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT a.id as id, a.dateTime as dateTime, a.reference as reference, " +
           "a.description as description, a.dateAudit as dateAudit, a.auditor as auditor, " +
           "a.numNonConform as numNonConform, a.typeConform as typeConform, " +
           "a.percentComplete as percentComplete, a.docAttach as docAttach, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "a.status.id as status_id, a.status.name as status_name, " +
           "a.doneBy.id as doneBy_id, a.doneBy.fullName as doneBy_fullName, a.doneBy.username as doneBy_username, " +
           "a.section.id as section_id, a.section.name as section_name " +
           "FROM CommFollowupAudit a " +
           "JOIN a.document d " +
           "JOIN d.owner " +
           "WHERE a.active = true")
    Page<CommFollowupAuditProjection> findAllActiveProjections(Pageable pageable);
    
    @Query("SELECT a.id as id, a.dateTime as dateTime, a.reference as reference, " +
           "a.description as description, a.dateAudit as dateAudit, a.auditor as auditor, " +
           "a.numNonConform as numNonConform, a.typeConform as typeConform, " +
           "a.percentComplete as percentComplete, a.docAttach as docAttach, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "a.status.id as status_id, a.status.name as status_name, " +
           "a.doneBy.id as doneBy_id, a.doneBy.fullName as doneBy_fullName, a.doneBy.username as doneBy_username, " +
           "a.section.id as section_id, a.section.name as section_name " +
           "FROM CommFollowupAudit a " +
           "JOIN a.document d " +
           "JOIN d.owner " +
           "WHERE a.active = true AND a.status.id = :statusId")
    Page<CommFollowupAuditProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);
    
    @Query("SELECT a.id as id, a.dateTime as dateTime, a.reference as reference, " +
           "a.description as description, a.dateAudit as dateAudit, a.auditor as auditor, " +
           "a.numNonConform as numNonConform, a.typeConform as typeConform, " +
           "a.percentComplete as percentComplete, a.docAttach as docAttach, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "a.status.id as status_id, a.status.name as status_name, " +
           "a.doneBy.id as doneBy_id, a.doneBy.fullName as doneBy_fullName, a.doneBy.username as doneBy_username, " +
           "a.section.id as section_id, a.section.name as section_name " +
           "FROM CommFollowupAudit a " +
           "JOIN a.document d " +
           "JOIN d.owner " +
           "WHERE a.active = true AND a.section.id = :sectionId")
    Page<CommFollowupAuditProjection> findByActiveTrueAndSectionIdProjections(@Param("sectionId") Long sectionId, Pageable pageable);
    
    @Query("SELECT a.id as id, a.dateTime as dateTime, a.reference as reference, " +
           "a.description as description, a.dateAudit as dateAudit, a.auditor as auditor, " +
           "a.numNonConform as numNonConform, a.typeConform as typeConform, " +
           "a.percentComplete as percentComplete, a.docAttach as docAttach, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "a.status.id as status_id, a.status.name as status_name, " +
           "a.doneBy.id as doneBy_id, a.doneBy.fullName as doneBy_fullName, a.doneBy.username as doneBy_username, " +
           "a.section.id as section_id, a.section.name as section_name " +
           "FROM CommFollowupAudit a " +
           "JOIN a.document d " +
           "JOIN d.owner " +
           "WHERE a.active = true AND " +
           "(LOWER(a.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.auditor) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommFollowupAuditProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);
    
    Optional<CommFollowupAudit> findByIdAndActiveTrue(Long id);
}