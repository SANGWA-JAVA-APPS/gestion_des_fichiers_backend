package com.bar.gestiondesfichier.document.controller;

import com.bar.gestiondesfichier.common.annotation.DocumentControllerCors;
import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.Insurance;
import com.bar.gestiondesfichier.document.projection.InsuranceProjection;
import com.bar.gestiondesfichier.document.repository.InsuranceRepository;
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
 * REST controller for Insurance management with 20-record default pagination
 */
@RestController
@RequestMapping("/api/document/insurance")
@DocumentControllerCors
@Tag(name = "Insurance Management", description = "Insurance CRUD operations with pagination")
@Slf4j

public class InsuranceController {

    private final InsuranceRepository insuranceRepository;

    public InsuranceController(InsuranceRepository insuranceRepository) {
        this.insuranceRepository = insuranceRepository;
    }

    @GetMapping
    @Operation(summary = "Get all insurance records", description = "Retrieve paginated list of insurance records with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insurance records retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Page<InsuranceProjection>> getAllInsurance(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "concerns") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by status ID") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {
        try {
            log.info("Retrieving insurance records - page: {}, size: {}, sort: {} {}, statusId: {}, search: {}", 
                    page, size, sort, direction, statusId, search);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<InsuranceProjection> insurances;
            
            if (search != null && !search.trim().isEmpty()) {
                insurances = insuranceRepository.findByActiveTrueAndSearchTermsProjections(search, pageable);
            } else if (statusId != null) {
                insurances = insuranceRepository.findByActiveTrueAndStatusIdProjections(statusId, pageable);
            } else {
                insurances = insuranceRepository.findAllActiveProjections(pageable);
            }
            
            return ResponseEntity.ok(insurances);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for insurance retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving insurance records", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get insurance by ID", description = "Retrieve a specific insurance record by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insurance record retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Insurance not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getInsuranceById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid insurance ID");
            }
            
            log.info("Retrieving insurance by ID: {}", id);
            Optional<Insurance> insurance = insuranceRepository.findByIdAndActiveTrue(id);
            
            if (insurance.isPresent()) {
                return ResponseUtil.success(insurance.get(), "Insurance retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Insurance not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving insurance with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve insurance: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create insurance record", description = "Create a new insurance record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Insurance created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Map<String, Object>> createInsurance(@RequestBody Insurance insurance) {
        try {
            log.info("Creating new insurance: {}", insurance.getConcerns());
            
            // Validate required fields
            if (insurance.getConcerns() == null || insurance.getConcerns().trim().isEmpty()) {
                return ResponseUtil.badRequest("Concerns field is required");
            }
            
            if (insurance.getDoneBy() == null) {
                return ResponseUtil.badRequest("DoneBy (Account) is required");
            }
            
            if (insurance.getDocument() == null) {
                return ResponseUtil.badRequest("Document is required");
            }
            
            if (insurance.getStatus() == null) {
                return ResponseUtil.badRequest("Status is required");
            }
            
            insurance.setActive(true);
            Insurance savedInsurance = insuranceRepository.save(insurance);
            
            return ResponseUtil.success(savedInsurance, "Insurance created successfully");
        } catch (Exception e) {
            log.error("Error creating insurance", e);
            return ResponseUtil.badRequest("Failed to create insurance: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update insurance record", description = "Update an existing insurance record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insurance updated successfully"),
        @ApiResponse(responseCode = "400", description = "Insurance not found or invalid data")
    })
    public ResponseEntity<Map<String, Object>> updateInsurance(@PathVariable Long id, @RequestBody Insurance insurance) {
        try {
            log.info("Updating insurance with ID: {}", id);
            
            Optional<Insurance> existingOpt = insuranceRepository.findByIdAndActiveTrue(id);
            if (existingOpt.isEmpty()) {
                return ResponseUtil.badRequest("Insurance not found with ID: " + id);
            }
            
            Insurance existing = existingOpt.get();
            
            // Update fields
            if (insurance.getConcerns() != null) {
                existing.setConcerns(insurance.getConcerns());
            }
            if (insurance.getCoverage() != null) {
                existing.setCoverage(insurance.getCoverage());
            }
            if (insurance.getValues() != null) {
                existing.setValues(insurance.getValues());
            }
            if (insurance.getDateValidity() != null) {
                existing.setDateValidity(insurance.getDateValidity());
            }
            if (insurance.getRenewalDate() != null) {
                existing.setRenewalDate(insurance.getRenewalDate());
            }
            if (insurance.getStatus() != null) {
                existing.setStatus(insurance.getStatus());
            }
            
            Insurance savedInsurance = insuranceRepository.save(existing);
            return ResponseUtil.success(savedInsurance, "Insurance updated successfully");
        } catch (Exception e) {
            log.error("Error updating insurance with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update insurance: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete insurance record", description = "Soft delete an insurance record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Insurance deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Insurance not found")
    })
    public ResponseEntity<Map<String, Object>> deleteInsurance(@PathVariable Long id) {
        try {
            log.info("Deleting insurance with ID: {}", id);
            
            Optional<Insurance> insuranceOpt = insuranceRepository.findByIdAndActiveTrue(id);
            if (insuranceOpt.isEmpty()) {
                return ResponseUtil.badRequest("Insurance not found with ID: " + id);
            }
            
            Insurance insurance = insuranceOpt.get();
            insurance.setActive(false);
            insuranceRepository.save(insurance);
            
            return ResponseUtil.success(null, "Insurance deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting insurance with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete insurance: " + e.getMessage());
        }
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get expiring insurance", description = "Retrieve insurance records expiring within specified days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expiring insurance retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<Page<InsuranceProjection>> getExpiringInsurance(
            @Parameter(description = "Days from now to check for expiration") @RequestParam(defaultValue = "30") Integer days,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.info("Retrieving expiring insurance within {} days - page: {}, size: {}", days, page, size);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, "dateValidity", "asc");
            Page<InsuranceProjection> expiringInsurance = 
                insuranceRepository.findExpiringWithinDaysProjections(days, pageable);
            
            return ResponseEntity.ok(expiringInsurance);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for expiring insurance retrieval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving expiring insurance", e);
            return ResponseEntity.badRequest().build();
        }
    }
}