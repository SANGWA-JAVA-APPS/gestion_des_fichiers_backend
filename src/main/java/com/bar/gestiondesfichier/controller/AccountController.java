package com.bar.gestiondesfichier.controller;

import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.document.repository.SectionCategoryRepository;
import com.bar.gestiondesfichier.dto.AccountDTO;
import com.bar.gestiondesfichier.dto.UserBlockPermissionProjection;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.entity.AccountCategory;
import com.bar.gestiondesfichier.location.model.LocationEntity;
import com.bar.gestiondesfichier.location.model.Permission;
import com.bar.gestiondesfichier.location.repository.LocationEntityRepository;
import com.bar.gestiondesfichier.location.repository.PermissionRepository;
import com.bar.gestiondesfichier.mapper.AccountMapper;
import com.bar.gestiondesfichier.repository.AccountRepository;
import com.bar.gestiondesfichier.repository.AccountCategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
@Tag(name = "Account Management", description = "Account CRUD operations with pagination")

public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountCategoryRepository accountCategoryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private SectionCategoryRepository sectionCategoryRepository;
    @Autowired
    private LocationEntityRepository locationEntityRepository;

    @GetMapping
    @Operation(summary = "Get all accounts", description = "Retrieve paginated list of active accounts with default 20 records per page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "403", description = "Session expired")
    })
    public ResponseEntity<Map<String, Object>> getAllAccounts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "fullName") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {
        try {
            log.info("Retrieving accounts - page: {}, size: {}, sort: {} {}", page, size, sort, direction);

            Pageable pageable = ResponseUtil.createPageable(page, size, sort, direction);
            Page<Account> accounts = accountRepository.findByActiveTrue(pageable);
            Page<AccountDTO> dtoPage = accounts.map(AccountMapper::toDTO);
            ;
            return ResponseUtil.successWithPagination(dtoPage);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for account retrieval: {}", e.getMessage());
            return ResponseUtil.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving accounts", e);
            return ResponseUtil.badRequest("Failed to retrieve accounts: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieve a specific account by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Account not found or invalid ID")
    })
    public ResponseEntity<Map<String, Object>> getAccountById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseUtil.badRequest("Invalid account ID");
            }

            log.info("Retrieving account by ID: {}", id);
            Optional<Account> account = accountRepository.findByIdAndActiveTrue(id);

            if (account.isPresent()) {
                AccountDTO dto = AccountMapper.toDTO(account.get());

                return ResponseUtil.success(dto, "Account retrieved successfully");
            } else {
                return ResponseUtil.badRequest("Account not found with ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error retrieving account with ID: {}", id, e);
            return ResponseUtil.badRequest("Failed to retrieve account: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create account", description = "Create a new account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountRequest accountRequest) {

        // 1. Username validation
        if (accountRepository.existsByUsername(accountRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + accountRequest.getUsername());
        }

        // 2. Load category
        AccountCategory category = accountCategoryRepository
                .findById(accountRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                "Invalid account category id: " + accountRequest.getCategoryId()
        ));

        // 3. Load location entity
        LocationEntity locationEntity = locationEntityRepository
                .findById(accountRequest.getLocationEntityId())
                .orElseThrow(() -> new IllegalArgumentException(
                "Invalid location entity id: " + accountRequest.getLocationEntityId()
        ));

        // 4. Build account
        Account account = new Account();
        account.setUsername(accountRequest.getUsername());
        account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
        account.setEmail(accountRequest.getEmail());
        account.setFullName(accountRequest.getFullName());
        account.setPhoneNumber(accountRequest.getPhoneNumber());
        account.setGender(accountRequest.getGender());
        account.setAccountCategory(category);
        account.setLocationEntity(locationEntity);
        account.setActive(true);

        // 5. Handle section categories (optional)
        if (accountRequest.getSectionCategoryIds() != null
                && !accountRequest.getSectionCategoryIds().isEmpty()) {

            List<Long> sectionIds = accountRequest.getSectionCategoryIds();

            List<Long> invalidIds = sectionIds.stream()
                    .filter(id -> !sectionCategoryRepository.existsById(id))
                    .toList();

            if (!invalidIds.isEmpty()) {
                throw new IllegalArgumentException("Invalid section category IDs: " + invalidIds);
            }

            Set<SectionCategory> sections = sectionIds.stream()
                    .map(id -> sectionCategoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException(
                    "SectionCategory disappeared during validation: " + id
            )))
                    .collect(Collectors.toSet());

            account.setSectionCategories(sections);
        }
