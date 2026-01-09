package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.PermiConstruction;
import com.bar.gestiondesfichier.document.projection.PermiConstructionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for PermiConstruction with pagination and projection support
 */
@Repository
public interface PermiConstructionRepository extends JpaRepository<PermiConstruction, Long> {

    Page<PermiConstruction> findByActiveTrue(Pageable pageable);
    Page<PermiConstruction> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<PermiConstruction> findByActiveTrueAndSectionCategory_Id(Long sectionCategoryId, Pageable pageable);
    Page<PermiConstruction> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);
    
    @Query("SELECT p FROM PermiConstruction p WHERE p.active = true AND " +
           "(LOWER(p.numeroPermis) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.projet) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PermiConstruction> findByActiveTrueAndNumeroPermisOrProjetContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM PermiConstruction p WHERE p.active = true AND " +
           "LOWER(p.referenceTitreFoncier) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.refPermisConstuire) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<PermiConstruction> findByActiveTrueAndReferenceContaining(@Param("search") String search, Pageable pageable);

    @Query(
            value = """
        SELECT p
        FROM PermiConstruction p
        JOIN p.document d
        JOIN d.owner o
        WHERE p.active = true
          AND (:ownerId IS NULL OR o.id = :ownerId)
    """,
            countQuery = """
        SELECT COUNT(p)
        FROM PermiConstruction p
        JOIN p.document d
        JOIN d.owner o
        WHERE p.active = true
          AND (:ownerId IS NULL OR o.id = :ownerId)
    """
    )
    Page<PermiConstructionProjection> findAllActiveProjections(
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );

    
    // Direct projection methods for improved performance
    @Query("SELECT p.id as id, p.dateTime as dateTime, p.numeroPermis as numeroPermis, " +
           "p.projet as projet, p.localisation as localisation, p.dateDelivrance as dateDelivrance, " +
           "p.dateExpiration as dateExpiration, p.autoriteDelivrance as autoriteDelivrance, " +
           "p.referenceTitreFoncier as referenceTitreFoncier, p.refPermisConstuire as refPermisConstuire, " +
           "p.dateValidation as dateValidation, p.dateEstimeeTravaux as dateEstimeeTravaux, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "p.status.id as status_id, p.status.name as status_name, " +
           "p.doneBy.id as doneBy_id, p.doneBy.fullName as doneBy_fullName, p.doneBy.username as doneBy_username, " +
           "p.sectionCategory.id as sectionCategory_id, p.sectionCategory.name as sectionCategory_name " +
           "FROM PermiConstruction p " +
           "JOIN p.document d " +
           "JOIN d.owner " +
           "WHERE p.active = true AND " +
           "(LOWER(p.numeroPermis) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.projet) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.localisation) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<PermiConstructionProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p.id as id, p.dateTime as dateTime, p.numeroPermis as numeroPermis, " +
           "p.projet as projet, p.localisation as localisation, p.dateDelivrance as dateDelivrance, " +
           "p.dateExpiration as dateExpiration, p.autoriteDelivrance as autoriteDelivrance, " +
           "p.referenceTitreFoncier as referenceTitreFoncier, p.refPermisConstuire as refPermisConstuire, " +
           "p.dateValidation as dateValidation, p.dateEstimeeTravaux as dateEstimeeTravaux, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "p.status.id as status_id, p.status.name as status_name, " +
           "p.doneBy.id as doneBy_id, p.doneBy.fullName as doneBy_fullName, p.doneBy.username as doneBy_username, " +
           "p.sectionCategory.id as sectionCategory_id, p.sectionCategory.name as sectionCategory_name " +
           "FROM PermiConstruction p " +
           "JOIN p.document d " +
           "JOIN d.owner " +
           "WHERE p.active = true AND p.status.id = :statusId")
    Page<PermiConstructionProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);
    
    @Query("SELECT p.id as id, p.dateTime as dateTime, p.numeroPermis as numeroPermis, " +
           "p.projet as projet, p.localisation as localisation, p.dateDelivrance as dateDelivrance, " +
           "p.dateExpiration as dateExpiration, p.autoriteDelivrance as autoriteDelivrance, " +
           "p.referenceTitreFoncier as referenceTitreFoncier, p.refPermisConstuire as refPermisConstuire, " +
           "p.dateValidation as dateValidation, p.dateEstimeeTravaux as dateEstimeeTravaux, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "p.status.id as status_id, p.status.name as status_name, " +
           "p.doneBy.id as doneBy_id, p.doneBy.fullName as doneBy_fullName, p.doneBy.username as doneBy_username, " +
           "p.sectionCategory.id as sectionCategory_id, p.sectionCategory.name as sectionCategory_name " +
           "FROM PermiConstruction p " +
           "JOIN p.document d " +
           "JOIN d.owner " +
           "WHERE p.active = true AND p.document.id = :documentId")
    Page<PermiConstructionProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);
    
    @Query("SELECT p.id as id, p.dateTime as dateTime, p.numeroPermis as numeroPermis, " +
           "p.projet as projet, p.localisation as localisation, p.dateDelivrance as dateDelivrance, " +
           "p.dateExpiration as dateExpiration, p.autoriteDelivrance as autoriteDelivrance, " +
           "p.referenceTitreFoncier as referenceTitreFoncier, p.refPermisConstuire as refPermisConstuire, " +
           "p.dateValidation as dateValidation, p.dateEstimeeTravaux as dateEstimeeTravaux, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "p.status.id as status_id, p.status.name as status_name, " +
           "p.doneBy.id as doneBy_id, p.doneBy.fullName as doneBy_fullName, p.doneBy.username as doneBy_username, " +
           "p.sectionCategory.id as sectionCategory_id, p.sectionCategory.name as sectionCategory_name " +
           "FROM PermiConstruction p " +
           "JOIN p.document d " +
           "JOIN d.owner " +
           "WHERE p.active = true AND p.sectionCategory.id = :sectionCategoryId")
    Page<PermiConstructionProjection> findByActiveTrueAndSectionCategoryIdProjections(@Param("sectionCategoryId") Long sectionCategoryId, Pageable pageable);
    
    @Query("SELECT p.id as id, p.dateTime as dateTime, p.numeroPermis as numeroPermis, " +
           "p.projet as projet, p.localisation as localisation, p.dateDelivrance as dateDelivrance, " +
           "p.dateExpiration as dateExpiration, p.autoriteDelivrance as autoriteDelivrance, " +
           "p.referenceTitreFoncier as referenceTitreFoncier, p.refPermisConstuire as refPermisConstuire, " +
           "p.dateValidation as dateValidation, p.dateEstimeeTravaux as dateEstimeeTravaux, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "p.status.id as status_id, p.status.name as status_name, " +
           "p.doneBy.id as doneBy_id, p.doneBy.fullName as doneBy_fullName, p.doneBy.username as doneBy_username, " +
           "p.sectionCategory.id as sectionCategory_id, p.sectionCategory.name as sectionCategory_name " +
           "FROM PermiConstruction p " +
           "JOIN p.document d " +
           "JOIN d.owner " +
           "WHERE p.active = true AND p.dateExpiration <= DATEADD(day, :days, CURRENT_TIMESTAMP)")
    Page<PermiConstructionProjection> findExpiringWithinDaysProjections(@Param("days") Integer days, Pageable pageable);
    
    Optional<PermiConstruction> findByIdAndActiveTrue(Long id);
    Optional<PermiConstruction> findByNumeroPermisAndActiveTrue(String numeroPermis);
    Optional<PermiConstruction> findByReferenceTitreFoncierAndActiveTrue(String referenceTitreFoncier);
    boolean existsByNumeroPermisAndActiveTrue(String numeroPermis);
    boolean existsByReferenceTitreFoncierAndActiveTrue(String referenceTitreFoncier);
}