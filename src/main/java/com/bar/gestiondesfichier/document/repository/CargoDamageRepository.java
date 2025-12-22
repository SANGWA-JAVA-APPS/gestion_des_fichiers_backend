package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.CargoDamage;
import com.bar.gestiondesfichier.document.projection.CargoDamageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoDamageRepository extends JpaRepository<CargoDamage, Long> {

    Page<CargoDamage> findByActiveTrue(Pageable pageable);
    Page<CargoDamage> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<CargoDamage> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);
    
    @Query("SELECT c FROM CargoDamage c WHERE c.active = true AND " +
           "(LOWER(c.refeRequest) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CargoDamage> findByActiveTrueAndIncidentIdOrCargoTypeContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT c FROM CargoDamage c WHERE c.active = true AND " +
           "(LOWER(c.refeRequest) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CargoDamage> findByActiveTrueAndClaimNumberOrDamageDescriptionContaining(@Param("search") String search, Pageable pageable);
    
    // Direct projection methods for improved performance using JOIN FETCH
    @Query("SELECT c FROM CargoDamage c " +
           "LEFT JOIN FETCH c.document d " +
           "LEFT JOIN FETCH d.owner " +
           "LEFT JOIN FETCH c.status " +
           "LEFT JOIN FETCH c.doneBy " +
           "WHERE c.active = true")
    Page<CargoDamageProjection> findAllActiveProjections(Pageable pageable);
    
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.refeRequest as refeRequest, " +
           "c.description as description, c.quotationContractNum as quotationContractNum, " +
           "c.dateRequest as dateRequest, c.dateContract as dateContract, " +
           "c.document.id as document_id, c.document.fileName as document_fileName, c.document.originalFileName as document_originalFileName, " +
           "c.document.filePath as document_filePath, c.document.contentType as document_contentType, c.document.fileSize as document_fileSize, " +
           "c.document.createdAt as document_createdAt, c.document.updatedAt as document_updatedAt, c.document.active as document_active, " +
           "c.document.status as document_status, c.document.version as document_version, c.document.expirationDate as document_expirationDate, " +
           "c.document.expiryDate as document_expiryDate, c.document.expiryAlertSent as document_expiryAlertSent, " +
           "c.document.owner.id as document_owner_id, c.document.owner.fullName as document_owner_fullName, " +
           "c.document.owner.username as document_owner_username, c.document.owner.email as document_owner_email, " +
           "c.status.id as status_id, c.status.name as status_name, " +
           "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username " +
           "FROM CargoDamage c " +
           "WHERE c.active = true AND " +
           "(LOWER(c.refeRequest) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.quotationContractNum) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CargoDamageProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.refeRequest as refeRequest, " +
           "c.description as description, c.quotationContractNum as quotationContractNum, " +
           "c.dateRequest as dateRequest, c.dateContract as dateContract, " +
           "c.document.id as document_id, c.document.fileName as document_fileName, c.document.originalFileName as document_originalFileName, " +
           "c.document.filePath as document_filePath, c.document.contentType as document_contentType, c.document.fileSize as document_fileSize, " +
           "c.document.createdAt as document_createdAt, c.document.updatedAt as document_updatedAt, c.document.active as document_active, " +
           "c.document.status as document_status, c.document.version as document_version, c.document.expirationDate as document_expirationDate, " +
           "c.document.expiryDate as document_expiryDate, c.document.expiryAlertSent as document_expiryAlertSent, " +
           "c.document.owner.id as document_owner_id, c.document.owner.fullName as document_owner_fullName, " +
           "c.document.owner.username as document_owner_username, c.document.owner.email as document_owner_email, " +
           "c.status.id as status_id, c.status.name as status_name, " +
           "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username " +
           "FROM CargoDamage c " +
           "WHERE c.active = true AND c.status.id = :statusId")
    Page<CargoDamageProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);
    
    @Query("SELECT c.id as id, c.dateTime as dateTime, c.refeRequest as refeRequest, " +
           "c.description as description, c.quotationContractNum as quotationContractNum, " +
           "c.dateRequest as dateRequest, c.dateContract as dateContract, " +
           "c.document.id as document_id, c.document.fileName as document_fileName, c.document.originalFileName as document_originalFileName, " +
           "c.document.filePath as document_filePath, c.document.contentType as document_contentType, c.document.fileSize as document_fileSize, " +
           "c.document.createdAt as document_createdAt, c.document.updatedAt as document_updatedAt, c.document.active as document_active, " +
           "c.document.status as document_status, c.document.version as document_version, c.document.expirationDate as document_expirationDate, " +
           "c.document.expiryDate as document_expiryDate, c.document.expiryAlertSent as document_expiryAlertSent, " +
           "c.document.owner.id as document_owner_id, c.document.owner.fullName as document_owner_fullName, " +
           "c.document.owner.username as document_owner_username, c.document.owner.email as document_owner_email, " +
           "c.status.id as status_id, c.status.name as status_name, " +
           "c.doneBy.id as doneBy_id, c.doneBy.fullName as doneBy_fullName, c.doneBy.username as doneBy_username " +
           "FROM CargoDamage c " +
           "WHERE c.active = true AND c.document.id = :documentId")
    Page<CargoDamageProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);
    
    Optional<CargoDamage> findByIdAndActiveTrue(Long id);
    boolean existsByRefeRequestAndActiveTrue(String refeRequest);
}