package com.bar.gestiondesfichier.location.controller;

import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.location.dto.ModuleRequestDTO;
import com.bar.gestiondesfichier.location.dto.ModuleResponseDTO;
import com.bar.gestiondesfichier.location.model.Module;
import com.bar.gestiondesfichier.location.model.LocationEntity;
import com.bar.gestiondesfichier.location.repository.ModuleRepository;
import com.bar.gestiondesfichier.location.repository.LocationEntityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/location/modules")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8104", "https://ingenzi.codeguru-pro.com"})
@Tag(name = "Module Management", description = "Module CRUD operations with pagination")
public class ModuleController {

    private static final Logger log = LoggerFactory.getLogger(ModuleController.class);
    private final ModuleRepository moduleRepository;
    private final LocationEntityRepository locationEntityRepository;

    public ModuleController(ModuleRepository moduleRepository, LocationEntityRepository locationEntityRepository) {
        this.moduleRepository = moduleRepository;
        this.locationEntityRepository = locationEntityRepository;
    }

    @GetMapping
    @Operation(summary = "Get all modules", description = "Retrieve paginated list of active modules with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Modules retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Map<String, Object>> getAllModules(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {
        try {
            log.info("Retrieving modules - page: {}, size: {}, sort: {} {}", page, size, sort, direction);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<ModuleResponseDTO> response =
                    moduleRepository.findByActiveTrue(pageable)
                            .map(this::toResponseDTO);
            
            return ResponseUtil.successWithPagination(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for module retrieval: {}", e.getMessage());
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving modules", e);
            return ResponseUtil.badRequest("Failed to retrieve modules: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get module by ID", description = "Retrieve a specific module by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Module retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Module not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getModuleById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid module ID");
            }
            
            log.info("Retrieving module by ID: {}", id);
            Optional<Module> module = moduleRepository.findByIdAndActiveTrue(id);
            
            if (module.isPresent()) {
                return ResponseUtil.success(module.get(), "Module retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Module not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving module with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve module: " + e.getMessage());
        }
    }

    @GetMapping("/entity/{entityId}")
    @Operation(summary = "Get modules by location entity", description = "Retrieve modules for a specific location entity")
    public ResponseEntity<Map<String, Object>> getModulesByLocationEntity(
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            if (entityId == null || entityId <= 0) {
                return ResponseUtil.badRequest("Invalid location entity ID");
            }
            
            log.info("Retrieving modules by location entity: {}", entityId);
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<Module> modules = moduleRepository.findByLocationEntityIdAndActiveTrue(entityId, pageable);
            
            return ResponseUtil.successWithPagination(modules);
        } catch (Exception e) {
            log.error("Error retrieving modules for location entity: {}", entityId, e);
            return ResponseUtil.badRequest("Failed to retrieve modules: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create module", description = "Create a new module")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Map<String, Object>> createModule(@RequestBody ModuleRequestDTO dto) {
        try {
            log.info("Creating module: {}", dto.getName());

            // 1️⃣ Validate input
            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                return ResponseUtil.badRequest("Module name is required");
            }

            if (dto.getLocationEntityId() == null || dto.getLocationEntityId() <= 0) {
                return ResponseUtil.badRequest("Valid location entity ID is required");
            }

            // 2️⃣ Fetch LocationEntity
            Optional<LocationEntity> locationEntityOpt =
                    locationEntityRepository.findByIdAndActiveTrue(dto.getLocationEntityId());

            if (locationEntityOpt.isEmpty()) {
                return ResponseUtil.badRequest(
                        "Location entity not found with ID: " + dto.getLocationEntityId()
                );
            }

            LocationEntity locationEntity = locationEntityOpt.get();

            // 3️⃣ Map DTO → Entity using setters
            Module module = new Module();
            module.setName(dto.getName().trim());
            module.setDescription(dto.getDescription());
            module.setModuleCode(dto.getModuleCode());
            module.setCoordinates(dto.getCoordinates());
            module.setAreaSize(dto.getAreaSize());
            module.setAreaUnit(dto.getAreaUnit());
            module.setLocationEntity(locationEntity);
            module.setActive(true);

            // Enum-safe conversion
            if (dto.getModuleType() != null && !dto.getModuleType().isBlank()) {
                try {
                    module.setModuleType(
                            Module.ModuleType.valueOf(dto.getModuleType().toUpperCase())
                    );
                } catch (IllegalArgumentException ex) {
                    return ResponseUtil.badRequest(
                            "Invalid moduleType: " + dto.getModuleType()
                    );
                }
            }

            // 4️⃣ Save
            Module savedModule = moduleRepository.save(module);

            return ResponseUtil.success(savedModule, "Module created successfully");

        } catch (Exception e) {
            log.error("Error creating module", e);
            return ResponseUtil.badRequest("Failed to create module: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update module", description = "Update an existing module")
    public ResponseEntity<Map<String, Object>> updateModule(
            @PathVariable Long id,
            @RequestBody ModuleRequestDTO dto
    ) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid module ID");
            }

            if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                return ResponseUtil.badRequest("Module name is required");
            }

            Optional<Module> existingModuleOpt = moduleRepository.findByIdAndActiveTrue(id);
            if (existingModuleOpt.isEmpty()) {
                return ResponseUtil.badRequest("Module not found with ID: " + id);
            }

            Module module = existingModuleOpt.get();

            // Update fields
            module.setName(dto.getName().trim());
            module.setDescription(dto.getDescription());
            module.setModuleCode(dto.getModuleCode());
            module.setCoordinates(dto.getCoordinates());
            module.setAreaSize(dto.getAreaSize());
            module.setAreaUnit(dto.getAreaUnit());

            // LocationEntity
            if (dto.getLocationEntityId() != null && dto.getLocationEntityId() > 0) {
                Optional<LocationEntity> locationEntityOpt =
                        locationEntityRepository.findByIdAndActiveTrue(dto.getLocationEntityId());
                if (locationEntityOpt.isEmpty()) {
                    return ResponseUtil.badRequest(
                            "Location entity not found with ID: " + dto.getLocationEntityId()
                    );
                }
                module.setLocationEntity(locationEntityOpt.get());
            }

            // Module type enum
            if (dto.getModuleType() != null && !dto.getModuleType().isBlank()) {
                try {
                    module.setModuleType(
                            Module.ModuleType.valueOf(dto.getModuleType().toUpperCase())
                    );
                } catch (IllegalArgumentException ex) {
                    return ResponseUtil.badRequest("Invalid moduleType: " + dto.getModuleType());
                }
            }

            Module savedModule = moduleRepository.save(module);
            return ResponseUtil.success(savedModule, "Module updated successfully");

        } catch (Exception e) {
            log.error("Error updating module with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to update module: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete module", description = "Soft delete a module")
    public ResponseEntity<Map<String, Object>> deleteModule(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid module ID");
            }
            
            log.info("Deleting module with ID: {}", id);
            
            Optional<Module> module = moduleRepository.findByIdAndActiveTrue(id);
            if (!module.isPresent()) {
                return ResponseUtil.badRequest("Module not found with ID: " + id);
            }

            Module moduleEntity = module.get();
            moduleEntity.setActive(false);
            moduleRepository.save(moduleEntity);
            
            return ResponseUtil.success(null, "Module deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting module with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to delete module: " + e.getMessage());
        }
    }

    // Simple DTO for simplified frontend requirements
    public static class ModuleRequest {
        private String name;
        private String description;
        private Long entityId;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }
    }




    private ModuleResponseDTO toResponseDTO(Module module) {
        LocationEntity entity = module.getLocationEntity();

        return new ModuleResponseDTO(
                module.getId(),
                module.getName(),
                module.getDescription(),
                module.getModuleCode(),
                module.getModuleType() != null ? module.getModuleType().name() : null,
                module.getCoordinates(),
                module.getAreaSize(),
                module.getAreaUnit(),
                entity.getId(),
                entity.getName()
        );
    }

}


