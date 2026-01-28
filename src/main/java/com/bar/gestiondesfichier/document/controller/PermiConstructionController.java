package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.config.CurrentUser;
import com.bar.gestiondesfichier.document.model.DocStatus;
import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.model.PermiConstruction;
import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.document.projection.PermiConstructionProjection;
import com.bar.gestiondesfichier.document.repository.DocStatusRepository;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.document.repository.PermiConstructionRepository;
import com.bar.gestiondesfichier.document.repository.SectionCategoryRepository;
import com.bar.gestiondesfichier.document.service.DocumentUploadService;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for Construction Permit management with 20-record default
 * pagination
 */
@RestController
@RequestMapping("/api/document/permi-construction")
@DocumentControllerCors
@Tag(name = "Construction Permit Management", description = "Construction Permit CRUD operations with pagination")
@Slf4j
public class PermiConstructionController {

    private final PermiConstructionRepository permiConstructionRepository;
    private final DocumentUploadService documentUploadService;
    private final DocumentRepository documentRepository;
    private final AccountRepository accountRepository;
    private final DocStatusRepository docStatusRepository;
    private final SectionCategoryRepository sectionCategoryRepository;
    private final CurrentUser currentUser;

    public PermiConstructionController(PermiConstructionRepository permiConstructionRepository,
                                       DocumentUploadService documentUploadService,
                                       DocumentRepository documentRepository,
                                       AccountRepository accountRepository, DocStatusRepository docStatusRepository, SectionCategoryRepository sectionCategoryRepository, CurrentUser currentUser) {
        this.permiConstructionRepository = permiConstructionRepository;
        this.documentUploadService = documentUploadService;
        this.documentRepository = documentRepository;
        this.accountRepository = accountRepository;
        this.docStatusRepository = docStatusRepository;
        this.sectionCategoryRepository = sectionCategoryRepository;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Get all construction permits", description = "Retrieve paginated list of construction permits with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Construction permits retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<PermiConstructionProjection>> getAllPermiConstruction(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "numeroPermis") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Filter by section category ID") @RequestParam(required = false) Long sectionCategoryId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving construction permits - page: {}, size: {}, sort: {} {}, statusId: {}, documentId: {}, sectionCategoryId: {}, search: '{}'",
                    page, size, sort, direction, statusId, documentId, sectionCategoryId, search);
Long ownerId=currentUser.isUser()?currentUser.getAccountId():null;
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<PermiConstructionProjection> projections;

            if (search != null && !search.trim().isEmpty()) {
                projections = permiConstructionRepository.findByActiveTrueAndSearchTermsProjections(search, pageable);
                log.debug("Found {} construction permits matching search term: '{}'", projections.getTotalElements(), search);
            } else if (statusId != null) {
                projections = permiConstructionRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
                log.debug("Found {} construction permits with statusId: {}", projections.getTotalElements(), statusId);
            } else if (documentId != null) {
                projections = permiConstructionRepository.findByActiveTrueAndDocumentIdProjections(documentId, pageable);
                log.debug("Found {} construction permits with documentId: {}", projections.getTotalElements(), documentId);
            } else if (sectionCategoryId != null) {
                projections = permiConstructionRepository.findByActiveTrueAndSectionCategoryIdProjections(sectionCategoryId, pageable);
                log.debug("Found {} construction permits with sectionCategoryId: {}", projections.getTotalElements(), sectionCategoryId);
            } else {
                projections = permiConstructionRepository.findAllActiveProjections(ownerId,pageable);
                log.debug("Found {} active construction permits", projections.getTotalElements());
            }

