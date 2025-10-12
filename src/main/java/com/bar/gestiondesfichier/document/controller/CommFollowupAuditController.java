package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.CommFollowupAudit;
import com.bar.gestiondesfichier.document.projection.CommFollowupAuditProjection;
import com.bar.gestiondesfichier.document.repository.CommFollowupAuditRepository;
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
 * REST controller for Commercial Followup Audit management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/comm-followup-audit")
@DocumentControllerCors
@Tag(name = "Commercial Followup Audit Management", description = "Commercial Followup Audit CRUD operations with pagination")
@Slf4j
public class CommFollowupAuditController {

    private final CommFollowupAuditRepository commFollowupAuditRepository;

    public CommFollowupAuditController(CommFollowupAuditRepository commFollowupAuditRepository) {
        this.commFollowupAuditRepository = commFollowupAuditRepository;
    }

    @GetMapping
    @Operation(summary = "Get all commercial followup audits", description = "Retrieve paginated list of commercial followup audits with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial followup audits retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<CommFollowupAuditProjection>> getAllCommFollowupAudit(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "reference") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by section ID") @RequestParam(required = false) Long sectionId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving commercial followup audits - page: {}, size: {}, sort: {} {}, statusId: {}, sectionId: {}, search: {}", 
                    page, size, sort, direction, statusId, sectionId, search);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<CommFollowupAuditProjection> audits;
            
            if (search != null && !search.trim().isEmpty()) {
                audits = commFollowupAuditRepository.findByActiveTrueAndSearchTermsProjections(search, pageable);
            } else if (statusId != null) {
                audits = commFollowupAuditRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else if (sectionId != null) {
                audits = commFollowupAuditRepository.findByActiveTrueAndSectionIdProjections(sectionId, pageable);
            } else {
                audits = commFollowupAuditRepository.findAllActiveProjections(pageable);
            }
            
            return ResponseEntity.ok(audits);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for commercial followup audit retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving commercial followup audits", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get commercial followup audit by ID", description = "Retrieve a specific commercial followup audit record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial followup audit retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Commercial followup audit not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getCommFollowupAuditById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid commercial followup audit ID");
            }
            
            log.info("Retrieving commercial followup audit by ID: {}", id);
            Optional<CommFollowupAudit> audit = commFollowupAuditRepository.findByIdAndActiveTrue(id);
            
            if (audit.isPresent()) {
                return ResponseUtil.success(audit.get(), "Commercial followup audit retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Commercial followup audit not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving commercial followup audit with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve commercial followup audit: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create commercial followup audit", description = "Create a new commercial followup audit record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Commercial followup audit created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createCommFollowupAudit(@RequestBody CommFollowupAudit audit) {
        try {
            log.info("Creating new commercial followup audit: {}", audit.getReference());
            
            // Validate required fields
            if (audit.getReference() == null || audit.getReference().trim().isEmpty()) {
                return ResponseUtil.badRequest("Reference is required");
            }
            
            if (audit.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }
            
            if (audit.getDocument() == null) {
                return ResponseUtil.badRequest("Document is required");
            }
            
            if (audit.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }
            
            audit.setActive(true);
            CommFollowupAudit savedAudit = commFollowupAuditRepository.save(audit);
            
            return ResponseUtil.success(savedAudit, "Commercial followup audit created successfully");
        } catch (Exception e) {
            log.error("Error creating commercial followup audit", e);
            return ResponseUtil.badRequest("Failed to create commercial followup audit: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update commercial followup audit", description = "Update an existing commercial followup audit record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial followup audit updated successfully"),
        @ApiResponse(responseCode = "400", description = "Commercial followup audit not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateCommFollowupAudit(@PathVariable Long id, @RequestBody CommFollowupAudit audit) {
        try {
            log.info("Updating commercial followup audit with ID: {}", id);
            
            Optional<CommFollowupAudit> existingAuditOpt = commFollowupAuditRepository.findByIdAndActiveTrue(id);
            if (existingAuditOpt.isEmpty()) {
                return ResponseUtil.badRequest("Commercial followup audit not found with ID: " + id);
            }
            
            CommFollowupAudit existingAudit = existingAuditOpt.get();
            
            // Update fields
            if (audit.getReference() != null) {
                existingAudit.setReference(audit.getReference());
            }
            if (audit.getDescription() != null) {
                existingAudit.setDescription(audit.getDescription());
            }
            if (audit.getDateAudit() != null) {
                existingAudit.setDateAudit(audit.getDateAudit());
            }
            if (audit.getAuditor() != null) {
                existingAudit.setAuditor(audit.getAuditor());
            }
            if (audit.getNumNonConform() != null) {
                existingAudit.setNumNonConform(audit.getNumNonConform());
            }
            if (audit.getTypeConform() != null) {
                existingAudit.setTypeConform(audit.getTypeConform());
            }
            if (audit.getPercentComplete() != null) {
                existingAudit.setPercentComplete(audit.getPercentComplete());
            }
            if (audit.getDocAttach() != null) {
                existingAudit.setDocAttach(audit.getDocAttach());
            }
            if (audit.getSection() != null) {
                existingAudit.setSection(audit.getSection());
            }
            if (audit.getStatus() != null) {
                existingAudit.setStatus(audit.getStatus());
            }
            
            CommFollowupAudit savedAudit = commFollowupAuditRepository.save(existingAudit);
            return ResponseUtil.success(savedAudit, "Commercial followup audit updated successfully");
        } catch (Exception e) {
            log.error("Error updating commercial followup audit with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update commercial followup audit: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete commercial followup audit", description = "Soft delete a commercial followup audit record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commercial followup audit deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Commercial followup audit not found")
    })
    public ResponseEntity<Map<String, Object>> deleteCommFollowupAudit(@PathVariable Long id) {
        try {
            log.info("Deleting commercial followup audit with ID: {}", id);
            
            Optional<CommFollowupAudit> auditOpt = commFollowupAuditRepository.findByIdAndActiveTrue(id);
            if (auditOpt.isEmpty()) {
                return ResponseUtil.badRequest("Commercial followup audit not found with ID: " + id);
            }
            
            CommFollowupAudit audit = auditOpt.get();
            audit.setActive(false);
            commFollowupAuditRepository.save(audit);
            
            return ResponseUtil.success(null, "Commercial followup audit deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting commercial followup audit with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete commercial followup audit: " + e.getMessage());
        }
    }
}