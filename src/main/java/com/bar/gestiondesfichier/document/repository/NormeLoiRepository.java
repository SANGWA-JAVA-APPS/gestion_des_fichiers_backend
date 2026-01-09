package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.config.CurrentUser;
import com.bar.gestiondesfichier.document.model.NormeLoi;
import com.bar.gestiondesfichier.document.projection.NormeLoiProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for NormeLoi with pagination and projection support
 */
@Repository
public interface NormeLoiRepository extends JpaRepository<NormeLoi, Long> {

    // Pagination methods
    Page<NormeLoi> findByActiveTrue(Pageable pageable);

    Page<NormeLoi> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);

    Page<NormeLoi> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);

    @Query("SELECT n FROM NormeLoi n WHERE n.active = true AND "
            + "(LOWER(n.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(n.domaineApplication) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<NormeLoi> findByActiveTrueAndReferenceOrDescriptionContaining(@Param("search") String search, Pageable pageable);

    // Direct projection methods for optimized performance
    @Query("SELECT n.id as id, n.dateTime as dateTime, n.reference as reference, "
            + "n.description as description, n.dateVigueur as dateVigueur, "
            + "n.domaineApplication as domaineApplication, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "n.status.id as status_id, n.status.name as status_name, "
            + "n.doneBy.id as doneBy_id, n.doneBy.fullName as doneBy_fullName, n.doneBy.username as doneBy_username "
            + "FROM NormeLoi n "
            + "JOIN n.document d "
            + "JOIN d.owner "
            + "WHERE n.active = true AND "
            + "(LOWER(n.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(n.domaineApplication) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<NormeLoiProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);

    @Query("SELECT n.id as id, n.dateTime as dateTime, n.reference as reference, "
            + "n.description as description, n.dateVigueur as dateVigueur, "
            + "n.domaineApplication as domaineApplication, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "n.status.id as status_id, n.status.name as status_name, "
            + "n.doneBy.id as doneBy_id, n.doneBy.fullName as doneBy_fullName, n.doneBy.username as doneBy_username "
            + "FROM NormeLoi n "
            + "JOIN n.document d "
            + "JOIN d.owner "
            + "WHERE n.active = true AND n.status.id = :statusId")
    Page<NormeLoiProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT n.id as id, n.dateTime as dateTime, n.reference as reference, "
            + "n.description as description, n.dateVigueur as dateVigueur, "
            + "n.domaineApplication as domaineApplication, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "n.status.id as status_id, n.status.name as status_name, "
            + "n.doneBy.id as doneBy_id, n.doneBy.fullName as doneBy_fullName, n.doneBy.username as doneBy_username "
            + "FROM NormeLoi n "
            + "JOIN n.document d "
            + "JOIN d.owner "
            + "WHERE n.active = true AND n.document.id = :documentId")
    Page<NormeLoiProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);


    // Projection methods
    @Query("SELECT n FROM NormeLoi n " +
            "JOIN FETCH n.document d " +
            "JOIN FETCH d.owner o " +
            "JOIN FETCH n.status " +
            "JOIN FETCH n.doneBy " +
            "WHERE n.active = true " +
            "AND (:ownerId IS NULL OR o.id = :ownerId)")
    Page<NormeLoiProjection> findAllActiveProjections(
            @Param("ownerId") Long ownerId, Pageable pageable);

    // Specific queries
    Optional<NormeLoi> findByIdAndActiveTrue(Long id);

    @Query("SELECT n.id as id, n.dateTime as dateTime, n.reference as reference, "
            + "n.description as description, n.dateVigueur as dateVigueur, "
            + "n.domaineApplication as domaineApplication, "
            + "n.document.id as document_id, n.document.fileName as document_fileName, n.document.originalFileName as document_originalFileName, "
            + "n.document.filePath as document_filePath, n.document.contentType as document_contentType, n.document.fileSize as document_fileSize, "
            + "n.document.createdAt as document_createdAt, n.document.updatedAt as document_updatedAt, n.document.active as document_active, "
            + "n.document.status as document_status, n.document.version as document_version, n.document.expirationDate as document_expirationDate, "
            + "n.document.expiryDate as document_expiryDate, n.document.expiryAlertSent as document_expiryAlertSent, "
            + "n.document.owner.id as document_owner_id, n.document.owner.fullName as document_owner_fullName, "
            + "n.document.owner.username as document_owner_username, n.document.owner.email as document_owner_email, "
            + "n.status.id as status_id, n.status.name as status_name, "
            + "n.doneBy.id as doneBy_id, n.doneBy.fullName as doneBy_fullName, n.doneBy.username as doneBy_username "
            + "FROM NormeLoi n "
            + "WHERE n.id = :id AND n.active = true")
    Optional<NormeLoiProjection> findByIdAndActiveTrueProjection(@Param("id") Long id);

    Optional<NormeLoi> findByReferenceAndActiveTrue(String reference);

    boolean existsByReferenceAndActiveTrue(String reference);



}
