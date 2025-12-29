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

@Repository
public interface CommAssetLandRepository extends JpaRepository<CommAssetLand, Long> {

    /* ===================== BASE QUERY ===================== */
    String BASE_QUERY = """
            SELECT
                c.id                                  AS id,
                c.date_time                           AS dateTime,
                c.description                         AS description,
                c.reference                           AS reference,
                c.date_obtention                      AS dateObtention,
                c.coordonnees_gps                     AS coordonneesGps,
                c.emplacement                         AS emplacement,

                d.id                                  AS documentId,
                d.file_name                            AS documentFileName,
                d.original_file_name                   AS documentOriginalFileName,
                d.file_path                            AS documentFilePath,
                d.content_type                         AS documentContentType,
                d.file_size                            AS documentFileSize,
                d.created_at                            AS documentCreatedAt,
                d.updated_at                            AS documentUpdatedAt,
                d.active                                AS documentActive,
                d.status                                AS documentStatus,
                d.version                               AS documentVersion,
                d.expiration_date                       AS documentExpirationDate,
                d.expiry_date                           AS documentExpiryDate,
                d.expiry_alert_sent                      AS documentExpiryAlertSent,

                owner.id                                AS documentOwnerId,
                owner.full_name                         AS documentOwnerFullName,
                owner.username                           AS documentOwnerUsername,
                owner.email                              AS documentOwnerEmail,

                s.id                                    AS statusId,
                s.name                                  AS statusName,

                doneBy.id                               AS doneById,
                doneBy.full_name                         AS doneByFullName,
                doneBy.username                          AS doneByUsername,

                sec.id                                   AS sectionId,
                sec.name                                 AS sectionName
            FROM comm_asset_land c
            JOIN files d              ON d.id = c.doc_id
            JOIN accounts owner       ON owner.id = d.owner_id
            JOIN docstatus s          ON s.id = c.statut_id
            JOIN accounts doneBy      ON doneBy.id = c.doneby
            JOIN section_category sec ON sec.id = c.section_id
            WHERE c.active = 1
            """;

    /* ===================== LIST ALL ACTIVE ===================== */
    @Query(
            value = BASE_QUERY,
            countQuery = "SELECT COUNT(*) FROM comm_asset_land c WHERE c.active = 1",
            nativeQuery = true
    )
    Page<CommAssetLandProjection> findAllActive(Pageable pageable);

    /* ===================== SEARCH ===================== */
    @Query(
            value = BASE_QUERY + """
              AND (
                  LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(c.emplacement) LIKE LOWER(CONCAT('%', :search, '%'))
              )
        """,
            countQuery = """
            SELECT COUNT(*) FROM comm_asset_land c
            WHERE c.active = 1
              AND (
                  LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(c.emplacement) LIKE LOWER(CONCAT('%', :search, '%'))
              )
        """,
            nativeQuery = true
    )
    Page<CommAssetLandProjection> search(@Param("search") String search, Pageable pageable);

    /* ===================== BY STATUS ===================== */
    @Query(
            value = BASE_QUERY + " AND c.statut_id = :statusId",
            countQuery = "SELECT COUNT(*) FROM comm_asset_land c WHERE c.active = 1 AND c.statut_id = :statusId",
            nativeQuery = true
    )
    Page<CommAssetLandProjection> findByStatus(@Param("statusId") Long statusId, Pageable pageable);

    /* ===================== BY DOCUMENT ===================== */
    @Query(
            value = BASE_QUERY + " AND c.doc_id = :documentId",
            countQuery = "SELECT COUNT(*) FROM comm_asset_land c WHERE c.active = 1 AND c.doc_id = :documentId",
            nativeQuery = true
    )
    Page<CommAssetLandProjection> findByDocument(@Param("documentId") Long documentId, Pageable pageable);

    /* ===================== BY SECTION ===================== */
    @Query(
            value = BASE_QUERY + " AND c.section_id = :sectionId",
            countQuery = "SELECT COUNT(*) FROM comm_asset_land c WHERE c.active = 1 AND c.section_id = :sectionId",
            nativeQuery = true
    )
    Page<CommAssetLandProjection> findBySection(@Param("sectionId") Long sectionId, Pageable pageable);

    /* ===================== ENTITY METHODS ===================== */
    Optional<CommAssetLand> findByIdAndActiveTrue(Long id);

    Optional<CommAssetLand> findByReferenceAndActiveTrue(String reference);

    boolean existsByReferenceAndActiveTrue(String reference);
}
