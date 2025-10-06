package com.magerwa.gestiondesfichiers.repository;

import com.magerwa.gestiondesfichiers.entity.Document;
import com.magerwa.gestiondesfichiers.entity.Document.DocumentStatus;
import com.magerwa.gestiondesfichiers.entity.Section;
import com.magerwa.gestiondesfichiers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findBySection(Section section);
    List<Document> findBySectionId(Long sectionId);
    List<Document> findByUploadedBy(User user);
    List<Document> findByUploadedById(Long userId);
    List<Document> findByStatus(DocumentStatus status);
    
    @Query("SELECT d FROM Document d WHERE d.section = :section AND d.status = :status")
    List<Document> findBySectionAndStatus(@Param("section") Section section, @Param("status") DocumentStatus status);
    
    @Query("SELECT d FROM Document d WHERE d.uploadDate BETWEEN :startDate AND :endDate")
    List<Document> findByUploadDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT d FROM Document d WHERE d.title LIKE %:title% AND d.status = :status")
    List<Document> findByTitleContainingAndStatus(@Param("title") String title, @Param("status") DocumentStatus status);
    
    List<Document> findByStatusOrderByUploadDateDesc(DocumentStatus status);
}