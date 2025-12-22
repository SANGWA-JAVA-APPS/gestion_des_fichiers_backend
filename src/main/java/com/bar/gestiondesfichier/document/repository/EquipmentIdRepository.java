package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.EquipmentId;
import com.bar.gestiondesfichier.document.projection.EquipmentIdProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for EquipmentId with pagination and projection support
 */
@Repository
public interface EquipmentIdRepository extends JpaRepository<EquipmentId, Long> {

    Page<EquipmentId> findByActiveTrue(Pageable pageable);
    Page<EquipmentId> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<EquipmentId> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);
    Page<EquipmentId> findByActiveTrueAndEquipmentType(String equipmentType, Pageable pageable);
    
    @Query("SELECT e FROM EquipmentId e WHERE e.active = true AND " +
           "(LOWER(e.equipmentType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<EquipmentId> findByActiveTrueAndEquipmentTypeOrSerialNumberContaining(@Param("search") String search, Pageable pageable);

    @Query("SELECT e.id as id, e.dateTime as dateTime, e.equipmentType as equipmentType, " +
           "e.serialNumber as serialNumber, e.plateNumber as plateNumber, " +
           "e.etatEquipement as etatEquipement, e.dateAchat as dateAchat, " +
           "e.dateVisiteTechnique as dateVisiteTechnique, e.assurance as assurance, " +
           "e.documentsTelecharger as documentsTelecharger, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "e.status.id as status_id, e.status.name as status_name, " +
           "e.doneBy.id as doneBy_id, e.doneBy.fullName as doneBy_fullName, e.doneBy.username as doneBy_username " +
           "FROM EquipmentId e " +
           "JOIN e.document d " +
           "JOIN d.owner " +
           "WHERE e.active = true AND " +
           "(LOWER(e.equipmentType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<EquipmentIdProjection> findByActiveTrueAndEquipmentTypeOrSerialNumberContainingProjections(@Param("search") String search, Pageable pageable);

    @Query("SELECT e.id as id, e.dateTime as dateTime, e.equipmentType as equipmentType, " +
           "e.serialNumber as serialNumber, e.plateNumber as plateNumber, " +
           "e.etatEquipement as etatEquipement, e.dateAchat as dateAchat, " +
           "e.dateVisiteTechnique as dateVisiteTechnique, e.assurance as assurance, " +
           "e.documentsTelecharger as documentsTelecharger, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "e.status.id as status_id, e.status.name as status_name, " +
           "e.doneBy.id as doneBy_id, e.doneBy.fullName as doneBy_fullName, e.doneBy.username as doneBy_username " +
           "FROM EquipmentId e " +
           "JOIN e.document d " +
           "JOIN d.owner " +
           "WHERE e.active = true AND e.status.id = :statusId")
    Page<EquipmentIdProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT e.id as id, e.dateTime as dateTime, e.equipmentType as equipmentType, " +
           "e.serialNumber as serialNumber, e.plateNumber as plateNumber, " +
           "e.etatEquipement as etatEquipement, e.dateAchat as dateAchat, " +
           "e.dateVisiteTechnique as dateVisiteTechnique, e.assurance as assurance, " +
           "e.documentsTelecharger as documentsTelecharger, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "e.status.id as status_id, e.status.name as status_name, " +
           "e.doneBy.id as doneBy_id, e.doneBy.fullName as doneBy_fullName, e.doneBy.username as doneBy_username " +
           "FROM EquipmentId e " +
           "JOIN e.document d " +
           "JOIN d.owner " +
           "WHERE e.active = true AND e.document.id = :documentId")
    Page<EquipmentIdProjection> findByActiveTrueAndDocumentIdProjections(@Param("documentId") Long documentId, Pageable pageable);

    @Query("SELECT e.id as id, e.dateTime as dateTime, e.equipmentType as equipmentType, " +
           "e.serialNumber as serialNumber, e.plateNumber as plateNumber, " +
           "e.etatEquipement as etatEquipement, e.dateAchat as dateAchat, " +
           "e.dateVisiteTechnique as dateVisiteTechnique, e.assurance as assurance, " +
           "e.documentsTelecharger as documentsTelecharger, " +
           "d.id as document_id, d.fileName as document_fileName, d.originalFileName as document_originalFileName, " +
           "d.filePath as document_filePath, d.contentType as document_contentType, d.fileSize as document_fileSize, " +
           "d.createdAt as document_createdAt, d.updatedAt as document_updatedAt, d.active as document_active, " +
           "d.status as document_status, d.version as document_version, d.expirationDate as document_expirationDate, " +
           "d.expiryDate as document_expiryDate, d.expiryAlertSent as document_expiryAlertSent, " +
           "d.owner.id as document_owner_id, d.owner.fullName as document_owner_fullName, " +
           "d.owner.username as document_owner_username, d.owner.email as document_owner_email, " +
           "e.status.id as status_id, e.status.name as status_name, " +
           "e.doneBy.id as doneBy_id, e.doneBy.fullName as doneBy_fullName, e.doneBy.username as doneBy_username " +
           "FROM EquipmentId e " +
           "JOIN e.document d " +
           "JOIN d.owner " +
           "WHERE e.active = true")
    Page<EquipmentIdProjection> findAllActiveProjections(Pageable pageable);
    
    Optional<EquipmentId> findByIdAndActiveTrue(Long id);
    Optional<EquipmentId> findBySerialNumberAndActiveTrue(String serialNumber);
    Optional<EquipmentId> findByPlateNumberAndActiveTrue(String plateNumber);
    boolean existsBySerialNumberAndActiveTrue(String serialNumber);
    boolean existsByPlateNumberAndActiveTrue(String plateNumber);
}