package com.bar.gestiondesfichier.controller;

import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.entity.AccountCategory;
import com.bar.gestiondesfichier.repository.AccountCategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
@Tag(name = "Role Management", description = "Role CRUD operations with pagination (mapped to Account Categories)")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final AccountCategoryRepository accountCategoryRepository;

    public RoleController(AccountCategoryRepository accountCategoryRepository) {
        this.accountCategoryRepository = accountCategoryRepository;
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieve paginated list of roles (account categories) with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Map<String, Object>> getAllRoles(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {
        try {
            log.info("Retrieving roles - page: {}, size: {}, sort: {} {}", page, size, sort, direction);
            
            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<AccountCategory> categories = accountCategoryRepository.findAll(pageable);
            
            return ResponseUtil.successWithPagination(categories);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for role retrieval: {}", e.getMessage());
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving roles", e);
            return ResponseUtil.badRequest("Failed to retrieve roles: " + e.getMessage());
        }
    }

        @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieve a specific role by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Role not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid role ID");
            }
            
            log.info("Retrieving role by ID: {}", id);
            Optional<AccountCategory> category = accountCategoryRepository.findById(id);
            
            if (category.isPresent()) {
                return ResponseUtil.success(category.get(), "Role retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Role not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving role with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve role: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create role", description = "Create a new role (account category)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleRequest roleRequest) {
        try {
            // Check if category with this name already exists
            Optional<AccountCategory> existingCategory = accountCategoryRepository.findByName(roleRequest.getName());
            if (existingCategory.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            AccountCategory category = new AccountCategory(roleRequest.getName(), roleRequest.getDescription());
            AccountCategory savedCategory = accountCategoryRepository.save(category);
            RoleDTO role = convertToRoleDTO(savedCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(role);
        } catch (Exception e) {
            log.error("Error creating role", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role", description = "Update an existing role (account category)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleRequest roleRequest) {
        try {
            Optional<AccountCategory> categoryOpt = accountCategoryRepository.findById(id);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Check if another category with this name already exists (excluding current one)
            Optional<AccountCategory> existingCategory = accountCategoryRepository.findByName(roleRequest.getName());
            if (existingCategory.isPresent() && !existingCategory.get().getId().equals(id)) {
                return ResponseEntity.badRequest().build();
            }

            AccountCategory category = new AccountCategory(roleRequest.getName(), roleRequest.getDescription());
            category.setId(id);
            AccountCategory savedCategory = accountCategoryRepository.save(category);
            RoleDTO role = convertToRoleDTO(savedCategory);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            log.error("Error updating role with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Delete a role (account category)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete role with associated accounts"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        try {
            Optional<AccountCategory> categoryOpt = accountCategoryRepository.findById(id);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            accountCategoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting role with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method to convert AccountCategory to RoleDTO
    // Using basic field access to avoid Lombok compilation issues
    private RoleDTO convertToRoleDTO(AccountCategory category) {
        Long id = category.getId();
        String name = category.getName();
        String description = category.getDescription();
        
        // Handle potential Lombok compilation issues gracefully
        try {
            return new RoleDTO(id, name, description, 1, 0);
        } catch (Exception e) {
            // Fallback if getters are not available due to Lombok issues
            return new RoleDTO(id, name != null ? name : "Unknown", 
                             description != null ? description : "", 1, 0);
        }
    }

    // DTOs for Role Management
    public static class RoleDTO {
        private Long id;
        private String name;
        private String description;
        private Integer level;
        private Integer accountCount;

        public RoleDTO() {}

        public RoleDTO(Long id, String name, String description, Integer level, Integer accountCount) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.level = level;
            this.accountCount = accountCount;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getLevel() { return level; }
        public void setLevel(Integer level) { this.level = level; }

        public Integer getAccountCount() { return accountCount; }
        public void setAccountCount(Integer accountCount) { this.accountCount = accountCount; }
    }

    public static class RoleRequest {
        private String name;
        private String description;
        private Integer level;

        public RoleRequest() {}

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getLevel() { return level; }
        public void setLevel(Integer level) { this.level = level; }
    }
}