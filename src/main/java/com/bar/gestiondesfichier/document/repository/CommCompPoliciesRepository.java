package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.CommCompPolicies;
import com.bar.gestiondesfichier.document.projection.CommCompPoliciesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommCompPoliciesRepository extends JpaRepository<CommCompPolicies, Long> {

    Page<CommCompPolicies> findByActiveTrue(Pageable pageable);
    Page<CommCompPolicies> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<CommCompPolicies> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);
    Page<CommCompPolicies> findByActiveTrueAndSection_Id(Long sectionId, Pageable pageable);
    
    @Query("SELECT p FROM CommCompPolicies p WHERE p.active = true AND " +
           "(LOWER(p.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommCompPolicies> findByActiveTrueAndPolicyNameOrRequirementContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM CommCompPolicies p WHERE p.active = true AND " +
           "(LOWER(p.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommCompPolicies> findByActiveTrueAndSearchTerms(@Param("search") String search, Pageable pageable);
    
    // Direct projection methods for improved performance  
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.reference as reference, " +
           "c.description as description, c.policyStatus as policyStatus, c.version as version, " +
           "c.expirationDate as expirationDate, c.section as section, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "c.status.id as status_id, c.status.name as status_name, " +
           "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username " +
           "FROM CommCompPolicies c " +
           "JOIN c.document d " +
           "JOIN d.owner " +
           "WHERE c.active = true")
    Page<CommCompPoliciesProjection> findAllActiveProjections(Pageable pageable);
    
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.reference as reference, " +
           "c.description as description, c.policyStatus as policyStatus, c.version as version, " +
           "c.expirationDate as expirationDate, c.section as section, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "c.status.id as status_id, c.status.name as status_name, " +
           "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username " +
           "FROM CommCompPolicies c " +
           "JOIN c.document d " +
           "JOIN d.owner " +
           "WHERE c.active = true AND " +
           "(LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.policyStatus) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommCompPoliciesProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.reference as reference, " +
           "c.description as description, c.policyStatus as policyStatus, c.version as version, " +
           "c.expirationDate as expirationDate, c.section as section, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "c.status.id as status_id, c.status.name as status_name, " +
           "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username " +
           "FROM CommCompPolicies c " +
           "JOIN c.document d " +
           "JOIN d.owner " +
           "WHERE c.active = true AND c.status.id = :statusId")
    Page<CommCompPoliciesProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);
    
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.reference as reference, " +
           "c.description as description, c.policyStatus as policyStatus, c.version as version, " +
           "c.expirationDate as expirationDate, c.section as section, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "c.status.id as status_id, c.status.name as status_name, " +
           "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username " +
           "FROM CommCompPolicies c " +
           "JOIN c.document d " +
           "JOIN d.owner " +
           "WHERE c.active = true AND c.document.id = :documentId")
    Page<CommCompPoliciesProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);
    
    Optional<CommCompPolicies> findByIdAndActiveTrue(Long id);
    Optional<CommCompPolicies> findByReferenceAndActiveTrue(String reference);
}