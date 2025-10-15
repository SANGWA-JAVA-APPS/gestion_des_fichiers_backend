package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.CargoDamage;
import com.bar.gestiondesfichier.document.model.Document;
import com.bar.gestiondesfichier.document.projection.CargoDamageProjection;
import com.bar.gestiondesfichier.document.repository.CargoDamageRepository;
import com.bar.gestiondesfichier.document.repository.DocumentRepository;
import com.bar.gestiondesfichier.document.service.DocumentUploadService;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.repository.AccountRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
 * REST controller for Cargo Damage management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/cargo-damage")
@DocumentControllerCors
@Tag(name = "Document", description = "Cargo Damage CRUD operations with pagination")
@RequiredArgsConstructor
@Slf4j
public class CargoDamageController {

    private final CargoDamageRepository cargoDamageRepository;
    private final DocumentUploadService documentUploadService;
    private final DocumentRepository documentRepository;
    private final AccountRepository accountRepository;

    @GetMapping
    @Operation(summary = "Get all cargo damage records", description = "Retrieve paginated list of cargo damage records with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cargo damage records retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<CargoDamageProjection>> getAllCargoDamage(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "refeRequest") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving cargo damage records - page: {}, size: {}, sort: {} {}, statusId: {}, documentId: {}, search: '{}'",
                    page, size, sort, direction, statusId, documentId, search);

            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<CargoDamageProjection> projections;

            if (search != null && !search.trim().isEmpty()) {
                projections = cargoDamageRepository.findByActiveTrueAndSearchTermsProjections(search, pageable);
                log.debug("Found {} cargo damage records matching search term: '{}'", projections.getTotalElements(), search);
            } else if (statusId != null) {
                projections = cargoDamageRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
                log.debug("Found {} cargo damage records with statusId: {}", projections.getTotalElements(), statusId);
            } else if (documentId != null) {
                projections = cargoDamageRepository.findByActiveTrueAndDocumentIdProjections(documentId, pageable);
                log.debug("Found {} cargo damage records with documentId: {}", projections.getTotalElements(), documentId);
            } else {
                projections = cargoDamageRepository.findAllActiveProjections(pageable);
                log.debug("Found {} active cargo damage records", projections.getTotalElements());
            }

            return ResponseEntity.ok(projections);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for cargo damage retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving cargo damage records", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cargo damage by ID", description = "Retrieve a specific cargo damage record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cargo damage record retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Cargo damage record not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getCargoDamageById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid cargo damage ID");
            }

            log.info("Retrieving cargo damage by ID: {}", id);
            Optional<CargoDamage> cargoDamage = cargoDamageRepository.findByIdAndActiveTrue(id);

            if (cargoDamage.isPresent()) {
                return ResponseUtil.success(cargoDamage.get(), "Cargo damage retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Cargo damage not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving cargo damage with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve cargo damage: " + e.getMessage());
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @Transactional
    @Operation(summary = "Create new cargo damage record", description = "Create a new cargo damage record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cargo damage record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Map<String, Object>> createCargoDamage(
            @RequestPart("cargoDamage") CargoDamage cargoDamage,
            @RequestPart("file") MultipartFile file) {
        try {
            log.info("Creating new cargo damage record: {}", cargoDamage.getRefeRequest());

            // Validate required fields
            if (cargoDamage.getRefeRequest() == null || cargoDamage.getRefeRequest().trim().isEmpty()) {
                log.warn("Validation failed: refeRequest is required");
                return ResponseUtil.badRequest("Reference request is required");
            }

            if (cargoDamage.getDoneBy() == null) {
                log.warn("Validation failed: DoneBy (Account) is required");
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }

            if (cargoDamage.getStatus() == null) {
                log.warn("Validation failed: Status is required");
                return ResponseUtil.badRequest("Status is required");
            }

            // Verify account exists
            Account actualOwner = accountRepository.findById(cargoDamage.getDoneBy().getId())
                    .orElseThrow(() -> new RuntimeException("Account not found with ID: " + cargoDamage.getDoneBy().getId()));

            // Validate file is provided (mandatory)
            if (file == null || file.isEmpty()) {
                log.warn("Validation failed: Document file is required");
                return ResponseUtil.badRequest("Document file is required. Please upload a file to create the cargo damage record.");
            }

            // Upload file to cargo_damage folder
            String filePath;
            try {
                filePath = documentUploadService.uploadFile(file, "cargo_damage");
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
                    "Cargo_Damage",
                    cargoDamage.getRefeRequest(),
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

            // Link document to cargo damage and save
            cargoDamage.setDocument(savedDocument);
            cargoDamage.setActive(true);
            CargoDamage saved = cargoDamageRepository.save(cargoDamage);

            log.info("Cargo damage record created successfully with ID: {}", saved.getId());
            return ResponseUtil.success(saved, "Cargo damage record created successfully");
        } catch (Exception e) {
            log.error("Error creating cargo damage record", e);
            return ResponseUtil.badRequest(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cargo damage record", description = "Update existing cargo damage record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cargo damage record updated successfully"),
        @ApiResponse(responseCode = "404", description = "Cargo damage record not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Map<String, Object>> updateCargoDamage(@PathVariable Long id, @RequestBody CargoDamage cargoDamage) {
        try {
            log.info("Updating cargo damage record with ID: {}", id);

            Optional<CargoDamage> existingOpt = cargoDamageRepository.findByIdAndActiveTrue(id);
            if (existingOpt.isEmpty()) {
                log.warn("Cargo damage record not found with ID: {}", id);
                return ResponseUtil.badRequest("Cargo damage record not found");
            }

            CargoDamage existingCargoDamage = existingOpt.get();

            if (cargoDamage.getRefeRequest() != null) {
                existingCargoDamage.setRefeRequest(cargoDamage.getRefeRequest());
            }
            if (cargoDamage.getDescription() != null) {
                existingCargoDamage.setDescription(cargoDamage.getDescription());
            }
            if (cargoDamage.getQuotationContractNum() != null) {
                existingCargoDamage.setQuotationContractNum(cargoDamage.getQuotationContractNum());
            }
            if (cargoDamage.getDateRequest() != null) {
                existingCargoDamage.setDateRequest(cargoDamage.getDateRequest());
            }
            if (cargoDamage.getDateContract() != null) {
                existingCargoDamage.setDateContract(cargoDamage.getDateContract());
            }

            CargoDamage updated = cargoDamageRepository.save(existingCargoDamage);
            log.info("Cargo damage record updated successfully with ID: {}", updated.getId());

            return ResponseUtil.success("Cargo damage record updated successfully");
        } catch (Exception e) {
            log.error("Error updating cargo damage record with ID: {}", id, e);
            return ResponseUtil.badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cargo damage", description = "Soft delete a cargo damage record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cargo damage record deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cargo damage record not found")
    })
    public ResponseEntity<Map<String, Object>> deleteCargoDamage(@PathVariable Long id) {
        try {
            log.info("Deleting cargo damage with ID: {}", id);

            Optional<CargoDamage> cargoDamageOpt = cargoDamageRepository.findByIdAndActiveTrue(id);
            if (cargoDamageOpt.isEmpty()) {
                return ResponseUtil.badRequest("Cargo damage not found with ID: " + id);
            }

            CargoDamage cargoDamage = cargoDamageOpt.get();
            cargoDamage.setActive(false);
            cargoDamageRepository.save(cargoDamage);

            return ResponseUtil.success(null, "Cargo damage deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting cargo damage with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete cargo damage: " + e.getMessage());
        }
    }

}
