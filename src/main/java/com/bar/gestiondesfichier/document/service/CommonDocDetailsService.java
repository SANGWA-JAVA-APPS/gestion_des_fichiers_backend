package com.bar.gestiondesfichier.document.service;

import com.bar.gestiondesfichier.document.dto.CommonDocDetailsRequestDTO;
import com.bar.gestiondesfichier.document.dto.CommonDocDetailsResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommonDocDetailsService {

    // Create a new document
    CommonDocDetailsResponseDTO createCommonDocDetails(CommonDocDetailsRequestDTO request);

    // Get a document by ID
    CommonDocDetailsResponseDTO getCommonDocDetailsById(Long id);

    // Update an existing document
    CommonDocDetailsResponseDTO updateCommonDocDetails(Long id, CommonDocDetailsRequestDTO request);

    // Delete a document by ID
    void deleteCommonDocDetails(Long id);

    // Get list of documents with optional search/filter/pagination
    Page<CommonDocDetailsResponseDTO> getCommonDocDetails(
            String reference,
            String status,
            Long sectionId,
            String sectionCode,
            Pageable pageable
    );
}
