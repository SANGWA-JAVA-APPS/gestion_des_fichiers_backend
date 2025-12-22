package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.model.PermiConstruction;
import com.bar.gestiondesfichier.document.projection.PermiConstructionProjection;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.document.repository.PermiConstructionRepository;
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

    public PermiConstructionController(PermiConstructionRepository permiConstructionRepository,
                                       DocumentUploadService documentUploadService,
                                       DocumentRepository documentRepository,
                                       AccountRepository accountRepository) {
        this.permiConstructionRepository = permiConstructionRepository;
        this.documentUploadService = documentUploadService;
        this.documentRepository = documentRepository;
        this.accountRepository = accountRepository;
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
                projections = permiConstructionRepository.findAllActiveProjections(pageable);
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
    @Operation(summary = "Create construction permit", description = "Create a new construction permit record with file upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Construction permit created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or missing file")
    })
    public ResponseEntity<Map<String, Object>> createPermiConstruction(
            @RequestPart("permiConstruction") PermiConstruction permiConstruction,
            @RequestPart("file") MultipartFile file) {
        try {
            log.info("Creating new construction permit: {}", permiConstruction.getNumeroPermis());

            // Validate required fields
            if (permiConstruction.getNumeroPermis() == null || permiConstruction.getNumeroPermis().trim().isEmpty()) {
                return ResponseUtil.badRequest("Numero permis is required");
            }

            if (permiConstruction.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }

            if (permiConstruction.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }

            // Check if permit number already exists
            if (permiConstructionRepository.existsByNumeroPermisAndActiveTrue(permiConstruction.getNumeroPermis())) {
                return ResponseUtil.badRequest("Construction permit with this number already exists");
            }

            // Get the current user (owner) for the document
            Account owner = permiConstruction.getDoneBy();

            // Verify the account exists
            Optional<Account> accountOpt = accountRepository.findById(owner.getId());
            if (accountOpt.isEmpty()) {
                return ResponseUtil.badRequest("Account not found with ID: " + owner.getId());
            }
            Account actualOwner = accountOpt.get();

            // Prepare variables for document metadata
            String contentType;
            String fileExtension;
            String uniqueFileName;
            String originalFileName;
            String filePath;
            long fileSize;

            // Validate that file is provided (mandatory)
            if (file == null || file.isEmpty()) {
                log.warn("File upload is required but not provided for construction permit: {}",
                        permiConstruction.getNumeroPermis());
                return ResponseUtil.badRequest("Document file is required. Please upload a file to create the construction permit.");
            }

            // Handle file upload
            log.info("File upload detected: {}", file.getOriginalFilename());

            // Upload file and get file path
            try {
                filePath = documentUploadService.uploadFile(file, "permi_construction");

                // Extract information from uploaded file
                contentType = file.getContentType();
                fileSize = file.getSize();
                fileExtension = documentUploadService.extractFileExtension(file.getOriginalFilename(), contentType);

                // Extract unique filename from path
                uniqueFileName = Paths.get(filePath).getFileName().toString();

                // Generate original filename
                originalFileName = documentUploadService.generateOriginalFileName(
                        "Permi_Construction",
                        permiConstruction.getNumeroPermis(),
                        fileExtension
                );

                log.info("File uploaded successfully: {}", filePath);
            } catch (IOException e) {
                log.error("Failed to upload file", e);
                return ResponseUtil.badRequest("Failed to upload file: " + e.getMessage());
            }

            // Initialize the document using DocumentUploadService
            Document document = documentUploadService.initializeDocument(
                    uniqueFileName, originalFileName, contentType, fileSize, filePath, actualOwner);

            // Save the document first
            Document savedDocument = documentRepository.save(document);
            log.info("Created document with ID: {} and version: {} for construction permit: {}",
                    savedDocument.getId(), savedDocument.getVersion(), permiConstruction.getNumeroPermis());

            // Link the saved document to the construction permit
            permiConstruction.setDocument(savedDocument);
            permiConstruction.setActive(true);

            // Save the construction permit
            PermiConstruction savedPermiConstruction = permiConstructionRepository.save(permiConstruction);

            return ResponseUtil.success(savedPermiConstruction, "Construction permit created successfully");
        } catch (Exception e) {
            log.error("Error creating construction permit", e);
            return ResponseUtil.badRequest("Failed to create construction permit: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update construction permit", description = "Update an existing construction permit record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Construction permit updated successfully"),
        @ApiResponse(responseCode = "400", description = "Construction permit not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updatePermiConstruction(@PathVariable Long id, @RequestBody PermiConstruction permiConstruction) {
        try {
            log.info("Updating construction permit with ID: {}", id);

            Optional<PermiConstruction> existingPermiConstructionOpt = permiConstructionRepository.findByIdAndActiveTrue(id);
            if (existingPermiConstructionOpt.isEmpty()) {
                return ResponseUtil.badRequest("Construction permit not found with ID: " + id);
            }

            PermiConstruction existingPermiConstruction = existingPermiConstructionOpt.get();

            // Update fields
            if (permiConstruction.getNumeroPermis() != null) {
                existingPermiConstruction.setNumeroPermis(permiConstruction.getNumeroPermis());
            }
            if (permiConstruction.getProjet() != null) {
                existingPermiConstruction.setProjet(permiConstruction.getProjet());
            }
            if (permiConstruction.getLocalisation() != null) {
                existingPermiConstruction.setLocalisation(permiConstruction.getLocalisation());
            }
            if (permiConstruction.getDateDelivrance() != null) {
                existingPermiConstruction.setDateDelivrance(permiConstruction.getDateDelivrance());
            }
            if (permiConstruction.getDateExpiration() != null) {
                existingPermiConstruction.setDateExpiration(permiConstruction.getDateExpiration());
            }
            if (permiConstruction.getAutoriteDelivrance() != null) {
                existingPermiConstruction.setAutoriteDelivrance(permiConstruction.getAutoriteDelivrance());
            }
            if (permiConstruction.getSectionCategory() != null) {
                existingPermiConstruction.setSectionCategory(permiConstruction.getSectionCategory());
            }
            if (permiConstruction.getStatus() != null) {
                existingPermiConstruction.setStatus(permiConstruction.getStatus());
            }

            PermiConstruction savedPermiConstruction = permiConstructionRepository.save(existingPermiConstruction);
            return ResponseUtil.success(savedPermiConstruction, "Construction permit updated successfully");
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
