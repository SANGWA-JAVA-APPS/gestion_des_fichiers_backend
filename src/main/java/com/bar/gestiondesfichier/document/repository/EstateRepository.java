package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.Estate;
import com.bar.gestiondesfichier.document.projection.EstateProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Estate with pagination and projection support
 */
@Repository
public interface EstateRepository extends JpaRepository<Estate, Long> {

    Page<Estate> findByActiveTrue(Pageable pageable);
    Page<Estate> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<Estate> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);
    Page<Estate> findByActiveTrueAndEstateType(String estateType, Pageable pageable);
    
    @Query("SELECT e FROM Estate e WHERE e.active = true AND " +
           "(LOWER(e.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.estateType) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Estate> findByActiveTrueAndReferenceOrEstateTypeContaining(@Param("search") String search, Pageable pageable);

    @Query("SELECT e.id as id, e.dateTime as dateTime, e.reference as reference, " +
           "e.estateType as estateType, e.emplacement as emplacement, " +
           "e.coordonneesGps as coordonneesGps, e.dateOfBuilding as dateOfBuilding, " +
           "e.comments as comments, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "e.status.id as status_id, e.status.name as status_name, " +
           "e.doneBy.id as doneBy_id, e.doneBy.fullName as doneBy_fullName, e.doneBy.username as doneBy_username " +
           "FROM Estate e " +
           "JOIN e.document d " +
           "JOIN d.owner " +
           "WHERE e.active = true AND " +
           "(LOWER(e.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.estateType) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<EstateProjection> findByActiveTrueAndReferenceOrEstateTypeContainingProjections(@Param("search") String search, Pageable pageable);

    @Query("SELECT e.id as id, e.dateTime as dateTime, e.reference as reference, " +
           "e.estateType as estateType, e.emplacement as emplacement, " +
           "e.coordonneesGps as coordonneesGps, e.dateOfBuilding as dateOfBuilding, " +
           "e.comments as comments, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "e.status.id as status_id, e.status.name as status_name, " +
           "e.doneBy.id as doneBy_id, e.doneBy.fullName as doneBy_fullName, e.doneBy.username as doneBy_username " +
           "FROM Estate e " +
           "JOIN e.document d " +
           "JOIN d.owner " +
           "WHERE e.active = true AND e.status.id = :statusId")
    Page<EstateProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT e.id as id, e.dateTime as dateTime, e.reference as reference, " +
           "e.estateType as estateType, e.emplacement as emplacement, " +
           "e.coordonneesGps as coordonneesGps, e.dateOfBuilding as dateOfBuilding, " +
           "e.comments as comments, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "e.status.id as status_id, e.status.name as status_name, " +
           "e.doneBy.id as doneBy_id, e.doneBy.fullName as doneBy_fullName, e.doneBy.username as doneBy_username " +
           "FROM Estate e " +
           "JOIN e.document d " +
           "JOIN d.owner " +
           "WHERE e.active = true AND e.document.id = :documentId")
    Page<EstateProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);
    
    @Query("SELECT e.id as id, e.dateTime as dateTime, e.reference as reference, " +
           "e.estateType as estateType, e.emplacement as emplacement, " +
           "e.coordonneesGps as coordonneesGps, e.dateOfBuilding as dateOfBuilding, " +
           "e.comments as comments, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "e.status.id as status_id, e.status.name as status_name, " +
           "e.doneBy.id as doneBy_id, e.doneBy.fullName as doneBy_fullName, e.doneBy.username as doneBy_username " +
           "FROM Estate e " +
           "JOIN e.document d " +
           "JOIN d.owner " +
           "WHERE e.active = true")
    Page<EstateProjection> findAllActiveProjections(Pageable pageable);
    
    Optional<Estate> findByIdAndActiveTrue(Long id);
    Optional<Estate> findByReferenceAndActiveTrue(String reference);
    boolean existsByReferenceAndActiveTrue(String reference);
}