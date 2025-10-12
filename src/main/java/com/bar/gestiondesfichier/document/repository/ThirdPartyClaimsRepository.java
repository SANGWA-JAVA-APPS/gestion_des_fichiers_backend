package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.ThirdPartyClaims;
import com.bar.gestiondesfichier.document.projection.ThirdPartyClaimsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThirdPartyClaimsRepository extends JpaRepository<ThirdPartyClaims, Long> {

    Page<ThirdPartyClaims> findByActiveTrue(Pageable pageable);
    Page<ThirdPartyClaims> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<ThirdPartyClaims> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);
    
    @Query("SELECT t FROM ThirdPartyClaims t WHERE t.active = true AND " +
           "(LOWER(t.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ThirdPartyClaims> findByActiveTrueAndReferenceOrDescriptionContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT t.id as id, t.dateTime as dateTime, t.reference as reference, " +
           "t.description as description, t.dateClaim as dateClaim, " +
           "t.departmentInCharge as departmentInCharge, " +
           "t.document as document, t.status as status, t.doneBy as doneBy " +
           "FROM ThirdPartyClaims t WHERE t.active = true")
    Page<ThirdPartyClaimsProjection> findAllActiveProjections(Pageable pageable);
    
    @Query("SELECT t.id as id, t.dateTime as dateTime, t.reference as reference, " +
           "t.description as description, t.dateClaim as dateClaim, " +
           "t.departmentInCharge as departmentInCharge, " +
           "t.document as document, t.status as status, t.doneBy as doneBy " +
           "FROM ThirdPartyClaims t WHERE t.active = true AND t.status.id = :statusId")
    Page<ThirdPartyClaimsProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);
    
    @Query("SELECT t.id as id, t.dateTime as dateTime, t.reference as reference, " +
           "t.description as description, t.dateClaim as dateClaim, " +
           "t.departmentInCharge as departmentInCharge, " +
           "t.document as document, t.status as status, t.doneBy as doneBy " +
           "FROM ThirdPartyClaims t WHERE t.active = true AND " +
           "(LOWER(t.reference) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.departmentInCharge) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ThirdPartyClaimsProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT t.id as id, t.dateTime as dateTime, t.reference as reference, " +
           "t.description as description, t.dateClaim as dateClaim, " +
           "t.departmentInCharge as departmentInCharge, " +
           "t.document as document, t.status as status, t.doneBy as doneBy " +
           "FROM ThirdPartyClaims t WHERE t.active = true AND " +
           "LOWER(t.departmentInCharge) = LOWER(:departmentInCharge)")
    Page<ThirdPartyClaimsProjection> findAllByDepartmentInChargeProjections(@Param("departmentInCharge") String departmentInCharge, Pageable pageable);
    
    Optional<ThirdPartyClaims> findByIdAndActiveTrue(Long id);
    boolean existsByReferenceAndActiveTrue(String reference);
}