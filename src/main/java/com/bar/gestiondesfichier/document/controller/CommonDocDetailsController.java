package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.config.CurrentUser;
import com.bar.gestiondesfichier.document.dto.CommonDocDetailsRequestDTO;
import com.bar.gestiondesfichier.document.projection.CommonDocDetailsProjection;
import com.bar.gestiondesfichier.document.service.CommonDocDetailsService;

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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/document/common-doc-details")
@DocumentControllerCors
@Tag(name = "Common Document Details Management", description = "Common Document Details CRUD operations with pagination")
@Slf4j
public class CommonDocDetailsController {

    private final CommonDocDetailsService commonDocDetailsService;
    private final CurrentUser currentUser;

    public CommonDocDetailsController(
            CommonDocDetailsService commonDocDetailsService,
            CurrentUser currentUser) {
        this.commonDocDetailsService = commonDocDetailsService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Get all common document details", description = "Retrieve paginated list of common document details with default 20 records per page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Common document details retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<CommonDocDetailsProjection>> getAllCommonDocDetails(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "reference") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by section category ID") @RequestParam(required = false) Long sectionCategoryId,
            @Parameter(description = "Filter by section category code") @RequestParam(required = false) String sectionCode,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        
        Long ownerId = currentUser.isUser() ? currentUser.getAccountId() : null;
        
        try {
            log.info("Retrieving common document details - page: {}, size: {}, sort: {} {}, status: {}, sectionCategoryId: {}, search: '{}'",
                    page, size, sort, direction, status, sectionCategoryId, search);

            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<CommonDocDetailsProjection> commonDocDetails = commonDocDetailsService.getCommonDocDetails(
                    null, status, sectionCategoryId, sectionCode, ownerId, search, pageable);

            log.info("Successfully retrieved {} common document details", commonDocDetails.getTotalElements());
            return ResponseEntity.ok(commonDocDetails);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for common document details retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving common document details", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get common document details by ID", description = "Retrieve a specific common document details record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Common document details retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Common document details not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getCommonDocDetailsById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid common document details ID");
            }

            log.info("Retrieving common document details by ID: {}", id);
            CommonDocDetailsProjection commonDocDetails = commonDocDetailsService.getCommonDocDetailsById(id);

            return ResponseUtil.success(commonDocDetails, "Common document details retrieved successfully");
        } catch (IllegalArgumentException e) {
            return ResponseUtil.badRequest("Common document details not found with ID: " + id);
        } catch (Exception e) {
            log.error("Error retrieving common document details with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve common document details: " + e.getMessage());
        }
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(summary = "Create common document details", description = "Create a new common document details record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Common document details created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createCommonDocDetails(
            @RequestPart("commonDocDetails") CommonDocDetailsRequestDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            log.info("Creating new common document details: {}", request.getReference());

            CommonDocDetailsProjection savedCommonDocDetails = commonDocDetailsService.createCommonDocDetails(request, file);
            return ResponseUtil.success(savedCommonDocDetails, "Common document details created successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed: {}", e.getMessage());
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating common document details", e);
            return ResponseUtil.badRequest("Failed to create common document details: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update common document details", description = "Update an existing common document details record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Common document details updated successfully"),
        @ApiResponse(responseCode = "400", description = "Common document details not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateCommonDocDetails(
            @PathVariable Long id, 
            @RequestBody CommonDocDetailsRequestDTO request) {
        try {
            log.info("Updating common document details with ID: {}", id);

            CommonDocDetailsProjection updatedCommonDocDetails = commonDocDetailsService.updateCommonDocDetails(id, request, null);
            return ResponseUtil.success(updatedCommonDocDetails, "Common document details updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating common document details with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update common document details: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @Operation(summary = "Update common document details with file", description = "Update an existing common document details record with optional file upload")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Common document details updated successfully"),
        @ApiResponse(responseCode = "400", description = "Common document details not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateCommonDocDetailsWithFile(
            @PathVariable Long id,
            @RequestPart("commonDocDetails") CommonDocDetailsRequestDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            log.info("Updating common document details with file, ID: {}", id);

            CommonDocDetailsProjection updatedCommonDocDetails = commonDocDetailsService.updateCommonDocDetails(id, request, file);
            String message = file != null && !file.isEmpty() ? 
                "Common document details updated successfully with new file" : 
                "Common document details updated successfully";
            
            return ResponseUtil.success(updatedCommonDocDetails, message);
        } catch (IllegalArgumentException e) {
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating common document details with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update common document details: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete common document details", description = "Soft delete a common document details record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Common document details deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Common document details not found")
    })
    public ResponseEntity<Map<String, Object>> deleteCommonDocDetails(@PathVariable Long id) {
        try {
            log.info("Deleting common document details with ID: {}", id);

            commonDocDetailsService.deleteCommonDocDetails(id);
            return ResponseUtil.success(null, "Common document details deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting common document details with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete common document details: " + e.getMessage());
        }
    }
}