package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.CommThirdParty;
import com.bar.gestiondesfichier.document.projection.CommThirdPartyProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommThirdPartyRepository extends JpaRepository<CommThirdParty, Long> {

    Page<CommThirdParty> findByActiveTrue(Pageable pageable);

    Page<CommThirdParty> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);

    Page<CommThirdParty> findByActiveTrueAndSection_Id(Long sectionId, Pageable pageable);

    @Query("SELECT t FROM CommThirdParty t WHERE t.active = true AND "
            + "(LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(t.location) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(t.activities) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommThirdParty> findByActiveTrueAndSearchTerms(@Param("search") String search, Pageable pageable);

    // Direct projection methods for optimized performance
    @Query("SELECT t.id as id, t.dateTime as dateTime, t.name as name, "
            + "t.location as location, t.validity as validity, t.activities as activities, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "t.status.id as status_id, t.status.name as status_name, "
            + "t.doneBy.id as doneBy_id, t.doneBy.fullName as doneBy_fullName, t.doneBy.username as doneBy_username, "
            + "t.section.id as section_id, t.section.name as section_name "
            + "FROM CommThirdParty t "
            + "JOIN t.document d "
            + "JOIN d.owner "
            + "WHERE t.active = true AND "
            + "(LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(t.location) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(t.activities) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CommThirdPartyProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);

    @Query("SELECT t.id as id,  t.dateTime as dateTime, t.name as name, "
            + "t.location as location, t.validity as validity, t.activities as activities, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "t.status.id as status_id, t.status.name as status_name, "
            + "t.doneBy.id as doneBy_id, t.doneBy.fullName as doneBy_fullName, t.doneBy.username as doneBy_username, "
            + "t.section.id as section_id, t.section.name as section_name "
            + "FROM CommThirdParty t "
            + "JOIN t.document d "
            + "JOIN d.owner "
            + "WHERE t.active = true AND t.status.id = :statusId")
    Page<CommThirdPartyProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT t.id as id, t.dateTime as dateTime, t.name as name, "
            + "t.location as location, t.validity as validity, t.activities as activities, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "t.status.id as status_id, t.status.name as status_name, "
            + "t.doneBy.id as doneBy_id, t.doneBy.fullName as doneBy_fullName, t.doneBy.username as doneBy_username, "
            + "t.section.id as section_id, t.section.name as section_name "
            + "FROM CommThirdParty t "
            + "JOIN t.document d "
            + "JOIN d.owner "
            + "WHERE t.active = true")
    Page<CommThirdPartyProjection> findAllActiveProjections(Pageable pageable);

    Optional<CommThirdParty> findByIdAndActiveTrue(Long id);
}
