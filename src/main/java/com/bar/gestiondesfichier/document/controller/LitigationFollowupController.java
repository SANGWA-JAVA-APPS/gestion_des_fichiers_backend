package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.LitigationFollowup;
import com.bar.gestiondesfichier.document.projection.LitigationFollowupProjection;
import com.bar.gestiondesfichier.document.repository.LitigationFollowupRepository;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST controller for Litigation Followup management with 20-record default
 * pagination
 */
@RestController
@RequestMapping("/api/document/litigation-followup")
@DocumentControllerCors
@Tag(name = "Litigation Followup Management", description = "Litigation Followup CRUD operations with pagination")
@Slf4j
@RequiredArgsConstructor
public class LitigationFollowupController {

    private final LitigationFollowupRepository litigationFollowupRepository;

    @GetMapping
    @Operation(summary = "Get all litigation followup records", description = "Retrieve paginated list of litigation followup records with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Litigation followup records retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<LitigationFollowupProjection>> getAllLitigationFollowup(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "concern") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving litigation followup records - page: {}, size: {}, sort: {} {}, statusId: {}, search: '{}'",
                    page, size, sort, direction, statusId, search);

            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<LitigationFollowupProjection> litigations;

            // Priority: search > statusId > all
            if (search != null && !search.trim().isEmpty()) {
                log.debug("Filtering litigation followups by search term: '{}'", search);
                litigations = litigationFollowupRepository.findByActiveTrueAndSearchTermsProjections(search.trim(), pageable);
            } else if (statusId != null) {
                log.debug("Filtering litigation followups by status ID: {}", statusId);
                litigations = litigationFollowupRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else {
                log.debug("Retrieving all active litigation followups");
                litigations = litigationFollowupRepository.findAllActiveProjections(pageable);
            }

            log.info("Successfully retrieved {} litigation followup records", litigations.getTotalElements());
            return ResponseEntity.ok(litigations);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for litigation followup retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving litigation followup records", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get litigation followup by ID", description = "Retrieve a specific litigation followup record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Litigation followup record retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Litigation followup record not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getLitigationFollowupById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid litigation followup ID");
            }
            
            log.info("Retrieving litigation followup by ID: {}", id);
            Optional<LitigationFollowup> litigation = litigationFollowupRepository.findByIdAndActiveTrue(id);
            
            if (litigation.isPresent()) {
                return ResponseUtil.success(litigation.get(), "Litigation followup retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Litigation followup not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving litigation followup with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve litigation followup: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create litigation followup", description = "Create a new litigation followup record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Litigation followup record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createLitigationFollowup(@RequestBody LitigationFollowup litigation) {
        try {
            log.info("Creating new litigation followup record: {}", litigation.getConcern());
            
            // Validate required fields
            if (litigation.getConcern() == null || litigation.getConcern().trim().isEmpty()) {
                return ResponseUtil.badRequest("Concern is required");
            }
            
            if (litigation.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }
            
            if (litigation.getDocument() == null) {
                return ResponseUtil.badRequest("Document is required");
            }
            
            if (litigation.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }
            
            litigation.setActive(true);
            LitigationFollowup savedLitigation = litigationFollowupRepository.save(litigation);
            
            return ResponseUtil.success(savedLitigation, "Litigation followup created successfully");
        } catch (Exception e) {
            log.error("Error creating litigation followup", e);
            return ResponseUtil.badRequest("Failed to create litigation followup: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update litigation followup", description = "Update an existing litigation followup record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Litigation followup record updated successfully"),
        @ApiResponse(responseCode = "400", description = "Litigation followup record not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateLitigationFollowup(@PathVariable Long id, @RequestBody LitigationFollowup litigation) {
        try {
            log.info("Updating litigation followup with ID: {}", id);
            
            Optional<LitigationFollowup> existingLitigationOpt = litigationFollowupRepository.findByIdAndActiveTrue(id);
            if (existingLitigationOpt.isEmpty()) {
                return ResponseUtil.badRequest("Litigation followup not found with ID: " + id);
            }
            
            LitigationFollowup existingLitigation = existingLitigationOpt.get();
            
            // Update fields
            if (litigation.getConcern() != null) {
                existingLitigation.setConcern(litigation.getConcern());
            }
            if (litigation.getStatut() != null) {
                existingLitigation.setStatut(litigation.getStatut());
            }
            if (litigation.getCreationDate() != null) {
                existingLitigation.setCreationDate(litigation.getCreationDate());
            }
            if (litigation.getExpectedCompletion() != null) {
                existingLitigation.setExpectedCompletion(litigation.getExpectedCompletion());
            }
            if (litigation.getRiskValue() != null) {
                existingLitigation.setRiskValue(litigation.getRiskValue());
            }
            if (litigation.getStatus() != null) {
                existingLitigation.setStatus(litigation.getStatus());
            }
            
            LitigationFollowup savedLitigation = litigationFollowupRepository.save(existingLitigation);
            return ResponseUtil.success(savedLitigation, "Litigation followup updated successfully");
        } catch (Exception e) {
            log.error("Error updating litigation followup with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update litigation followup: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete litigation followup", description = "Soft delete a litigation followup record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Litigation followup record deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Litigation followup record not found")
    })
    public ResponseEntity<Map<String, Object>> deleteLitigationFollowup(@PathVariable Long id) {
        try {
            log.info("Deleting litigation followup with ID: {}", id);
            
            Optional<LitigationFollowup> litigationOpt = litigationFollowupRepository.findByIdAndActiveTrue(id);
            if (litigationOpt.isEmpty()) {
                return ResponseUtil.badRequest("Litigation followup not found with ID: " + id);
            }
            
            LitigationFollowup litigation = litigationOpt.get();
            litigation.setActive(false);
            litigationFollowupRepository.save(litigation);
            
            return ResponseUtil.success(null, "Litigation followup deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting litigation followup with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete litigation followup: " + e.getMessage());
        }
    }
}
