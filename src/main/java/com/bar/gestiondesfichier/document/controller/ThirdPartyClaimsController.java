package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.ThirdPartyClaims;
import com.bar.gestiondesfichier.document.projection.ThirdPartyClaimsProjection;
import com.bar.gestiondesfichier.document.repository.ThirdPartyClaimsRepository;
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
 * REST controller for Third Party Claims management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/third-party-claims")
@DocumentControllerCors
@Tag(name = "Third Party Claims Management", description = "Third Party Claims CRUD operations with pagination")
@Slf4j
public class ThirdPartyClaimsController {

    private final ThirdPartyClaimsRepository thirdPartyClaimsRepository;

    public ThirdPartyClaimsController(ThirdPartyClaimsRepository thirdPartyClaimsRepository) {
        this.thirdPartyClaimsRepository = thirdPartyClaimsRepository;
    }

    @GetMapping
    @Operation(summary = "Get all third party claims", description = "Retrieve paginated list of third party claims with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Third party claims retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<ThirdPartyClaimsProjection>> getAllThirdPartyClaims(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "reference") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving third party claims - page: {}, size: {}, sort: {} {}, statusId: {}, search: {}", 
                    page, size, sort, direction, statusId, search);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<ThirdPartyClaimsProjection> claims;
            
            if (search != null && !search.trim().isEmpty()) {
                claims = thirdPartyClaimsRepository.findByActiveTrueAndSearchTermsProjections(search, pageable);
            } else if (statusId != null) {
                claims = thirdPartyClaimsRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else {
                claims = thirdPartyClaimsRepository.findAllActiveProjections(pageable);
            }
            
            return ResponseEntity.ok(claims);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for third party claims retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving third party claims", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get third party claim by ID", description = "Retrieve a specific third party claim by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Third party claim retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Third party claim not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getThirdPartyClaimById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid third party claim ID");
            }
            
            log.info("Retrieving third party claim by ID: {}", id);
            Optional<ThirdPartyClaims> claim = thirdPartyClaimsRepository.findByIdAndActiveTrue(id);
            
            if (claim.isPresent()) {
                return ResponseUtil.success(claim.get(), "Third party claim retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Third party claim not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving third party claim with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve third party claim: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create third party claim", description = "Create a new third party claim record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Third party claim created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createThirdPartyClaim(@RequestBody ThirdPartyClaims claim) {
        try {
            log.info("Creating new third party claim: {}", claim.getReference());
            
            // Validate required fields
            if (claim.getReference() == null || claim.getReference().trim().isEmpty()) {
                return ResponseUtil.badRequest("Reference is required");
            }
            
            if (claim.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }
            
            if (claim.getDocument() == null) {
                return ResponseUtil.badRequest("Document is required");
            }
            
            if (claim.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }
            
            claim.setActive(true);
            ThirdPartyClaims savedClaim = thirdPartyClaimsRepository.save(claim);
            
            return ResponseUtil.success(savedClaim, "Third party claim created successfully");
        } catch (Exception e) {
            log.error("Error creating third party claim", e);
            return ResponseUtil.badRequest("Failed to create third party claim: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update third party claim", description = "Update an existing third party claim record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Third party claim updated successfully"),
        @ApiResponse(responseCode = "400", description = "Third party claim not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateThirdPartyClaim(@PathVariable Long id, @RequestBody ThirdPartyClaims claim) {
        try {
            log.info("Updating third party claim with ID: {}", id);
            
            Optional<ThirdPartyClaims> existingOpt = thirdPartyClaimsRepository.findByIdAndActiveTrue(id);
            if (existingOpt.isEmpty()) {
                return ResponseUtil.badRequest("Third party claim not found with ID: " + id);
            }
            
            ThirdPartyClaims existing = existingOpt.get();
            
            // Update fields
            if (claim.getReference() != null) {
                existing.setReference(claim.getReference());
            }
            if (claim.getDescription() != null) {
                existing.setDescription(claim.getDescription());
            }
            if (claim.getDateClaim() != null) {
                existing.setDateClaim(claim.getDateClaim());
            }
            if (claim.getDepartmentInCharge() != null) {
                existing.setDepartmentInCharge(claim.getDepartmentInCharge());
            }
            if (claim.getStatus() != null) {
                existing.setStatus(claim.getStatus());
            }
            
            ThirdPartyClaims savedClaim = thirdPartyClaimsRepository.save(existing);
            return ResponseUtil.success(savedClaim, "Third party claim updated successfully");
        } catch (Exception e) {
            log.error("Error updating third party claim with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update third party claim: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete third party claim", description = "Soft delete a third party claim record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Third party claim deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Third party claim not found")
    })
    public ResponseEntity<Map<String, Object>> deleteThirdPartyClaim(@PathVariable Long id) {
        try {
            log.info("Deleting third party claim with ID: {}", id);
            
            Optional<ThirdPartyClaims> claimOpt = thirdPartyClaimsRepository.findByIdAndActiveTrue(id);
            if (claimOpt.isEmpty()) {
                return ResponseUtil.badRequest("Third party claim not found with ID: " + id);
            }
            
            ThirdPartyClaims claim = claimOpt.get();
            claim.setActive(false);
            thirdPartyClaimsRepository.save(claim);
            
            return ResponseUtil.success(null, "Third party claim deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting third party claim with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete third party claim: " + e.getMessage());
        }
    }

    @GetMapping("/by-department/{departmentInCharge}")
    @Operation(summary = "Get third party claims by department", description = "Retrieve third party claims filtered by department in charge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Third party claims retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid department or request parameters")
    })
    public ResponseEntity<Page<ThirdPartyClaimsProjection>> getThirdPartyClaimsByDepartment(
            @PathVariable String departmentInCharge,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.info("Retrieving third party claims by department: {} - page: {}, size: {}", departmentInCharge, page, size);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, "dateClaim", "desc");
            Page<ThirdPartyClaimsProjection> claims = 
                thirdPartyClaimsRepository.findAllByDepartmentInChargeProjections(departmentInCharge, pageable);
            
            return ResponseEntity.ok(claims);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for third party claims by department: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving third party claims by department: {}", departmentInCharge, e);
            return ResponseEntity.badRequest().build();
        }
    }
}