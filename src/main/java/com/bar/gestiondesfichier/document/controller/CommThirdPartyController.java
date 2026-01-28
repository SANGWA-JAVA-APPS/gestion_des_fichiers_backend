package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.config.CurrentUser;
import com.bar.gestiondesfichier.document.dto.CommThirdPartyCreateRequest;
import com.bar.gestiondesfichier.document.dto.CommThirdPartyUpdateRequest;
import com.bar.gestiondesfichier.document.model.*;
import com.bar.gestiondesfichier.document.projection.CommThirdPartyProjection;
import com.bar.gestiondesfichier.document.repository.*;
import com.bar.gestiondesfichier.document.service.DocumentUploadService;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.location.model.Section;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/document/comm-third-party")
@DocumentControllerCors
@Tag(name = "Commercial Third Party Management", description = "Commercial Third Party CRUD operations with pagination")
@Slf4j
public class CommThirdPartyController {

    private final CommThirdPartyRepository commThirdPartyRepository;
    private final SectionCategoryRepository sectionCategoryRepository;
    private final DocStatusRepository docStatusRepository;
    private final DocumentUploadService documentUploadService;
    private final DocumentRepository documentRepository;
    private final CurrentUser currentUser;

    public CommThirdPartyController(
            CommThirdPartyRepository commThirdPartyRepository,
            SectionCategoryRepository sectionCategoryRepository,
            DocStatusRepository docStatusRepository,
            DocumentUploadService documentUploadService,
            DocumentRepository documentRepository,
            CurrentUser currentUser
    ) {
        this.commThirdPartyRepository = commThirdPartyRepository;
        this.sectionCategoryRepository = sectionCategoryRepository;
        this.docStatusRepository = docStatusRepository;
        this.documentUploadService = documentUploadService;
        this.documentRepository = documentRepository;
        this.currentUser = currentUser;
    }

