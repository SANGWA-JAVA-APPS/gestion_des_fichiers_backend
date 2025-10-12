package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.NormeLoi;
import com.bar.gestiondesfichier.document.projection.NormeLoiProjection;
import com.bar.gestiondesfichier.document.repository.NormeLoiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST controller for NormeLoi management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/norme-loi")
@DocumentControllerCors
@Tag(name = "Norme Loi Management", description = "Legal Norms and Laws CRUD operations with pagination")
@Slf4j
public class NormeLoiController {

    private final NormeLoiRepository normeLoiRepository;

    public NormeLoiController(NormeLoiRepository normeLoiRepository) {
        this.normeLoiRepository = normeLoiRepository;
    }

    @GetMapping
    @Operation(summary = "Get all norme loi records", description = "Retrieve paginated list of legal norms and laws with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Norme loi records retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<NormeLoiProjection>> getAllNormeLoi(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "reference") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving norme loi records - page: {}, size: {}, sort: {} {}, statusId: {}, documentId: {}, search: '{}'", 
                    page, size, sort, direction, statusId, documentId, search);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<NormeLoiProjection> normeLois;
            
            // Priority: search > statusId > documentId > all
            if (search != null && !search.trim().isEmpty()) {
                log.debug("Filtering norme loi by search term: '{}'", search);
                normeLois = normeLoiRepository.findByActiveTrueAndSearchTermsProjections(search.trim(), pageable);
            } else if (statusId != null) {
                log.debug("Filtering norme loi by status ID: {}", statusId);
                normeLois = normeLoiRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else if (documentId != null) {
                log.debug("Filtering norme loi by document ID: {}", documentId);
                normeLois = normeLoiRepository.findByActiveTrueAndDocumentIdProjections(documentId, pageable);
            } else {
                log.debug("Retrieving all active norme loi records");
                normeLois = normeLoiRepository.findAllActiveProjections(pageable);
            }
            
            log.info("Successfully retrieved {} norme loi records", normeLois.getTotalElements());
            return ResponseEntity.ok(normeLois);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for norme loi retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving norme loi records", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get norme loi by ID", description = "Retrieve a specific norme loi record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Norme loi retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Norme loi not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getNormeLoiById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid norme loi ID");
            }
            
            log.info("Retrieving norme loi by ID: {}", id);
            Optional<NormeLoi> normeLoi = normeLoiRepository.findByIdAndActiveTrue(id);
            
            if (normeLoi.isPresent()) {
                return ResponseUtil.success(normeLoi.get(), "Norme loi retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Norme loi not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving norme loi with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve norme loi: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create norme loi", description = "Create a new norme loi record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Norme loi created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createNormeLoi(@RequestBody NormeLoi normeLoi) {
        try {
            log.info("Creating new norme loi: {}", normeLoi.getReference());
            
            // Validate required fields
            if (normeLoi.getReference() == null || normeLoi.getReference().trim().isEmpty()) {
                return ResponseUtil.badRequest("Reference is required");
            }
            
            if (normeLoi.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }
            
            if (normeLoi.getDocument() == null) {
                return ResponseUtil.badRequest("Document is required");
            }
            
            if (normeLoi.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }
            
            // Check if reference already exists
            if (normeLoiRepository.existsByReferenceAndActiveTrue(normeLoi.getReference())) {
                return ResponseUtil.badRequest("Norme loi with this reference already exists");
            }
            
            normeLoi.setActive(true);
            NormeLoi savedNormeLoi = normeLoiRepository.save(normeLoi);
            
            return ResponseUtil.success(savedNormeLoi, "Norme loi created successfully");
        } catch (Exception e) {
            log.error("Error creating norme loi", e);
            return ResponseUtil.badRequest("Failed to create norme loi: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update norme loi", description = "Update an existing norme loi record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Norme loi updated successfully"),
        @ApiResponse(responseCode = "400", description = "Norme loi not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateNormeLoi(@PathVariable Long id, @RequestBody NormeLoi normeLoi) {
        try {
            log.info("Updating norme loi with ID: {}", id);
            
            Optional<NormeLoi> existingNormeLoiOpt = normeLoiRepository.findByIdAndActiveTrue(id);
            if (existingNormeLoiOpt.isEmpty()) {
                return ResponseUtil.badRequest("Norme loi not found with ID: " + id);
            }
            
            NormeLoi existingNormeLoi = existingNormeLoiOpt.get();
            
            // Update fields
            if (normeLoi.getReference() != null) {
                existingNormeLoi.setReference(normeLoi.getReference());
            }
            if (normeLoi.getDescription() != null) {
                existingNormeLoi.setDescription(normeLoi.getDescription());
            }
            if (normeLoi.getDateVigueur() != null) {
                existingNormeLoi.setDateVigueur(normeLoi.getDateVigueur());
            }
            if (normeLoi.getDomaineApplication() != null) {
                existingNormeLoi.setDomaineApplication(normeLoi.getDomaineApplication());
            }
            if (normeLoi.getStatus() != null) {
                existingNormeLoi.setStatus(normeLoi.getStatus());
            }
            
            NormeLoi savedNormeLoi = normeLoiRepository.save(existingNormeLoi);
            return ResponseUtil.success(savedNormeLoi, "Norme loi updated successfully");
        } catch (Exception e) {
            log.error("Error updating norme loi with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update norme loi: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete norme loi", description = "Soft delete a norme loi record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Norme loi deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Norme loi not found")
    })
    public ResponseEntity<Map<String, Object>> deleteNormeLoi(@PathVariable Long id) {
        try {
            log.info("Deleting norme loi with ID: {}", id);
            
            Optional<NormeLoi> normeLoiOpt = normeLoiRepository.findByIdAndActiveTrue(id);
            if (normeLoiOpt.isEmpty()) {
                return ResponseUtil.badRequest("Norme loi not found with ID: " + id);
            }
            
            NormeLoi normeLoi = normeLoiOpt.get();
            normeLoi.setActive(false);
            normeLoiRepository.save(normeLoi);
            
            return ResponseUtil.success(null, "Norme loi deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting norme loi with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete norme loi: " + e.getMessage());
        }
    }
}