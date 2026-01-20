package com.bar.gestiondesfichier.document.service.impl;

import com.bar.gestiondesfichier.document.dto.CommonDocDetailsRequestDTO;
import com.bar.gestiondesfichier.document.dto.CommonDocDetailsResponseDTO;
import com.bar.gestiondesfichier.document.model.CommonDocDetails;
import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.document.repository.CommonDocDetailsRepository;
import com.bar.gestiondesfichier.document.repository.SectionCategoryRepository;
import com.bar.gestiondesfichier.document.service.CommonDocDetailsService;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommonDocDetailsServiceImpl implements CommonDocDetailsService {

    private final CommonDocDetailsRepository repo;
    private final SectionCategoryRepository sectionCategoryRepo;

    public CommonDocDetailsServiceImpl(
            CommonDocDetailsRepository repo,
            SectionCategoryRepository sectionCategoryRepo
    ) {
        this.repo = repo;
        this.sectionCategoryRepo = sectionCategoryRepo;
    }

    @Override
    public CommonDocDetailsResponseDTO createCommonDocDetails(
            CommonDocDetailsRequestDTO request
    ) {
        CommonDocDetails doc = new CommonDocDetails();
        applyRequest(doc, request);
        return toDTO(repo.save(doc));
    }

    @Override
    public CommonDocDetailsResponseDTO getCommonDocDetailsById(Long id) {
        return toDTO(findByIdOrThrow(id));
    }

    @Override
    public CommonDocDetailsResponseDTO updateCommonDocDetails(
            Long id,
            CommonDocDetailsRequestDTO request
    ) {
        CommonDocDetails doc = findByIdOrThrow(id);
        applyRequest(doc, request);
        return toDTO(repo.save(doc));
    }

    @Override
    public void deleteCommonDocDetails(Long id) {
        CommonDocDetails doc = findByIdOrThrow(id);
        repo.delete(doc);
    }

    @Override
    public Page<CommonDocDetailsResponseDTO> getCommonDocDetails(
            String reference,
            String status,
            Long sectionCategoryId,
            String sectionCategoryCode,
            Pageable pageable
    ) {
        return repo
                .getCommonDocDetails(reference, status, sectionCategoryId, sectionCategoryCode, pageable)
                .map(this::toDTO);
    }

    // ------------------------
    // Internal helpers
    // ------------------------

    private CommonDocDetails findByIdOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
    }

    private void applyRequest(
            CommonDocDetails doc,
            CommonDocDetailsRequestDTO request
    ) {
        doc.setReference(request.getReference());
        doc.setDescription(request.getDescription());
        doc.setStatus(request.getStatus());
        doc.setDateTime(request.getDateTime());
        doc.setVersion(request.getVersion());
        doc.setExpirationDate(request.getExpirationDate());
        doc.setSectionCategory(resolveSectionCategory(request.getSectionCategoryId()));
    }

    private SectionCategory resolveSectionCategory(Long sectionCategoryId) {
        return sectionCategoryRepo.findById(sectionCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Section category not found"));
    }

    private CommonDocDetailsResponseDTO toDTO(CommonDocDetails doc) {
        CommonDocDetailsResponseDTO dto = new CommonDocDetailsResponseDTO();
        dto.setId(doc.getId());
        dto.setReference(doc.getReference());
        dto.setDescription(doc.getDescription());
        dto.setStatus(doc.getStatus());
        dto.setDateTime(doc.getDateTime());
        dto.setVersion(doc.getVersion());
        dto.setExpirationDate(doc.getExpirationDate());

        SectionCategory category = doc.getSectionCategory();
        dto.setSectionCategoryId(category.getId());
        dto.setSectionCategoryCode(category.getCode());
        dto.setSectionCategoryName(category.getName());

        return dto;
    }
}
