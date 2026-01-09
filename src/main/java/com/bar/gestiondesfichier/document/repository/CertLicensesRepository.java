package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.CertLicenses;
import com.bar.gestiondesfichier.document.projection.CertLicensesProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertLicensesRepository extends JpaRepository<CertLicenses, Long> {

    Page<CertLicenses> findByActiveTrue(Pageable pageable);

    Page<CertLicenses> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);

    Page<CertLicenses> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);

    @Query("SELECT c FROM CertLicenses c WHERE c.active = true AND "
            + "(LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.agentCertifica) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CertLicenses> findByActiveTrueAndCertificateNameOrIssuingAuthorityContaining(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM CertLicenses c WHERE c.active = true AND "
            + "(LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.agentCertifica) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CertLicenses> findByActiveTrueAndSearchTerms(@Param("search") String search, Pageable pageable);

    // Direct projection methods for improved performance
//    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
//            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
//            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
//            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
//            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
//            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
//            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
//            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
//            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
//            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
//            + "c.status.id as status_id, c.status.name as status_name, "
//            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username "
//            + "FROM CertLicenses c "
//            + "JOIN c.document d "
//            + "JOIN d.owner "
//            + "WHERE c.active = true")
    @Query(
            value = """
        SELECT c FROM CertLicenses c
        LEFT JOIN FETCH c.document d
        LEFT JOIN FETCH d.owner o
        WHERE c.active = true
        AND (:ownerId IS NULL OR o.id = :ownerId)
    """,
            countQuery = """
        SELECT COUNT(c) FROM CertLicenses c
        LEFT JOIN c.document d
        LEFT JOIN d.owner o
        WHERE c.active = true
        AND (:ownerId IS NULL OR o.id = :ownerId)
    """
    )
    Page<CertLicensesProjection> findAllActiveProjections(
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );


    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username "
            + "FROM CertLicenses c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true AND "
            + "(LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.agentCertifica) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.numeroAgent) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CertLicensesProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username "
            + "FROM CertLicenses c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true AND c.status.id = :statusId")
    Page<CertLicensesProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username "
            + "FROM CertLicenses c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true AND c.document.id = :documentId")
    Page<CertLicensesProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, "
            + "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, "
            + "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, "
            + "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, "
            + "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, "
            + "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, "
            + "d.owner.username as document_owner_username, d.owner.email as document_owner_email, "
            + "c.status.id as status_id, c.status.name as status_name, "
            + "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username "
            + "FROM CertLicenses c "
            + "JOIN c.document d "
            + "JOIN d.owner "
            + "WHERE c.active = true AND c.dateCertificate <= DATEADD(day, :days, CURRENT_TIMESTAMP)")
    Page<CertLicensesProjection> findExpiringWithinDaysProjections(@Param("days") Integer days, Pageable pageable);

    Optional<CertLicenses> findByIdAndActiveTrue(Long id);
}
