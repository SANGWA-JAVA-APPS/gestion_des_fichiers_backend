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
    
    @Query("SELECT c FROM CommAssetLand c WHERE c.active = true AND " +
           "(LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.emplacement) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommAssetLand> findByActiveTrueAndDescriptionOrReferenceContaining(@Param("search") String search, Pageable pageable);

    // Direct projection methods for optimized performance
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, " +
           "c.reference as reference, c.dateObtention as dateObtention, " +
           "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, " +
           "c.document as document, c.status as status, c.doneBy as doneBy, c.section as section " +
           "FROM CommAssetLand c WHERE c.active = true AND " +
           "(LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.emplacement) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommAssetLandProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, " +
           "c.reference as reference, c.dateObtention as dateObtention, " +
           "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, " +
           "c.document as document, c.status as status, c.doneBy as doneBy, c.section as section " +
           "FROM CommAssetLand c WHERE c.active = true AND c.status.id = :statusId")
    Page<CommAssetLandProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, " +
           "c.reference as reference, c.dateObtention as dateObtention, " +
           "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, " +
           "c.document as document, c.status as status, c.doneBy as doneBy, c.section as section " +
           "FROM CommAssetLand c WHERE c.active = true AND c.document.id = :documentId")
    Page<CommAssetLandProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, " +
           "c.reference as reference, c.dateObtention as dateObtention, " +
           "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, " +
           "c.document as document, c.status as status, c.doneBy as doneBy, c.section as section " +
           "FROM CommAssetLand c WHERE c.active = true AND c.section.id = :sectionCategoryId")
    Page<CommAssetLandProjection> findByActiveTrueAndSectionCategoryIdProjections(@Param("sectionCategoryId") Long sectionCategoryId, Pageable pageable);
    
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, " +
           "c.reference as reference, c.dateObtention as dateObtention, " +
           "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, " +
           "c.document as document, c.status as status, c.doneBy as doneBy, c.section as section " +
           "FROM CommAssetLand c WHERE c.active = true")
    Page<CommAssetLandProjection> findAllActiveProjections(Pageable pageable);
    
    // Method for the by-section endpoint
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, " +
           "c.reference as reference, c.dateObtention as dateObtention, " +
           "c.coordonneesGps as coordonneesGps, c.emplacement as emplacement, " +
           "c.document as document, c.status as status, c.doneBy as doneBy, c.section as section " +
           "FROM CommAssetLand c WHERE c.active = true AND c.section.id = :sectionCategoryId")
    Page<CommAssetLandProjection> findAllBySectionCategoryProjections(@Param("sectionCategoryId") Long sectionCategoryId, Pageable pageable);
    
    Optional<CommAssetLand> findByIdAndActiveTrue(Long id);
    Optional<CommAssetLand> findByReferenceAndActiveTrue(String reference);
    boolean existsByReferenceAndActiveTrue(String reference);
}