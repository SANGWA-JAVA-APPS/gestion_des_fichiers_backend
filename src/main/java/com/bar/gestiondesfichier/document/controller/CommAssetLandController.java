package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.CommAssetLand;
import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.projection.CommAssetLandProjection;
import com.bar.gestiondesfichier.document.repository.CommAssetLandRepository;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
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
 * REST controller for Commercial Asset Land management with 20-record default
 * pagination
 */
@RestController
@RequestMapping("/api/document/comm-asset-land")
@DocumentControllerCors
@Tag(name = "Commercial Asset Land Management", description = "Commercial Asset Land CRUD operations with pagination")
@Slf4j
public class CommAssetLandController {

    private final CommAssetLandRepository commAssetLandRepository;
    private final DocumentUploadService documentUploadService;
    private final DocumentRepository documentRepository;
    private final AccountRepository accountRepository;

    public CommAssetLandController(
            CommAssetLandRepository commAssetLandRepository,
            DocumentUploadService documentUploadService,
            DocumentRepository documentRepository,
            AccountRepository accountRepository) {
        this.commAssetLandRepository = commAssetLandRepository;
        this.documentUploadService = documentUploadService;
        this.documentRepository = documentRepository;
        this.accountRepository = accountRepository;
    }

    @GetMapping
    @Operation(summary = "Get all commercial asset land records", description = "Retrieve paginated list of commercial asset land with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial asset land records retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<CommAssetLandProjection>> getAllCommAssetLand(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "description") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Filter by section category ID") @RequestParam(required = false) Long sectionCategoryId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving commercial asset land records - page: {}, size: {}, sort: {} {}, statusId: {}, documentId: {}, sectionCategoryId: {}, search: '{}'",
                    page, size, sort, direction, statusId, documentId, sectionCategoryId, search);

            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<CommAssetLandProjection> commAssetLands;

            // Priority: search > statusId > sectionCategoryId > documentId > all
            if (search != null && !search.trim().isEmpty()) {
                log.debug("Filtering commercial asset lands by search term: '{}'", search);
                commAssetLands = commAssetLandRepository.findByActiveTrueAndSearchTermsProjections(search.trim(), pageable);
            } else if (statusId != null) {
                log.debug("Filtering commercial asset lands by status ID: {}", statusId);
                commAssetLands = commAssetLandRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else if (sectionCategoryId != null) {
                log.debug("Filtering commercial asset lands by section category ID: {}", sectionCategoryId);
                commAssetLands = commAssetLandRepository.findByActiveTrueAndSectionCategoryIdProjections(sectionCategoryId, pageable);
            } else if (documentId != null) {
                log.debug("Filtering commercial asset lands by document ID: {}", documentId);
                commAssetLands = commAssetLandRepository.findByActiveTrueAndDocumentIdProjections(documentId, pageable);
            } else {
                log.debug("Retrieving all active commercial asset lands");
                commAssetLands = commAssetLandRepository.findAllActiveProjections(pageable);
            }

            log.info("Successfully retrieved {} commercial asset land records", commAssetLands.getTotalElements());
            return ResponseEntity.ok(commAssetLands);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for commercial asset land retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving commercial asset land records", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get commercial asset land by ID", description = "Retrieve a specific commercial asset land record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial asset land retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Commercial asset land not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getCommAssetLandById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid commercial asset land ID");
            }

            log.info("Retrieving commercial asset land by ID: {}", id);
            Optional<CommAssetLand> commAssetLand = commAssetLandRepository.findByIdAndActiveTrue(id);

            if (commAssetLand.isPresent()) {
                return ResponseUtil.success(commAssetLand.get(), "Commercial asset land retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Commercial asset land not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving commercial asset land with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve commercial asset land: " + e.getMessage());
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @Transactional
    @Operation(summary = "Create commercial asset land", description = "Create a new commercial asset land record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Commercial asset land created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createCommAssetLand(
            @RequestPart("commAssetLand") CommAssetLand commAssetLand,
            @RequestPart("file") MultipartFile file) {
        try {
            log.info("Creating new commercial asset land: {}", commAssetLand.getReference());

            // Validate required fields
            if (commAssetLand.getReference() == null || commAssetLand.getReference().trim().isEmpty()) {
                return ResponseUtil.badRequest("Reference is required");
            }

            if (commAssetLand.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }

            if (commAssetLand.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }

            // Verify account exists
            Account actualOwner = accountRepository.findById(commAssetLand.getDoneBy().getId())
                    .orElseThrow(() -> new RuntimeException("Account not found with ID: " + commAssetLand.getDoneBy().getId()));

            // Validate file is provided (mandatory)
            if (file == null || file.isEmpty()) {
                log.warn("Validation failed: Document file is required");
                return ResponseUtil.badRequest("Document file is required. Please upload a file to create the commercial asset land record.");
            }

            // Upload file to comm_asset_land folder
            String filePath;
            try {
                filePath = documentUploadService.uploadFile(file, "comm_asset_land");
                log.info("File uploaded successfully to: {}", filePath);
            } catch (IOException e) {
                log.error("Failed to upload file", e);
                return ResponseUtil.badRequest("Failed to upload file: " + e.getMessage());
            }

            // Extract file metadata
            String contentType = file.getContentType();
            Long fileSize = file.getSize();
            String fileExtension = documentUploadService.extractFileExtension(file.getOriginalFilename(), contentType);
            String uniqueFileName = Paths.get(filePath).getFileName().toString();
            String originalFileName = documentUploadService.generateOriginalFileName(
                    "Comm_Asset_Land",
                    commAssetLand.getReference(),
                    fileExtension
            );

            // Initialize and save document
            Document document = documentUploadService.initializeDocument(
                    uniqueFileName,
                    originalFileName,
                    contentType,
                    fileSize,
                    filePath,
                    actualOwner
            );
            Document savedDocument = documentRepository.save(document);
            log.info("Document saved with ID: {}", savedDocument.getId());

            // Link document to commercial asset land and save
            commAssetLand.setDocument(savedDocument);
            commAssetLand.setActive(true);
            CommAssetLand savedCommAssetLand = commAssetLandRepository.save(commAssetLand);

            return ResponseUtil.success(savedCommAssetLand, "Commercial asset land created successfully");
        } catch (Exception e) {
            log.error("Error creating commercial asset land", e);
            return ResponseUtil.badRequest("Failed to create commercial asset land: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update commercial asset land", description = "Update an existing commercial asset land record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial asset land updated successfully"),
        @ApiResponse(responseCode = "400", description = "Commercial asset land not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateCommAssetLand(@PathVariable Long id, @RequestBody CommAssetLand commAssetLand) {
        try {
            log.info("Updating commercial asset land with ID: {}", id);

            Optional<CommAssetLand> existingCommAssetLandOpt = commAssetLandRepository.findByIdAndActiveTrue(id);
            if (existingCommAssetLandOpt.isEmpty()) {
                return ResponseUtil.badRequest("Commercial asset land not found with ID: " + id);
            }

            CommAssetLand existingCommAssetLand = existingCommAssetLandOpt.get();

            // Update fields
            if (commAssetLand.getReference() != null) {
                existingCommAssetLand.setReference(commAssetLand.getReference());
            }
            if (commAssetLand.getDescription() != null) {
                existingCommAssetLand.setDescription(commAssetLand.getDescription());
            }
            if (commAssetLand.getEmplacement() != null) {
                existingCommAssetLand.setEmplacement(commAssetLand.getEmplacement());
            }
            if (commAssetLand.getCoordonneesGps() != null) {
                existingCommAssetLand.setCoordonneesGps(commAssetLand.getCoordonneesGps());
            }
            if (commAssetLand.getDateObtention() != null) {
                existingCommAssetLand.setDateObtention(commAssetLand.getDateObtention());
            }
            if (commAssetLand.getSection() != null) {
                existingCommAssetLand.setSection(commAssetLand.getSection());
            }
            if (commAssetLand.getStatus() != null) {
                existingCommAssetLand.setStatus(commAssetLand.getStatus());
            }

            CommAssetLand savedCommAssetLand = commAssetLandRepository.save(existingCommAssetLand);
            return ResponseUtil.success(savedCommAssetLand, "Commercial asset land updated successfully");
        } catch (Exception e) {
            log.error("Error updating commercial asset land with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update commercial asset land: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete commercial asset land", description = "Soft delete a commercial asset land record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial asset land deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Commercial asset land not found")
    })
    public ResponseEntity<Map<String, Object>> deleteCommAssetLand(@PathVariable Long id) {
        try {
            log.info("Deleting commercial asset land with ID: {}", id);

            Optional<CommAssetLand> commAssetLandOpt = commAssetLandRepository.findByIdAndActiveTrue(id);
            if (commAssetLandOpt.isEmpty()) {
                return ResponseUtil.badRequest("Commercial asset land not found with ID: " + id);
            }

            CommAssetLand commAssetLand = commAssetLandOpt.get();
            commAssetLand.setActive(false);
            commAssetLandRepository.save(commAssetLand);

            return ResponseUtil.success(null, "Commercial asset land deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting commercial asset land with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete commercial asset land: " + e.getMessage());
        }
    }

    @GetMapping("/by-section/{sectionCategoryId}")
    @Operation(summary = "Get commercial asset lands by section category", description = "Retrieve commercial asset lands filtered by section category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial asset lands retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid section category ID")
    })
    public ResponseEntity<Page<CommAssetLandProjection>> getCommAssetLandsBySectionCategory(
            @PathVariable Long sectionCategoryId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.info("Retrieving commercial asset lands by section category ID: {}", sectionCategoryId);

            Pageable pageable = ResponseUtil.createPageable(page, size, "reference", "asc");
            Page<CommAssetLandProjection> commAssetLands
                    = commAssetLandRepository.findByActiveTrueAndSectionCategoryIdProjections(sectionCategoryId, pageable);

            return ResponseEntity.ok(commAssetLands);
        } catch (Exception e) {
            log.error("Error retrieving commercial asset lands by section category: {}", sectionCategoryId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
