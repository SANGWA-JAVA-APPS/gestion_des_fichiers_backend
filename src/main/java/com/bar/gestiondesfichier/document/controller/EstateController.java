package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.Estate;
import com.bar.gestiondesfichier.document.projection.EstateProjection;
import com.bar.gestiondesfichier.document.repository.EstateRepository;
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
 * REST controller for Estate management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/estate")
@DocumentControllerCors
@Tag(name = "Estate Management", description = "Estate CRUD operations with pagination")
@Slf4j
@RequiredArgsConstructor
public class EstateController {

    private final EstateRepository estateRepository;

    @GetMapping
    @Operation(summary = "Get all estates", description = "Retrieve paginated list of estates with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estates retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<EstateProjection>> getAllEstate(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "reference") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving estates - page: {}, size: {}, sort: {} {}, statusId: {}, search: {}", 
                    page, size, sort, direction, statusId, search);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<EstateProjection> estates;
            
            if (search != null && !search.trim().isEmpty()) {
                estates = estateRepository.findByActiveTrueAndReferenceOrEstateTypeContainingProjections(search, pageable);
            } else if (statusId != null) {
                estates = estateRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else if (documentId != null) {
                estates = estateRepository.findByActiveTrueAndDocumentIdProjections(documentId, pageable);
            } else {
                estates = estateRepository.findAllActiveProjections(pageable);
            }
            
            return ResponseEntity.ok(estates);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for estate retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving estates", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get estate by ID", description = "Retrieve a specific estate record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estate retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Estate not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getEstateById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid estate ID");
            }
            
            log.info("Retrieving estate by ID: {}", id);
            Optional<Estate> estate = estateRepository.findByIdAndActiveTrue(id);
            
            if (estate.isPresent()) {
                return ResponseUtil.success(estate.get(), "Estate retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Estate not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving estate with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve estate: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create estate", description = "Create a new estate record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estate created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createEstate(@RequestBody Estate estate) {
        try {
            log.info("Creating new estate: {}", estate.getReference());
            
            // Validate required fields
            if (estate.getReference() == null || estate.getReference().trim().isEmpty()) {
                return ResponseUtil.badRequest("Reference is required");
            }
            
            if (estate.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }
            
            if (estate.getDocument() == null) {
                return ResponseUtil.badRequest("Document is required");
            }
            
            if (estate.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }
            
            estate.setActive(true);
            Estate savedEstate = estateRepository.save(estate);
            
            return ResponseUtil.success(savedEstate, "Estate created successfully");
        } catch (Exception e) {
            log.error("Error creating estate", e);
            return ResponseUtil.badRequest("Failed to create estate: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update estate", description = "Update an existing estate record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estate updated successfully"),
        @ApiResponse(responseCode = "400", description = "Estate not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateEstate(@PathVariable Long id, @RequestBody Estate estate) {
        try {
            log.info("Updating estate with ID: {}", id);
            
            Optional<Estate> existingEstateOpt = estateRepository.findByIdAndActiveTrue(id);
            if (existingEstateOpt.isEmpty()) {
                return ResponseUtil.badRequest("Estate not found with ID: " + id);
            }
            
            Estate existingEstate = existingEstateOpt.get();
            
            // Update fields
            if (estate.getReference() != null) {
                existingEstate.setReference(estate.getReference());
            }
            if (estate.getEstateType() != null) {
                existingEstate.setEstateType(estate.getEstateType());
            }
            if (estate.getEmplacement() != null) {
                existingEstate.setEmplacement(estate.getEmplacement());
            }
            if (estate.getCoordonneesGps() != null) {
                existingEstate.setCoordonneesGps(estate.getCoordonneesGps());
            }
            if (estate.getDateOfBuilding() != null) {
                existingEstate.setDateOfBuilding(estate.getDateOfBuilding());
            }
            if (estate.getComments() != null) {
                existingEstate.setComments(estate.getComments());
            }
            if (estate.getStatus() != null) {
                existingEstate.setStatus(estate.getStatus());
            }
            
            Estate savedEstate = estateRepository.save(existingEstate);
            return ResponseUtil.success(savedEstate, "Estate updated successfully");
        } catch (Exception e) {
            log.error("Error updating estate with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update estate: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete estate", description = "Soft delete an estate record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estate deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Estate not found")
    })
    public ResponseEntity<Map<String, Object>> deleteEstate(@PathVariable Long id) {
        try {
            log.info("Deleting estate with ID: {}", id);
            
            Optional<Estate> estateOpt = estateRepository.findByIdAndActiveTrue(id);
            if (estateOpt.isEmpty()) {
                return ResponseUtil.badRequest("Estate not found with ID: " + id);
            }
            
            Estate estate = estateOpt.get();
            estate.setActive(false);
            estateRepository.save(estate);
            
            return ResponseUtil.success(null, "Estate deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting estate with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete estate: " + e.getMessage());
        }
    }
}