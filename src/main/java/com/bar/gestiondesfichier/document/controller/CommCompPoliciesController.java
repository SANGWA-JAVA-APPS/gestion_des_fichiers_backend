package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.CommCompPolicies;
import com.bar.gestiondesfichier.document.projection.CommCompPoliciesProjection;
import com.bar.gestiondesfichier.document.repository.CommCompPoliciesRepository;
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
 * REST controller for Commercial Compliance Policies management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/comm-comp-policies")
@DocumentControllerCors
@Tag(name = "Commercial Compliance Policies Management", description = "Commercial Compliance Policies CRUD operations with pagination")
@Slf4j
public class CommCompPoliciesController {

    private final CommCompPoliciesRepository commCompPoliciesRepository;

    public CommCompPoliciesController(CommCompPoliciesRepository commCompPoliciesRepository) {
        this.commCompPoliciesRepository = commCompPoliciesRepository;
    }

    @GetMapping
    @Operation(summary = "Get all commercial compliance policies", description = "Retrieve paginated list of commercial compliance policies with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial compliance policies retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<CommCompPoliciesProjection>> getAllCommCompPolicies(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "reference") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving commercial compliance policies - page: {}, size: {}, sort: {} {}, statusId: {}, documentId: {}, search: '{}'", 
                    page, size, sort, direction, statusId, documentId, search);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<CommCompPoliciesProjection> projections;
            
            if (search != null && !search.trim().isEmpty()) {
                projections = commCompPoliciesRepository.findByActiveTrueAndSearchTermsProjections(search, pageable);
                log.debug("Found {} commercial compliance policies matching search term: '{}'", projections.getTotalElements(), search);
            } else if (statusId != null) {
                projections = commCompPoliciesRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
                log.debug("Found {} commercial compliance policies with statusId: {}", projections.getTotalElements(), statusId);
            } else if (documentId != null) {
                projections = commCompPoliciesRepository.findByActiveTrueAndDocumentIdProjections(documentId, pageable);
                log.debug("Found {} commercial compliance policies with documentId: {}", projections.getTotalElements(), documentId);
            } else {
                projections = commCompPoliciesRepository.findAllActiveProjections(pageable);
                log.debug("Found {} active commercial compliance policies", projections.getTotalElements());
            }
            
            return ResponseEntity.ok(projections);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for commercial compliance policy retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving commercial compliance policies", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCommCompPolicyById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid policy ID");
            }
            
            Optional<CommCompPolicies> policy = commCompPoliciesRepository.findByIdAndActiveTrue(id);
            
            if (policy.isPresent()) {
                return ResponseUtil.success(policy.get(), "Commercial compliance policy retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Commercial compliance policy not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving commercial compliance policy with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve commercial compliance policy: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create commercial company policy", description = "Create a new commercial company policy record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Commercial company policy created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createCommCompPolicy(@RequestBody CommCompPolicies policy) {
        try {
            log.info("Creating new commercial company policy: {}", policy.getReference());
            
            // Validate required fields
            if (policy.getReference() == null || policy.getReference().trim().isEmpty()) {
                return ResponseUtil.badRequest("Reference is required");
            }
            if (policy.getDoneBy() == null || policy.getDocument() == null || policy.getStatus() == null) {
                return ResponseUtil.badRequest("DoneBy, Document, and Status are required");
            }
            
            policy.setActive(true);
            CommCompPolicies savedPolicy = commCompPoliciesRepository.save(policy);
            return ResponseUtil.success(savedPolicy, "Commercial company policy created successfully");
        } catch (Exception e) {
            log.error("Error creating commercial company policy", e);
            return ResponseUtil.badRequest("Failed to create commercial company policy: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update commercial company policy", description = "Update an existing commercial company policy record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial company policy updated successfully"),
        @ApiResponse(responseCode = "400", description = "Commercial company policy not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateCommCompPolicy(@PathVariable Long id, @RequestBody CommCompPolicies policy) {
        try {
            log.info("Updating commercial company policy with ID: {}", id);
            
            Optional<CommCompPolicies> existingOpt = commCompPoliciesRepository.findByIdAndActiveTrue(id);
            if (existingOpt.isEmpty()) {
                return ResponseUtil.badRequest("Commercial company policy not found with ID: " + id);
            }
            
            CommCompPolicies existing = existingOpt.get();
            
            // Update fields
            if (policy.getReference() != null) existing.setReference(policy.getReference());
            if (policy.getDescription() != null) existing.setDescription(policy.getDescription());
            if (policy.getPolicyStatus() != null) existing.setPolicyStatus(policy.getPolicyStatus());
            if (policy.getVersion() != null) existing.setVersion(policy.getVersion());
            if (policy.getExpirationDate() != null) existing.setExpirationDate(policy.getExpirationDate());
            if (policy.getSection() != null) existing.setSection(policy.getSection());
            if (policy.getStatus() != null) existing.setStatus(policy.getStatus());
            
            CommCompPolicies savedPolicy = commCompPoliciesRepository.save(existing);
            return ResponseUtil.success(savedPolicy, "Commercial company policy updated successfully");
        } catch (Exception e) {
            log.error("Error updating commercial company policy with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update commercial company policy: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCommCompPolicy(@PathVariable Long id) {
        try {
            Optional<CommCompPolicies> policyOpt = commCompPoliciesRepository.findByIdAndActiveTrue(id);
            if (policyOpt.isEmpty()) {
                return ResponseUtil.badRequest("Commercial compliance policy not found with ID: " + id);
            }
            
            CommCompPolicies policy = policyOpt.get();
            policy.setActive(false);
            commCompPoliciesRepository.save(policy);
            
            return ResponseUtil.success(null, "Commercial compliance policy deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting commercial compliance policy with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete commercial compliance policy: " + e.getMessage());
        }
    }
}