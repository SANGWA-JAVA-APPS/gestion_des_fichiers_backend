package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.*;
import com.bar.gestiondesfichier.document.projection.*;
import com.bar.gestiondesfichier.document.repository.*;
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
 * REST controller for Commercial Third Party management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/comm-third-party")
@DocumentControllerCors
@Tag(name = "Commercial Third Party Management", description = "Commercial Third Party CRUD operations with pagination")

@Slf4j
public class CommThirdPartyController {

    private final CommThirdPartyRepository commThirdPartyRepository;

    public CommThirdPartyController(CommThirdPartyRepository commThirdPartyRepository) {
        this.commThirdPartyRepository = commThirdPartyRepository;
    }

    @GetMapping
    @Operation(summary = "Get all commercial third parties", description = "Retrieve paginated list of commercial third parties with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial third parties retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<CommThirdPartyProjection>> getAllCommThirdParty(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving commercial third parties - page: {}, size: {}, sort: {} {}, statusId: {}, search: '{}'", 
                    page, size, sort, direction, statusId, search);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<CommThirdPartyProjection> thirdParties;

            // Priority: search > statusId > all
            if (search != null && !search.trim().isEmpty()) {
                log.debug("Filtering commercial third parties by search term: '{}'", search);
                thirdParties = commThirdPartyRepository.findByActiveTrueAndSearchTermsProjections(search.trim(), pageable);
            } else if (statusId != null) {
                log.debug("Filtering commercial third parties by status ID: {}", statusId);
                thirdParties = commThirdPartyRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else {
                log.debug("Retrieving all active commercial third parties");
                thirdParties = commThirdPartyRepository.findAllActiveProjections(pageable);
            }

            log.info("Successfully retrieved {} commercial third party records", thirdParties.getTotalElements());
            return ResponseEntity.ok(thirdParties);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for commercial third party retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving commercial third parties", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCommThirdPartyById(@PathVariable Long id) {
        try {
            Optional<CommThirdParty> thirdParty = commThirdPartyRepository.findByIdAndActiveTrue(id);

            if (thirdParty.isPresent()) {
                return ResponseUtil.success(thirdParty.get(), "Commercial third party retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Commercial third party not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving commercial third party with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve commercial third party: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCommThirdParty(@RequestBody CommThirdParty thirdParty) {
        try {
            if (thirdParty.getName() == null || thirdParty.getName().trim().isEmpty()) {
                return ResponseUtil.badRequest("Third party name is required");
            }
            if (thirdParty.getDoneBy() == null || thirdParty.getDocument() == null || thirdParty.getStatus() == null) {
                return ResponseUtil.badRequest("DoneBy, Document, and Status are required");
            }

            thirdParty.setActive(true);
            CommThirdParty savedThirdParty = commThirdPartyRepository.save(thirdParty);
            return ResponseUtil.success(savedThirdParty, "Commercial third party created successfully");
        } catch (Exception e) {
            log.error("Error creating commercial third party", e);
            return ResponseUtil.badRequest("Failed to create commercial third party: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCommThirdParty(@PathVariable Long id, @RequestBody CommThirdParty thirdParty) {
        try {
            Optional<CommThirdParty> existingOpt = commThirdPartyRepository.findByIdAndActiveTrue(id);
            if (existingOpt.isEmpty()) {
                return ResponseUtil.badRequest("Commercial third party not found with ID: " + id);
            }

            CommThirdParty existing = existingOpt.get();

            if (thirdParty.getName() != null) {
                existing.setName(thirdParty.getName());
            }
            if (thirdParty.getLocation() != null) {
                existing.setLocation(thirdParty.getLocation());
            }
            if (thirdParty.getValidity() != null) {
                existing.setValidity(thirdParty.getValidity());
            }
            if (thirdParty.getActivities() != null) {
                existing.setActivities(thirdParty.getActivities());
            }
            if (thirdParty.getSection() != null) {
                existing.setSection(thirdParty.getSection());
            }
            if (thirdParty.getStatus() != null) {
                existing.setStatus(thirdParty.getStatus());
            }

            CommThirdParty savedThirdParty = commThirdPartyRepository.save(existing);
            return ResponseUtil.success(savedThirdParty, "Commercial third party updated successfully");
        } catch (Exception e) {
            log.error("Error updating commercial third party with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update commercial third party: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCommThirdParty(@PathVariable Long id) {
        try {
            Optional<CommThirdParty> thirdPartyOpt = commThirdPartyRepository.findByIdAndActiveTrue(id);
            if (thirdPartyOpt.isEmpty()) {
                return ResponseUtil.badRequest("Commercial third party not found with ID: " + id);
            }

            CommThirdParty thirdParty = thirdPartyOpt.get();
            thirdParty.setActive(false);
            commThirdPartyRepository.save(thirdParty);

            return ResponseUtil.success(null, "Commercial third party deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting commercial third party with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete commercial third party: " + e.getMessage());
        }
    }
}
