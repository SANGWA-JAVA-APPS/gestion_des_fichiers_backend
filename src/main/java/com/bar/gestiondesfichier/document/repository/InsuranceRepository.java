package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.Insurance;
import com.bar.gestiondesfichier.document.projection.InsuranceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    Page<Insurance> findByActiveTrue(Pageable pageable);
    Page<Insurance> findByActiveTrueAndStatus_Id(Long statusId, Pageable pageable);
    Page<Insurance> findByActiveTrueAndDocument_Id(Long documentId, Pageable pageable);
    
    @Query("SELECT i FROM Insurance i WHERE i.active = true AND " +
           "(LOWER(i.concerns) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.coverage) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Insurance> findByActiveTrueAndConcernsOrCoverageContaining(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT i.id as id, i.dateTime as dateTime, i.concerns as concerns, " +
           "i.coverage as coverage, i.values as values, " +
           "i.dateValidity as dateValidity, i.renewalDate as renewalDate, " +
           "i.document as document, i.status as status, i.doneBy as doneBy " +
           "FROM Insurance i WHERE i.active = true")
    Page<InsuranceProjection> findAllActiveProjections(Pageable pageable);
    
    @Query("SELECT i.id as id, i.dateTime as dateTime, i.concerns as concerns, " +
           "i.coverage as coverage, i.values as values, " +
           "i.dateValidity as dateValidity, i.renewalDate as renewalDate, " +
           "i.document as document, i.status as status, i.doneBy as doneBy " +
           "FROM Insurance i WHERE i.active = true AND i.status.id = :statusId")
    Page<InsuranceProjection> findByActiveTrueAndStatusIdProjections(@Param("statusId") Long statusId, Pageable pageable);
    
    @Query("SELECT i.id as id, i.dateTime as dateTime, i.concerns as concerns, " +
           "i.coverage as coverage, i.values as values, " +
           "i.dateValidity as dateValidity, i.renewalDate as renewalDate, " +
           "i.document as document, i.status as status, i.doneBy as doneBy " +
           "FROM Insurance i WHERE i.active = true AND " +
           "(LOWER(i.concerns) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.coverage) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<InsuranceProjection> findByActiveTrueAndSearchTermsProjections(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT i.id as id, i.dateTime as dateTime, i.concerns as concerns, " +
           "i.coverage as coverage, i.values as values, " +
           "i.dateValidity as dateValidity, i.renewalDate as renewalDate, " +
           "i.document as document, i.status as status, i.doneBy as doneBy " +
           "FROM Insurance i WHERE i.active = true AND " +
           "i.dateValidity <= DATEADD(day, :days, CURRENT_TIMESTAMP)")
    Page<InsuranceProjection> findExpiringWithinDaysProjections(@Param("days") Integer days, Pageable pageable);
    
    @Query("SELECT i FROM Insurance i WHERE i.active = true AND " +
           "i.dateValidity <= DATEADD(day, :days, CURRENT_TIMESTAMP)")
    Page<Insurance> findExpiringWithinDays(@Param("days") Integer days, Pageable pageable);
    
    Optional<Insurance> findByIdAndActiveTrue(Long id);
    boolean existsByConcernsAndActiveTrue(String concerns);
}