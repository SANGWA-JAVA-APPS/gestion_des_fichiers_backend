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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST controller for Equipment ID management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/equipment-id")
@DocumentControllerCors
@Tag(name = "Equipment ID Management", description = "Equipment ID CRUD operations with pagination")
@RequiredArgsConstructor
@Slf4j
public class EquipmentIdController {

    private final EquipmentIdRepository equipmentIdRepository;

    @GetMapping
    @Operation(summary = "Get all equipment IDs", description = "Retrieve paginated list of equipment IDs with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Equipment IDs retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<EquipmentIdProjection>> getAllEquipmentId(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "equipmentType") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by document ID") @RequestParam(required = false) Long documentId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving equipment IDs - page: {}, size: {}, sort: {} {}, statusId: {}, documentId: {}, search: {}",
                    page, size, sort, direction, statusId, documentId, search);

            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<EquipmentIdProjection> equipmentIds;

            if (search != null && !search.trim().isEmpty()) {
                equipmentIds = equipmentIdRepository.findByActiveTrueAndEquipmentTypeOrSerialNumberContainingProjections(search, pageable);
            } else if (statusId != null) {
                equipmentIds = equipmentIdRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else if (documentId != null) {
                equipmentIds = equipmentIdRepository.findByActiveTrueAndDocumentIdProjections(documentId, pageable);
            } else {
                equipmentIds = equipmentIdRepository.findAllActiveProjections(pageable);
            }

            return ResponseEntity.ok(equipmentIds);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for equipment ID retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving equipment IDs", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get equipment ID by ID", description = "Retrieve a specific equipment ID record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Equipment ID retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Equipment ID not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getEquipmentIdById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid equipment ID");
            }

            log.info("Retrieving equipment ID by ID: {}", id);
            Optional<EquipmentId> equipmentId = equipmentIdRepository.findByIdAndActiveTrue(id);

            if (equipmentId.isPresent()) {
                return ResponseUtil.success(equipmentId.get(), "Equipment ID retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Equipment ID not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving equipment ID with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve equipment ID: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create equipment ID", description = "Create a new equipment ID record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Equipment ID created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createEquipmentId(@RequestBody EquipmentId equipmentId) {
        try {
            log.info("Creating new equipment ID: {}", equipmentId.getEquipmentType());

            // Validate required fields
            if (equipmentId.getEquipmentType() == null || equipmentId.getEquipmentType().trim().isEmpty()) {
                return ResponseUtil.badRequest("Equipment type is required");
            }

            if (equipmentId.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }

            if (equipmentId.getDocument() == null) {
                return ResponseUtil.badRequest("Document is required");
            }

            if (equipmentId.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }

            equipmentId.setActive(true);
            EquipmentId savedEquipmentId = equipmentIdRepository.save(equipmentId);

            return ResponseUtil.success(savedEquipmentId, "Equipment ID created successfully");
        } catch (Exception e) {
            log.error("Error creating equipment ID", e);
            return ResponseUtil.badRequest("Failed to create equipment ID: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update equipment ID", description = "Update an existing equipment ID record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Equipment ID updated successfully"),
        @ApiResponse(responseCode = "400", description = "Equipment ID not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateEquipmentId(@PathVariable Long id, @RequestBody EquipmentId equipmentId) {
        try {
            log.info("Updating equipment ID with ID: {}", id);

            Optional<EquipmentId> existingEquipmentIdOpt = equipmentIdRepository.findByIdAndActiveTrue(id);
            if (existingEquipmentIdOpt.isEmpty()) {
                return ResponseUtil.badRequest("Equipment ID not found with ID: " + id);
            }

            EquipmentId existingEquipmentId = existingEquipmentIdOpt.get();

            // Update fields with correct field names
            if (equipmentId.getEquipmentType() != null) {
                existingEquipmentId.setEquipmentType(equipmentId.getEquipmentType());
            }
            if (equipmentId.getSerialNumber() != null) {
                existingEquipmentId.setSerialNumber(equipmentId.getSerialNumber());
            }
            if (equipmentId.getPlateNumber() != null) {
                existingEquipmentId.setPlateNumber(equipmentId.getPlateNumber());
            }
            if (equipmentId.getEtatEquipement() != null) {
                existingEquipmentId.setEtatEquipement(equipmentId.getEtatEquipement());
            }
            if (equipmentId.getDateAchat() != null) {
                existingEquipmentId.setDateAchat(equipmentId.getDateAchat());
            }
            if (equipmentId.getDateVisiteTechnique() != null) {
                existingEquipmentId.setDateVisiteTechnique(equipmentId.getDateVisiteTechnique());
            }
            if (equipmentId.getAssurance() != null) {
                existingEquipmentId.setAssurance(equipmentId.getAssurance());
            }
            if (equipmentId.getDocumentsTelecharger() != null) {
                existingEquipmentId.setDocumentsTelecharger(equipmentId.getDocumentsTelecharger());
            }
            if (equipmentId.getStatus() != null) {
                existingEquipmentId.setStatus(equipmentId.getStatus());
            }

            EquipmentId savedEquipmentId = equipmentIdRepository.save(existingEquipmentId);
            return ResponseUtil.success(savedEquipmentId, "Equipment ID updated successfully");
        } catch (Exception e) {
            log.error("Error updating equipment ID with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update equipment ID: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete equipment ID", description = "Soft delete an equipment ID record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Equipment ID deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Equipment ID not found")
    })
    public ResponseEntity<Map<String, Object>> deleteEquipmentId(@PathVariable Long id) {
        try {
            log.info("Deleting equipment ID with ID: {}", id);

            Optional<EquipmentId> equipmentIdOpt = equipmentIdRepository.findByIdAndActiveTrue(id);
            if (equipmentIdOpt.isEmpty()) {
                return ResponseUtil.badRequest("Equipment ID not found with ID: " + id);
            }

            EquipmentId equipmentId = equipmentIdOpt.get();
            equipmentId.setActive(false);
            equipmentIdRepository.save(equipmentId);

            return ResponseUtil.success(null, "Equipment ID deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting equipment ID with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete equipment ID: " + e.getMessage());
        }
    }
}