            return ResponseEntity.ok(projections);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for construction permit retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving construction permits", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get construction permit by ID", description = "Retrieve a specific construction permit record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Construction permit retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Construction permit not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getPermiConstructionById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid construction permit ID");
            }

            log.info("Retrieving construction permit by ID: {}", id);
            Optional<PermiConstruction> permiConstruction = permiConstructionRepository.findByIdAndActiveTrue(id);

            if (permiConstruction.isPresent()) {
                return ResponseUtil.success(permiConstruction.get(), "Construction permit retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Construction permit not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving construction permit with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve construction permit: " + e.getMessage());
        }
    }
    @PostMapping(consumes = {"multipart/form-data"})
    @Transactional
    @Operation(
            summary = "Create construction permit",
            description = "Create a new construction permit record with file upload"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Construction permit created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or missing file")
    })
    public ResponseEntity<Map<String, Object>> createPermiConstruction(
            @RequestPart("permiConstruction") PermiConstruction permiConstruction,
            @RequestPart("file") MultipartFile file) {

        try {
            log.info("Creating new construction permit: {}", permiConstruction.getNumeroPermis());

        /* =========================================================
           1️⃣ BASIC FIELD VALIDATION
        ========================================================= */

            if (permiConstruction.getNumeroPermis() == null || permiConstruction.getNumeroPermis().isBlank()) {
                return ResponseUtil.badRequest("Numero permis is required");
            }

            if (permiConstruction.getDoneBy() == null || permiConstruction.getDoneBy().getId() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }

            if (permiConstruction.getStatus() == null || permiConstruction.getStatus().getId() == null) {
                return ResponseUtil.badRequest("Status is required");
            }

            if (permiConstruction.getSectionCategory() == null
                    || permiConstruction.getSectionCategory().getId() == null) {
                return ResponseUtil.badRequest("Section category is required");
            }

        /* =========================================================
           2️⃣ UNIQUENESS CHECK
        ========================================================= */

            if (permiConstructionRepository
                    .existsByNumeroPermisAndActiveTrue(permiConstruction.getNumeroPermis())) {
                return ResponseUtil.badRequest("Construction permit with this number already exists");
            }

        /* =========================================================
           3️⃣ ATTACH REAL DATABASE ENTITIES
        ========================================================= */

            Account actualOwner = accountRepository
                    .findById(permiConstruction.getDoneBy().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Account not found with ID: "
                                    + permiConstruction.getDoneBy().getId()));

            DocStatus actualStatus = docStatusRepository
                    .findById(permiConstruction.getStatus().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Status not found with ID: "
                                    + permiConstruction.getStatus().getId()));

            SectionCategory actualSectionCategory = sectionCategoryRepository
                    .findById(permiConstruction.getSectionCategory().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Section category not found with ID: "
                                    + permiConstruction.getSectionCategory().getId()));

        /* =========================================================
           4️⃣ FILE VALIDATION
        ========================================================= */

            if (file == null || file.isEmpty()) {
                return ResponseUtil.badRequest(
                        "Document file is required. Please upload a file."
                );
            }

        /* =========================================================
           5️⃣ FILE UPLOAD
        ========================================================= */

            String filePath;
            String contentType;
            String fileExtension;
            String uniqueFileName;
            String originalFileName;
            long fileSize;

            try {
                filePath = documentUploadService.uploadFile(file, "permi_construction");

                contentType = file.getContentType();
                fileSize = file.getSize();
                fileExtension = documentUploadService.extractFileExtension(
                        file.getOriginalFilename(), contentType);

                uniqueFileName = Paths.get(filePath).getFileName().toString();

                originalFileName = documentUploadService.generateOriginalFileName(
                        "Permi_Construction",
                        permiConstruction.getNumeroPermis(),
                        fileExtension
                );

            } catch (IOException e) {
                log.error("File upload failed", e);
                return ResponseUtil.badRequest("Failed to upload file: " + e.getMessage());
            }

        /* =========================================================
           6️⃣ CREATE & SAVE DOCUMENT
        ========================================================= */

            Document document = documentUploadService.initializeDocument(
                    uniqueFileName,
                    originalFileName,
                    contentType,
                    fileSize,
                    filePath,
                    actualOwner
            );

            Document savedDocument = documentRepository.save(document);

        /* =========================================================
           7️⃣ FINAL ENTITY NORMALIZATION
        ========================================================= */

            permiConstruction.setDoneBy(actualOwner);
            permiConstruction.setStatus(actualStatus);
            permiConstruction.setSectionCategory(actualSectionCategory);
            permiConstruction.setDocument(savedDocument);
            permiConstruction.setActive(true);

        /* =========================================================
           8️⃣ SAVE PERMIT
        ========================================================= */

            PermiConstruction savedPermiConstruction =
                    permiConstructionRepository.save(permiConstruction);

            return ResponseUtil.success(
                    savedPermiConstruction,
                    "Construction permit created successfully"
            );

        } catch (Exception e) {
            log.error("Error creating construction permit", e);
            return ResponseUtil.badRequest(
                    "Failed to create construction permit: " + e.getMessage()
            );
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update construction permit", description = "Update an existing construction permit record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Construction permit updated successfully"),
            @ApiResponse(responseCode = "400", description = "Construction permit not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updatePermiConstruction(
            @PathVariable Long id,
            @RequestBody PermiConstruction permiConstruction) {

        try {
            log.info("Updating construction permit with ID: {}", id);

            Optional<PermiConstruction> existingOpt = permiConstructionRepository.findByIdAndActiveTrue(id);
            if (existingOpt.isEmpty()) {
                return ResponseUtil.badRequest("Construction permit not found with ID: " + id);
            }

            PermiConstruction existing = existingOpt.get();

            applyPermiConstructionUpdates(existing, permiConstruction);

            PermiConstruction saved = permiConstructionRepository.save(existing);
            return ResponseUtil.success(saved, "Construction permit updated successfully");

        } catch (Exception e) {
            log.error("Error updating construction permit with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update construction permit: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @Operation(summary = "Update construction permit with file", description = "Update an existing construction permit record with optional file upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Construction permit updated successfully"),
            @ApiResponse(responseCode = "400", description = "Construction permit not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updatePermiConstructionWithFile(
            @PathVariable Long id,
            @RequestPart("permiConstruction") PermiConstruction permiConstruction,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            log.info("Updating construction permit with file, ID: {}", id);

            Optional<PermiConstruction> existingOpt = permiConstructionRepository.findByIdAndActiveTrue(id);
            if (existingOpt.isEmpty()) {
                return ResponseUtil.badRequest("Construction permit not found with ID: " + id);
            }

            PermiConstruction existing = existingOpt.get();

            applyPermiConstructionUpdates(existing, permiConstruction);

                String message = "Construction permit updated successfully";

                if (file != null && !file.isEmpty()) {
                String extension = documentUploadService.extractFileExtension(file.getOriginalFilename(), file.getContentType());
                String originalFileName = documentUploadService.generateOriginalFileName(
                        "Permi_Construction",
                        existing.getNumeroPermis(),
                        extension
                );

                Document updatedDocument = documentUploadService
                        .handleFileUpdate(existing.getDocument(), file, "permi_construction", originalFileName, existing.getDoneBy())
                        .map(documentRepository::save)
                        .orElse(null);

                if (updatedDocument != null) {
                    existing.setDocument(updatedDocument);
                    message = "Construction permit updated successfully. Document version upgraded to " + updatedDocument.getVersion();
                }
            }

            PermiConstruction saved = permiConstructionRepository.save(existing);
            return ResponseUtil.success(saved, message);
        } catch (Exception e) {
            log.error("Error updating construction permit with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update construction permit: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete construction permit", description = "Soft delete a construction permit record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Construction permit deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Construction permit not found")
    })
    public ResponseEntity<Map<String, Object>> deletePermiConstruction(@PathVariable Long id) {
        try {
            log.info("Deleting construction permit with ID: {}", id);
            Optional<PermiConstruction> permiConstructionOpt = permiConstructionRepository.findByIdAndActiveTrue(id);
            if (permiConstructionOpt.isEmpty()) {
                return ResponseUtil.badRequest("Construction permit not found with ID: " + id);
            }

            PermiConstruction permiConstruction = permiConstructionOpt.get();
            permiConstruction.setActive(false);
            permiConstructionRepository.save(permiConstruction);

            return ResponseUtil.success(null, "Construction permit deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting construction permit with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete construction permit: " + e.getMessage());
        }
    }

    private void applyPermiConstructionUpdates(PermiConstruction existing, PermiConstruction permiConstruction) {
        if (permiConstruction == null) {
            return;
        }

        if (permiConstruction.getNumeroPermis() != null)
            existing.setNumeroPermis(permiConstruction.getNumeroPermis());

        if (permiConstruction.getProjet() != null)
            existing.setProjet(permiConstruction.getProjet());

        if (permiConstruction.getLocalisation() != null)
            existing.setLocalisation(permiConstruction.getLocalisation());

        if (permiConstruction.getRefPermisConstuire() != null)
            existing.setRefPermisConstuire(permiConstruction.getRefPermisConstuire());

        if (permiConstruction.getReferenceTitreFoncier() != null)
            existing.setReferenceTitreFoncier(permiConstruction.getReferenceTitreFoncier());

        if (permiConstruction.getAutoriteDelivrance() != null)
            existing.setAutoriteDelivrance(permiConstruction.getAutoriteDelivrance());

        if (permiConstruction.getDateDelivrance() != null)
            existing.setDateDelivrance(permiConstruction.getDateDelivrance());

        if (permiConstruction.getDateExpiration() != null)
            existing.setDateExpiration(permiConstruction.getDateExpiration());

        if (permiConstruction.getDateValidation() != null)
            existing.setDateValidation(permiConstruction.getDateValidation());

        if (permiConstruction.getDateEstimeeTravaux() != null)
            existing.setDateEstimeeTravaux(permiConstruction.getDateEstimeeTravaux());

        if (permiConstruction.getDoneBy() != null && permiConstruction.getDoneBy().getId() != null) {
            Account user = accountRepository.findById(permiConstruction.getDoneBy().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + permiConstruction.getDoneBy().getId()));
            existing.setDoneBy(user);
        }

        if (permiConstruction.getStatus() != null && permiConstruction.getStatus().getId() != null) {
            DocStatus status = docStatusRepository.findById(permiConstruction.getStatus().getId())
                    .orElseThrow(() -> new RuntimeException("Status not found with ID: " + permiConstruction.getStatus().getId()));
            existing.setStatus(status);
        }

        if (permiConstruction.getSectionCategory() != null && permiConstruction.getSectionCategory().getId() != null) {
            SectionCategory section = sectionCategoryRepository.findById(permiConstruction.getSectionCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Section category not found with ID: " + permiConstruction.getSectionCategory().getId()));
            existing.setSectionCategory(section);
        }
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get expiring construction permits", description = "Retrieve construction permits expiring within specified days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expiring construction permits retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<Page<PermiConstructionProjection>> getExpiringPermiConstruction(
            @Parameter(description = "Days until expiration") @RequestParam(defaultValue = "30") Integer days,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.info("Retrieving construction permits expiring within {} days - page: {}, size: {}", days, page, size);

            Pageable pageable = ResponseUtil.createPageable(page, size, "dateExpiration", "asc");
            Page<PermiConstructionProjection> projections
                    = permiConstructionRepository.findExpiringWithinDaysProjections(days, pageable);

            log.debug("Found {} construction permits expiring within {} days", projections.getTotalElements(), days);

            return ResponseEntity.ok(projections);
        } catch (Exception e) {
            log.error("Error retrieving expiring construction permits", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
