package com.bar.gestiondesfichier.document.service;

import com.bar.gestiondesfichier.document.dto.CommonDocDetailsRequestDTO;
import com.bar.gestiondesfichier.document.projection.CommonDocDetailsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface CommonDocDetailsService {

    // Create a new document (file optional)
    CommonDocDetailsProjection createCommonDocDetails(
            CommonDocDetailsRequestDTO request,
            MultipartFile file
    );

    // Get a document by ID
    CommonDocDetailsProjection getCommonDocDetailsById(Long id);

    // Update an existing document (file optional)
    CommonDocDetailsProjection updateCommonDocDetails(
            Long id,
            CommonDocDetailsRequestDTO request,
            MultipartFile file
    );

    // Delete a document by ID
    void deleteCommonDocDetails(Long id);

    // Get list of documents with optional search/filter/pagination using projections
    Page<CommonDocDetailsProjection> getCommonDocDetails(
            String reference,
            String status,
            Long sectionId,
            String sectionCode,
            Long ownerId,
            String search,
            Pageable pageable
    );
}
