package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.CommAssetLand;
import com.bar.gestiondesfichier.document.projection.CommAssetLandProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for CommAssetLand with pagination and projection support
 */
@Repository
public interface CommAssetLandRepository extends JpaRepository<CommAssetLand, Long> {

    Page<CommAssetLand> findByActiveTrue(Pageable pageable);

    Page<CommAssetLand> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);

    Page<CommAssetLand> findByActiveTrueAndSection_Id(Long sectionId, Pageable pageable);

    @Query("SELECT c FROM CommAssetLand c WHERE c.active = true AND "
            + "(LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.emplacement) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommAssetLand> findByActiveTrueAndDescriptionOrReferenceContaining(@Param("search") String search, Pageable pageable);

    // Direct projection methods for optimized performance
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.reference as reference, c.dateObtention as dateObtention, "
            + "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username, "
            + "c.section.id as section_id, c.section.name as section_name "
            + "FROM CommAssetLand c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true AND "
            + "(LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.emplacement) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommAssetLandProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.reference as reference, c.dateObtention as dateObtention, "
            + "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username, "
            + "c.section.id as section_id, c.section.name as section_name "
            + "FROM CommAssetLand c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true AND c.status.id = :statusId")
    Page<CommAssetLandProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.reference as reference, c.dateObtention as dateObtention, "
            + "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username, "
            + "c.section.id as section_id, c.section.name as section_name "
            + "FROM CommAssetLand c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true AND c.document.id = :documentId")
    Page<CommAssetLandProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.reference as reference, c.dateObtention as dateObtention, "
            + "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username, "
            + "c.section.id as section_id, c.section.name as section_name "
            + "FROM CommAssetLand c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true AND c.section.id = :sectionCategoryId")
    Page<CommAssetLandProjection> findByActiveTrueAndSectionCategoryIdProjections(@Param("sectionCategoryId") Long sectionCategoryId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.reference as reference, c.dateObtention as dateObtention, "
            + "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username, "
            + "c.section.id as section_id, c.section.name as section_name "
            + "FROM CommAssetLand c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true")
    Page<CommAssetLandProjection> findAllActiveProjections(Pageable pageable);

    // Method for the by-section endpoint
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.reference as reference, c.dateObtention as dateObtention, "
            + "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username, "
            + "c.section.id as section_id, c.section.name as section_name "
            + "  FROM CommAssetLand c "
            + "  JOIN c.document d "
            + "  JOIN d.owner "
            + "  WHERE c.active = true AND c.section.id = :sectionCategoryId")
    Page<CommAssetLandProjection> findAllBySectionCategoryProjections(@Param("sectionCategoryId") Long sectionCategoryId, Pageable pageable);

    Optional<CommAssetLand> findByIdAndActiveTrue(Long id);

    Optional<CommAssetLand> findByReferenceAndActiveTrue(String reference);

    boolean existsByReferenceAndActiveTrue(String reference);
}
