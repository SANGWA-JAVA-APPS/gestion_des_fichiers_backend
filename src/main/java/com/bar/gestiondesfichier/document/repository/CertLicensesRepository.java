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
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "c.document as document, c.status as status, c.doneBy as doneBy "
            + "FROM CertLicenses c WHERE c.active = true")
    Page<CertLicensesProjection> findAllActiveProjections(Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "c.document as document, c.status as status, c.doneBy as doneBy "
            + "FROM CertLicenses c WHERE c.active = true AND "
            + "(LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.agentCertifica) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(c.numeroAgent) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CertLicensesProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "c.document as document, c.status as status, c.doneBy as doneBy "
            + "FROM CertLicenses c WHERE c.active = true AND c.status.id = :statusId")
    Page<CertLicensesProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "c.document as document, c.status as status, c.doneBy as doneBy "
            + "FROM CertLicenses c WHERE c.active = true AND c.document.id = :documentId")
    Page<CertLicensesProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);

    @Query("SELECT c.id as id, c.dateTime as dateTime, c.description as description, "
            + "c.agentCertifica as agentCertifica, c.numeroAgent as numeroAgent, "
            + "c.dateCertificate as dateCertificate, c.dureeCertificat as dureeCertificat, "
            + "c.document as document, c.status as status, c.doneBy as doneBy "
            + "FROM CertLicenses c WHERE c.active = true AND c.dateCertificate <= DATEADD(day, :days, CURRENT_TIMESTAMP)")
    Page<CertLicensesProjection> findExpiringWithinDaysProjections(@Param("days") Integer days, Pageable pageable);

    Optional<CertLicenses> findByIdAndActiveTrue(Long id);
}
