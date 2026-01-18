package com.bar.gestiondesfichier.document.service.impl;

import com.bar.gestiondesfichier.document.dto.CommonDocDetailsRequestDTO;
import com.bar.gestiondesfichier.document.dto.CommonDocDetailsResponseDTO;
import com.bar.gestiondesfichier.document.model.CommonDocDetails;
import com.bar.gestiondesfichier.document.repository.CommonDocDetailsRepository;
import com.bar.gestiondesfichier.document.service.CommonDocDetailsService;
import com.bar.gestiondesfichier.location.model.Section;
import com.bar.gestiondesfichier.location.repository.SectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommonDocDetailsServiceImpl implements CommonDocDetailsService {

    private final CommonDocDetailsRepository repo;
    private final SectionRepository sectionRepo;

    public CommonDocDetailsServiceImpl(CommonDocDetailsRepository repo, SectionRepository sectionRepo) {
        this.repo = repo;
        this.sectionRepo = sectionRepo;
    }

    @Override
    public CommonDocDetailsResponseDTO createCommonDocDetails(CommonDocDetailsRequestDTO request) {
        CommonDocDetails doc = new CommonDocDetails();
        doc.setReference(request.getReference());
        doc.setDescription(request.getDescription());
        doc.setStatus(request.getStatus());
        doc.setDateTime(request.getDateTime());
        doc.setVersion(request.getVersion());
        doc.setExpirationDate(request.getExpirationDate());

        Section section = sectionRepo.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));
        doc.setSection(section);

        CommonDocDetails saved = repo.save(doc);
        return toDTO(saved);
    }

    @Override
    public CommonDocDetailsResponseDTO getCommonDocDetailsById(Long id) {
        CommonDocDetails doc = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        return toDTO(doc);
    }

    @Override
    public CommonDocDetailsResponseDTO updateCommonDocDetails(Long id, CommonDocDetailsRequestDTO request) {
        CommonDocDetails doc = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        doc.setReference(request.getReference());
        doc.setDescription(request.getDescription());
        doc.setStatus(request.getStatus());
        doc.setDateTime(request.getDateTime());
        doc.setVersion(request.getVersion());
        doc.setExpirationDate(request.getExpirationDate());

        Section section = sectionRepo.findById(request.getSectionId())
                .orElseThrow(() -> new RuntimeException("Section not found"));
        doc.setSection(section);

        CommonDocDetails updated = repo.save(doc);
        return toDTO(updated);
    }

    @Override
    public void deleteCommonDocDetails(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Document not found");
        }
        repo.deleteById(id);
    }

    @Override
    public Page<CommonDocDetailsResponseDTO> getCommonDocDetails(
            String reference,
            String status,
            Long sectionId,
            String sectionCode,
            Pageable pageable
    ) {
        Page<CommonDocDetails> page = repo.getCommonDocDetails(reference, status, sectionId,sectionCode, pageable);
        return page.map(this::toDTO);
    }

    // Mapper from entity → DTO
    private CommonDocDetailsResponseDTO toDTO(CommonDocDetails doc) {
        CommonDocDetailsResponseDTO dto = new CommonDocDetailsResponseDTO();
        dto.setId(doc.getId());
        dto.setReference(doc.getReference());
        dto.setDescription(doc.getDescription());
        dto.setStatus(doc.getStatus());
        dto.setDateTime(doc.getDateTime());
        dto.setVersion(doc.getVersion());
        dto.setExpirationDate(doc.getExpirationDate());
        dto.setSectionId(doc.getSection().getId());
        return dto;
    }
}
