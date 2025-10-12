package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.DueDiligence;
import com.bar.gestiondesfichier.document.projection.DueDiligenceProjection;
import com.bar.gestiondesfichier.document.repository.DueDiligenceRepository;
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
 * REST controller for Due Diligence management with 20-record default
 * pagination
 */
@RestController
@RequestMapping("/api/document/due-diligence")
@DocumentControllerCors
@Tag(name = "Due Diligence Management", description = "Due Diligence CRUD operations with pagination")
@Slf4j
public class DueDiligenceController {

    private final DueDiligenceRepository dueDiligenceRepository;

    public DueDiligenceController(DueDiligenceRepository dueDiligenceRepository) {
        this.dueDiligenceRepository = dueDiligenceRepository;
    }

    @GetMapping
    @Operation(summary = "Get all due diligence records", description = "Retrieve paginated list of due diligence records with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Due diligence records retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<DueDiligenceProjection>> getAllDueDiligence(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "projectName") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by section ID") @RequestParam(required = false) Long sectionId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving due diligence records - page: {}, size: {}, sort: {} {}, statusId: {}, sectionId: {}, search: {}",
                    page, size, sort, direction, statusId, sectionId, search);

            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<DueDiligenceProjection> dueDiligenceRecords;

            if (search != null && !search.trim().isEmpty()) {
                dueDiligenceRecords = dueDiligenceRepository.findByActiveTrueAndSearchTermsProjections(search, pageable);
            } else if (statusId != null) {
                dueDiligenceRecords = dueDiligenceRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else if (sectionId != null) {
                dueDiligenceRecords = dueDiligenceRepository.findByActiveTrueAndSectionIdProjections(sectionId, pageable);
            } else {
                dueDiligenceRecords = dueDiligenceRepository.findAllActiveProjections(pageable);
            }

            return ResponseEntity.ok(dueDiligenceRecords);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for due diligence retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving due diligence records", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get due diligence by ID", description = "Retrieve a specific due diligence record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Due diligence record retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Due diligence record not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getDueDiligenceById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid due diligence ID");
            }

            log.info("Retrieving due diligence by ID: {}", id);
            Optional<DueDiligence> dueDiligence = dueDiligenceRepository.findByIdAndActiveTrue(id);

            if (dueDiligence.isPresent()) {
                return ResponseUtil.success(dueDiligence.get(), "Due diligence record retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Due diligence record not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving due diligence with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve due diligence: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create due diligence", description = "Create a new due diligence record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Due diligence record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createDueDiligence(@RequestBody DueDiligence dueDiligence) {
        try {
            log.info("Creating new due diligence record: {}", dueDiligence.getReference());

            // Validate required fields
            if (dueDiligence.getReference() == null || dueDiligence.getReference().trim().isEmpty()) {
                return ResponseUtil.badRequest("Reference is required");
            }

            if (dueDiligence.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }

            if (dueDiligence.getDocument() == null) {
                return ResponseUtil.badRequest("Document is required");
            }

            if (dueDiligence.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }

            dueDiligence.setActive(true);
            DueDiligence savedDueDiligence = dueDiligenceRepository.save(dueDiligence);

            return ResponseUtil.success(savedDueDiligence, "Due diligence record created successfully");
        } catch (Exception e) {
            log.error("Error creating due diligence record", e);
            return ResponseUtil.badRequest("Failed to create due diligence record: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update due diligence", description = "Update an existing due diligence record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Due diligence record updated successfully"),
        @ApiResponse(responseCode = "400", description = "Due diligence record not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateDueDiligence(@PathVariable Long id, @RequestBody DueDiligence dueDiligence) {
        try {
            log.info("Updating due diligence with ID: {}", id);

            Optional<DueDiligence> existingDueDiligenceOpt = dueDiligenceRepository.findByIdAndActiveTrue(id);
            if (existingDueDiligenceOpt.isEmpty()) {
                return ResponseUtil.badRequest("Due diligence record not found with ID: " + id);
            }

            DueDiligence existingDueDiligence = existingDueDiligenceOpt.get();

            // Update fields
            if (dueDiligence.getReference() != null) {
                existingDueDiligence.setReference(dueDiligence.getReference());
            }
            if (dueDiligence.getDescription() != null) {
                existingDueDiligence.setDescription(dueDiligence.getDescription());
            }
            if (dueDiligence.getDateDueDiligence() != null) {
                existingDueDiligence.setDateDueDiligence(dueDiligence.getDateDueDiligence());
            }
            if (dueDiligence.getAuditor() != null) {
                existingDueDiligence.setAuditor(dueDiligence.getAuditor());
            }
            if (dueDiligence.getCreationDate() != null) {
                existingDueDiligence.setCreationDate(dueDiligence.getCreationDate());
            }
            if (dueDiligence.getCompletionDate() != null) {
                existingDueDiligence.setCompletionDate(dueDiligence.getCompletionDate());
            }
            if (dueDiligence.getDocAttach() != null) {
                existingDueDiligence.setDocAttach(dueDiligence.getDocAttach());
            }
            if (dueDiligence.getSection() != null) {
                existingDueDiligence.setSection(dueDiligence.getSection());
            }
            if (dueDiligence.getStatus() != null) {
                existingDueDiligence.setStatus(dueDiligence.getStatus());
            }

            DueDiligence savedDueDiligence = dueDiligenceRepository.save(existingDueDiligence);
            return ResponseUtil.success(savedDueDiligence, "Due diligence record updated successfully");
        } catch (Exception e) {
            log.error("Error updating due diligence with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update due diligence record: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete due diligence", description = "Soft delete a due diligence record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Due diligence record deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Due diligence record not found")
    })
    public ResponseEntity<Map<String, Object>> deleteDueDiligence(@PathVariable Long id) {
        try {
            log.info("Deleting due diligence with ID: {}", id);

            Optional<DueDiligence> dueDiligenceOpt = dueDiligenceRepository.findByIdAndActiveTrue(id);
            if (dueDiligenceOpt.isEmpty()) {
                return ResponseUtil.badRequest("Due diligence record not found with ID: " + id);
            }

            DueDiligence dueDiligence = dueDiligenceOpt.get();
            dueDiligence.setActive(false);
            dueDiligenceRepository.save(dueDiligence);

            return ResponseUtil.success(null, "Due diligence record deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting due diligence with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete due diligence record: " + e.getMessage());
        }
    }

}
