package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.LitigationFollowup;
import com.bar.gestiondesfichier.document.projection.LitigationFollowupProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LitigationFollowupRepository extends JpaRepository<LitigationFollowup, Long> {

    Page<LitigationFollowup> findByActiveTrue(Pageable pageable);
    Page<LitigationFollowup> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<LitigationFollowup> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);
    
    @Query("SELECT l FROM LitigationFollowup l WHERE l.active = true AND " +
           "(LOWER(l.concern) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.statut) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<LitigationFollowup> findByActiveTrueAndConcernOrStatutContaining(@Param("search") String search, Pageable pageable);
    
    // Direct projection methods for optimized performance
    @Query("SELECT l.id as id, l.dateTime as dateTime, l.creationDate as creationDate, " +
           "l.concern as concern, l.statut as statut, l.expectedCompletion as expectedCompletion, " +
           "l.riskValue as riskValue, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "l.status.id as status_id, l.status.name as status_name, " +
           "l.doneBy.id as doneBy_id, l.doneBy.fullName as doneBy_fullName, l.doneBy.username as doneBy_username " +
           "FROM LitigationFollowup l " +
           "JOIN l.document d " +
           "JOIN d.owner " +
           "WHERE l.active = true AND " +
           "(LOWER(l.concern) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.statut) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<LitigationFollowupProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT l.id as id, l.dateTime as dateTime, l.creationDate as creationDate, " +
           "l.concern as concern, l.statut as statut, l.expectedCompletion as expectedCompletion, " +
           "l.riskValue as riskValue, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "l.status.id as status_id, l.status.name as status_name, " +
           "l.doneBy.id as doneBy_id, l.doneBy.fullName as doneBy_fullName, l.doneBy.username as doneBy_username " +
           "FROM LitigationFollowup l " +
           "JOIN l.document d " +
           "JOIN d.owner " +
           "WHERE l.active = true AND l.status.id = :statusId")
    Page<LitigationFollowupProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);
    
    @Query("SELECT l.id as id, l.dateTime as dateTime, l.creationDate as creationDate, " +
           "l.concern as concern, l.statut as statut, l.expectedCompletion as expectedCompletion, " +
           "l.riskValue as riskValue, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "l.status.id as status_id, l.status.name as status_name, " +
           "l.doneBy.id as doneBy_id, l.doneBy.fullName as doneBy_fullName, l.doneBy.username as doneBy_username " +
           "FROM LitigationFollowup l " +
           "JOIN l.document d " +
           "JOIN d.owner " +
           "WHERE l.active = true")
    Page<LitigationFollowupProjection> findAllActiveProjections(Pageable pageable);
    
    Optional<LitigationFollowup> findByIdAndActiveTrue(Long id);
    boolean existsByConcernAndActiveTrue(String concern);
}