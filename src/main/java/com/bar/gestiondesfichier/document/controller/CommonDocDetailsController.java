package com.bar.gestiondesfichier.document.controller;


import com.bar.gestiondesfichier.document.dto.CommonDocDetailsRequestDTO;
import com.bar.gestiondesfichier.document.dto.CommonDocDetailsResponseDTO;
import com.bar.gestiondesfichier.document.service.CommonDocDetailsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common-docs")
public class CommonDocDetailsController {

    private final CommonDocDetailsService service;

    public CommonDocDetailsController(CommonDocDetailsService service) {
        this.service = service;
    }

    // Create a new document
    @PostMapping
    public CommonDocDetailsResponseDTO createCommonDocDetails(
            @RequestBody CommonDocDetailsRequestDTO request
    ) {
        return service.createCommonDocDetails(request);
    }

    // Get document by ID
    @GetMapping("/{id}")
    public CommonDocDetailsResponseDTO getCommonDocDetailsById(@PathVariable Long id) {
        return service.getCommonDocDetailsById(id);
    }

    // Update document
    @PutMapping("/{id}")
    public CommonDocDetailsResponseDTO updateCommonDocDetails(
            @PathVariable Long id,
            @RequestBody CommonDocDetailsRequestDTO request
    ) {
        return service.updateCommonDocDetails(id, request);
    }

    // Delete document
    @DeleteMapping("/{id}")
    public void deleteCommonDocDetails(@PathVariable Long id) {
        service.deleteCommonDocDetails(id);
    }

    // List documents with optional search/filter/pagination
    @GetMapping
    public Page<CommonDocDetailsResponseDTO> getCommonDocDetails(
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) String sectionCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return service.getCommonDocDetails(reference, status, sectionId, sectionCode, pageable);
    }
}