    // ==================== GET ALL ====================
    @GetMapping
    @Operation(summary = "Get all commercial third parties", description = "Retrieve paginated list of commercial third parties")
    public ResponseEntity<Page<CommThirdPartyProjection>> getAllCommThirdParty(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) String search
    ) {
        try {
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);

            Page<CommThirdPartyProjection> thirdParties = commThirdPartyRepository.getAllCommThirdParties(
                    statusId,
                    sectionId,
                    (search != null && !search.trim().isEmpty()) ? search.trim() : null,
                    pageable
            );

            return ResponseEntity.ok(thirdParties);
        } catch (Exception e) {
            log.error("Error retrieving commercial third parties", e);
            return ResponseEntity.badRequest().build();
        }
    }


    // ==================== GET BY ID ====================
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCommThirdPartyById(@PathVariable Long id) {
        try {
            Optional<CommThirdParty> thirdParty = commThirdPartyRepository.findByIdAndActiveTrue(id);
            return thirdParty
                    .map(tp -> ResponseUtil.success(tp, "Commercial third party retrieved successfully"))
                    .orElseGet(() -> ResponseUtil.badRequest("Commercial third party not found with ID: " + id));
        } catch (Exception e) {
            log.error("Error retrieving commercial third party with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve commercial third party: " + e.getMessage());
        }
    }


    // ==================== CREATE ====================
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createCommThirdParty(
            @RequestPart("data") CommThirdPartyCreateRequest request,
            @RequestPart( "file") MultipartFile file
    ) {
        try {
            Account account = currentUser.getAccount();
            if (account == null) throw new IllegalStateException("Authenticated user not found");

            CommThirdParty thirdParty = new CommThirdParty();
            thirdParty.setName(request.getName());
            thirdParty.setLocation(request.getLocation());
            thirdParty.setValidity(request.getValidity());
            thirdParty.setActivities(request.getActivities());
            thirdParty.setActive(true);
            thirdParty.setDoneBy(account);

            if (request.getSectionId() != null) {
                SectionCategory section = sectionCategoryRepository
                        .findByIdAndActiveTrue(request.getSectionId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid section"));
                thirdParty.setSection(section);
            }

            if (request.getStatusId() != null) {
                DocStatus status = docStatusRepository
                        .findById(request.getStatusId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid status"));
                thirdParty.setStatus(status);
            }

            if (file != null && !file.isEmpty()) {
                String folder = "comm_third_party";
                String filePath = documentUploadService.uploadFile(file, folder);
                String extension = documentUploadService.extractFileExtension(file.getOriginalFilename(), file.getContentType());
                String originalFileName = documentUploadService.generateOriginalFileName("CommThirdParty", thirdParty.getName(), extension);

                Document document = documentUploadService.initializeDocument(
                        file.getOriginalFilename(), originalFileName,
                        file.getContentType(), file.getSize(), filePath, account
                );
                documentRepository.save(document);
                thirdParty.setDocument(document);
            }

            CommThirdParty saved = commThirdPartyRepository.save(thirdParty);
            return ResponseUtil.success(saved, "Commercial third party created successfully");
        } catch (Exception e) {
            log.error("Error creating commercial third party", e);
            return ResponseUtil.badRequest(e.getMessage());
        }
    }
    // ==================== UPDATE (with optional file upload) ====================
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateCommThirdParty(
            @PathVariable Long id,
            @RequestPart("data") CommThirdPartyUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            // Find existing entity
            CommThirdParty existing = commThirdPartyRepository
                    .findByIdAndActiveTrue(id)
                    .orElseThrow(() -> new IllegalArgumentException("Commercial third party not found with ID: " + id));

            Account account = currentUser.getAccount();
            if (account == null) throw new IllegalStateException("Authenticated user not found");

            // Update fields if provided
            if (request.getName() != null) existing.setName(request.getName());
            if (request.getLocation() != null) existing.setLocation(request.getLocation());
            if (request.getValidity() != null) existing.setValidity(request.getValidity());
            if (request.getActivities() != null) existing.setActivities(request.getActivities());

            // Update section if provided
            if (request.getSectionId() != null) {
                SectionCategory section = sectionCategoryRepository
                        .findByIdAndActiveTrue(request.getSectionId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid section"));
                existing.setSection(section);
            }

            // Update status if provided
            if (request.getStatusId() != null) {
                DocStatus status = docStatusRepository
                        .findById(request.getStatusId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid status"));
                existing.setStatus(status);
            }

            // Handle optional file upload
            String message = "Commercial third party updated successfully";
            if (file != null && !file.isEmpty()) {
                String extension = documentUploadService.extractFileExtension(file.getOriginalFilename(), file.getContentType());
                String originalFileName = documentUploadService.generateOriginalFileName("CommThirdParty", existing.getName(), extension);

                Document updatedDocument = documentUploadService
                        .handleFileUpdate(existing.getDocument(), file, "comm_third_party", originalFileName, account)
                        .map(documentRepository::save)
                        .orElse(null);

                if (updatedDocument != null) {
                    existing.setDocument(updatedDocument);
                    message = "Commercial third party updated successfully. Document version upgraded to " + updatedDocument.getVersion();
                }
            }

            // Track who updated the entity
            existing.setDoneBy(account);

            // Save updates
            CommThirdParty saved = commThirdPartyRepository.save(existing);
            return ResponseUtil.success(saved, message);

        } catch (Exception e) {
            log.error("Error updating commercial third party with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update commercial third party: " + e.getMessage());
        }
    }




    // ==================== DELETE ====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCommThirdParty(@PathVariable Long id) {
        try {
            CommThirdParty existing = commThirdPartyRepository
                    .findByIdAndActiveTrue(id)
                    .orElseThrow(() -> new IllegalArgumentException("Commercial third party not found with ID: " + id));
            existing.setActive(false);
            commThirdPartyRepository.save(existing);
            return ResponseUtil.success(null, "Commercial third party deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting commercial third party with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete commercial third party: " + e.getMessage());
        }
    }
}
