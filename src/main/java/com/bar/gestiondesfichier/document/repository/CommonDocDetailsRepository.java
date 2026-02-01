package com.bar.gestiondesfichier.document.repository;

import com.bar.gestiondesfichier.document.model.CommonDocDetails;
import com.bar.gestiondesfichier.document.projection.CommonDocDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommonDocDetailsRepository extends JpaRepository<CommonDocDetails, Long> {
    
    @Query("""
    SELECT c FROM CommonDocDetails c
    WHERE (:reference IS NULL OR LOWER(c.reference) LIKE LOWER(CONCAT('%', :reference, '%')))
      AND (:status IS NULL OR c.status = :status)
      AND (:sectionId IS NULL OR c.sectionCategory.id = :sectionId)
      AND (:sectionCode IS NULL OR c.sectionCategory.code = :sectionCode)
    """)
    Page<CommonDocDetails> getCommonDocDetails(
            @Param("reference") String reference,
            @Param("status") String status,
            @Param("sectionId") Long sectionId,
            @Param("sectionCode") String sectionCode,
            Pageable pageable
    );
    
    @Query("""
    SELECT c FROM CommonDocDetails c
    WHERE c.active = true
      AND (:ownerId IS NULL OR c.doneBy.id = :ownerId)
    ORDER BY c.dateTime DESC
    """)
    Page<CommonDocDetailsProjection> findAllActive(
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
    
    @Query("""
    SELECT c FROM CommonDocDetails c
    WHERE c.active = true
      AND c.status.id = :statusId
      AND (:ownerId IS NULL OR c.doneBy.id = :ownerId)
    """)
    Page<CommonDocDetailsProjection> findByStatus(
            @Param("statusId") Long statusId,
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
    
    @Query("""
    SELECT c FROM CommonDocDetails c
    WHERE c.active = true
      AND c.sectionCategory.id = :sectionId
      AND (:ownerId IS NULL OR c.doneBy.id = :ownerId)
    """)
    Page<CommonDocDetailsProjection> findBySection(
            @Param("sectionId") Long sectionId,
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
    
    @Query("""
    SELECT c FROM CommonDocDetails c
    WHERE c.active = true
      AND c.document.id = :documentId
      AND (:ownerId IS NULL OR c.doneBy.id = :ownerId)
    """)
    Page<CommonDocDetailsProjection> findByDocument(
            @Param("documentId") Long documentId,
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
    
    @Query("""
    SELECT c FROM CommonDocDetails c
    WHERE c.active = true
      AND (LOWER(c.reference) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:ownerId IS NULL OR c.doneBy.id = :ownerId)
    """)
    Page<CommonDocDetailsProjection> search(
            @Param("search") String search,
            @Param("ownerId") Long ownerId,
            Pageable pageable
    );
    
    @Query("""
    SELECT c FROM CommonDocDetails c
    WHERE c.active = true
      AND c.id = :id
    """)
    Optional<CommonDocDetailsProjection> findByIdProjection(@Param("id") Long id);
    
    boolean existsByReference(String reference);
}