// 5b. Handle permissions (optional)
        if (accountRequest.getPermissionIds() != null && !accountRequest.getPermissionIds().isEmpty()) {

            List<Long> permissionIds = accountRequest.getPermissionIds();

            // Validate all permissions exist
            List<Long> invalidPermissionIds = permissionIds.stream()
                    .filter(id -> !permissionRepository.existsById(id))
                    .toList();

            if (!invalidPermissionIds.isEmpty()) {
                throw new IllegalArgumentException("Invalid permission IDs: " + invalidPermissionIds);
            }

            // Load Permission objects
            Set<Permission> permissions = permissionIds.stream()
                    .map(id -> permissionRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException(
                    "Permission disappeared during validation: " + id
            )))
                    .collect(Collectors.toSet());

            account.setPermissions(permissions);
        }

        // 6. Save
        Account savedAccount = accountRepository.save(account);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AccountMapper.toDTO(savedAccount));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account", description = "Update an existing account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account updated successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountRequest accountRequest) {
        try {
            Optional<Account> existingAccount = accountRepository.findById(id);
            if (!existingAccount.isPresent() || !existingAccount.get().isActive()) {
                return ResponseEntity.notFound().build();
            }

            Account account = existingAccount.get();
            account.setUsername(accountRequest.getUsername());
            if (accountRequest.getPassword() != null && !accountRequest.getPassword().isEmpty()) {
                account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
            }
            account.setEmail(accountRequest.getEmail());
            account.setFullName(accountRequest.getFullName());
            account.setPhoneNumber(accountRequest.getPhoneNumber());
            account.setGender(accountRequest.getGender());

            if (accountRequest.getCategoryId() != null) {
                Optional<AccountCategory> category = accountCategoryRepository.findById(accountRequest.getCategoryId());
                if (category.isPresent()) {
                    account.setAccountCategory(category.get());
                }
            }

            // Update LocationEntity
            if (accountRequest.getLocationEntityId() != null) {
                locationEntityRepository.findById(accountRequest.getLocationEntityId())
                        .ifPresent(account::setLocationEntity);
            } else {
                account.setLocationEntity(null); // remove if not provided
            }

            Account savedAccount = accountRepository.save(account);
            return ResponseEntity.ok(AccountMapper.toDTO(savedAccount));

        } catch (Exception e) {
            log.error("Error updating account with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account", description = "Soft delete an account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            Optional<Account> account = accountRepository.findById(id);
            if (!account.isPresent() || !account.get().isActive()) {
                return ResponseEntity.notFound().build();
            }

            Account accountEntity = account.get();
            accountEntity.setActive(false);
            accountRepository.save(accountEntity);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting account with id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/sections")
    @Operation(summary = "Update user's section categories", description = "Replace all section categories for a user")
    public ResponseEntity<?> updateAccountSections(
            @PathVariable Long id,
            @RequestBody List<Long> sectionIds
    ) {
        try {
            Optional<Account> optionalAccount = accountRepository.findById(id);
            if (!optionalAccount.isPresent() || !optionalAccount.get().isActive()) {
                return ResponseEntity.notFound().build();
            }

            Account account = optionalAccount.get();

            // Validate all section IDs exist
            List<Long> invalidIds = sectionIds.stream()
                    .filter(sectionId -> !sectionCategoryRepository.existsById(sectionId))
                    .toList();

            if (!invalidIds.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("Invalid section IDs: " + invalidIds);
            }

            // Load SectionCategory objects
            Set<SectionCategory> sections = sectionIds.stream()
                    .map(sectionCategoryRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            // Replace all sections atomically
            account.setSectionCategories(sections);
            accountRepository.save(account);

            return ResponseEntity.ok("Section categories updated successfully");

        } catch (Exception e) {
            log.error("Error updating sections for account id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update sections");
        }
    }

    @GetMapping("/{id}/sections")
    @Operation(summary = "Get user's section categories", description = "List all section categories assigned to a specific user")
    public ResponseEntity<?> getAccountSections(@PathVariable Long id) {
        try {
            Optional<Account> optionalAccount = accountRepository.findById(id);
            if (!optionalAccount.isPresent() || !optionalAccount.get().isActive()) {
                return ResponseEntity.notFound().build();
            }

            Account account = optionalAccount.get();
            Set<SectionCategory> sections = account.getSectionCategories();

            // Return just the IDs or full objects depending on your DTO strategy
            List<Map<String, Object>> sectionList = sections.stream()
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", s.getId());
                        map.put("name", s.getName());
                        map.put("code", s.getCode());
                        return map;
                    })
                    .toList();

            return ResponseEntity.ok(sectionList);

        } catch (Exception e) {
            log.error("Error retrieving sections for account id: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve sections");
        }
    }

    @GetMapping("/{id}/permissions")
    @Operation(summary = "Get user's permissions", description = "List all permissions assigned to a specific user")
    public ResponseEntity<?> getAccountPermissions(@PathVariable Long id) {
        try {
            Optional<Account> optionalAccount = accountRepository.findById(id);
            if (!optionalAccount.isPresent() || !optionalAccount.get().isActive()) {
                return ResponseEntity.notFound().build();
            }

            Account account = optionalAccount.get();
            Set<Permission> permissions = account.getPermissions();

            // Return DTO-friendly response
            List<Map<String, Object>> permissionList = permissions.stream()
                    .map(p -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", p.getId());
                        map.put("name", p.getName());
                        map.put("code", p.getCode());
                        return map;
                    })
                    .toList();

            return ResponseEntity.ok(permissionList);

        } catch (Exception e) {
            log.error("Error retrieving permissions for account id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve permissions");
        }
    }

    @GetMapping("/permissions")
    @Operation(summary = "Get all permissions", description = "Retrieve all available permissions in the system")
    public ResponseEntity<?> getAllPermissions() {
        try {
            List<Permission> permissions = permissionRepository.findAll();

            // Map to DTO or simple response
            List<Map<String, Object>> permissionList = permissions.stream()
                    .map(p -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", p.getId());
                        map.put("name", p.getName());
                        map.put("code", p.getCode());
                        return map;
                    })
                    .toList();

            return ResponseEntity.ok(permissionList);

        } catch (Exception e) {
            log.error("Error retrieving all permissions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve permissions");
        }
    }

    @GetMapping("/{id}/blocks-permissions")
    public ResponseEntity<?> getUserBlocksWithPermissions(@PathVariable Long id) {
        List<UserBlockPermissionProjection> projectionList
                = accountRepository.findUserPermissionsByAccountId(id);

        // Group by Block
        Map<Long, Map<String, Object>> blocksMap = new HashMap<>();
        for (var p : projectionList) {
            blocksMap.computeIfAbsent(p.getBlockId(), k -> {
                Map<String, Object> map = new HashMap<>();
                map.put("blockId", p.getBlockId());
                map.put("blockName", p.getBlockName());
                map.put("blockCode", p.getBlockCode());
                map.put("permissions", new ArrayList<Map<String, Object>>());
                return map;
            });

            Map<String, Object> permissionMap = new HashMap<>();
            permissionMap.put("id", p.getPermissionId());
            permissionMap.put("name", p.getPermissionName());
            Object code = permissionMap.put("code", p.getPermissionCode());

            ((List<Map<String, Object>>) blocksMap.get(p.getBlockId()).get("permissions"))
                    .add(permissionMap);
        }

        return ResponseEntity.ok(blocksMap.values());
    }

    @PutMapping("/{id}/permissions")
    @Operation(summary = "Update user's permissions", description = "Replace all permissions for a user")
    public ResponseEntity<?> updateAccountPermissions(@PathVariable Long id, @RequestBody com.fasterxml.jackson.databind.JsonNode payload) {
        try {
            Long resolvedAccountId = id;
            List<Long> permissionIds = new ArrayList<>();

            if (payload != null && payload.isArray()) {
                for (var node : payload) {
                    if (node != null && node.canConvertToLong()) {
                        permissionIds.add(node.asLong());
                    }
                }
            } else if (payload != null && payload.isObject()) {
                if (payload.hasNonNull("accountId") && payload.get("accountId").canConvertToLong()) {
                    resolvedAccountId = payload.get("accountId").asLong();
                }
                if (payload.hasNonNull("permissionIds") && payload.get("permissionIds").isArray()) {
                    for (var node : payload.get("permissionIds")) {
                        if (node != null && node.canConvertToLong()) {
                            permissionIds.add(node.asLong());
                        }
                    }
                }
            }

            if (!resolvedAccountId.equals(id)) {
                return ResponseEntity.badRequest().body("Account ID mismatch between path and payload");
            }

            Optional<Account> optionalAccount = accountRepository.findById(resolvedAccountId);
            if (!optionalAccount.isPresent() || !optionalAccount.get().isActive()) {
                return ResponseEntity.notFound().build();
            }

            Account account = optionalAccount.get();

            // Validate all permission IDs exist
                List<Long> invalidIds = permissionIds.stream()
                    .filter(pid -> !permissionRepository.existsById(pid))
                    .toList();

            if (!invalidIds.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid permission IDs: " + invalidIds);
            }

            // Load Permission objects
            Set<Permission> permissions = permissionIds.stream()
                    .map(pid -> permissionRepository.findById(pid).orElseThrow())
                    .collect(Collectors.toSet());

            account.setPermissions(permissions);
            accountRepository.save(account);

            return ResponseEntity.ok("Permissions updated successfully");

        } catch (Exception e) {
            log.error("Error updating permissions for account id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update permissions");
        }
    }

    // Simple DTO for request
    @Setter
    @Getter
    public static class AccountRequest {

        // Getters and Setters
        private String username;
        private String password;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String gender;
        private Long categoryId;
        private Long locationEntityId;
        private List<Long> sectionCategoryIds;
        private List<Long> permissionIds;
    }

}
