package com.bar.gestiondesfichier.document.repository;




import com.bar.gestiondesfichier.document.model.CommonDocDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    boolean existsByReference(String reference);
}
