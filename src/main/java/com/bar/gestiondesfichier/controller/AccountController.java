package com.bar.gestiondesfichier.controller;

import com.bar.gestiondesfichier.common.util.ResponseUtil;
import com.bar.gestiondesfichier.dto.AccountDTO;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.entity.AccountCategory;
import com.bar.gestiondesfichier.location.model.LocationEntity;
import com.bar.gestiondesfichier.location.repository.LocationEntityRepository;
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

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
@Tag(name = "Account Management", description = "Account CRUD operations with pagination")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountRepository accountRepository;
    private final AccountCategoryRepository accountCategoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocationEntityRepository locationEntityRepository;
    public AccountController(AccountRepository accountRepository,
                             AccountCategoryRepository accountCategoryRepository,
                             PasswordEncoder passwordEncoder, LocationEntityRepository locationEntityRepository) {
        this.accountRepository = accountRepository;
        this.accountCategoryRepository = accountCategoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.locationEntityRepository = locationEntityRepository;
    }

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
        try {
            // 1. Username validation
            if (accountRepository.existsByUsername(accountRequest.getUsername())) {
                return ResponseEntity.badRequest().build();
            }

            // 2. Load category
            AccountCategory category = accountCategoryRepository
                    .findById(accountRequest.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category"));

            // 3. Load location entity (REQUIRED)
            LocationEntity locationEntity = locationEntityRepository
                    .findById(accountRequest.getLocationEntityId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid location entity"));

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

            // 5. Save
            Account savedAccount = accountRepository.save(account);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(AccountMapper.toDTO(savedAccount));


        } catch (Exception e) {
            log.error("Error creating account", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account", description = "Update an existing account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account updated successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody AccountUpdateRequest  accountRequest) {
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





    @Setter
    @Getter
    public static class AccountUpdateRequest {

        private String username;
        private String password; // optional
        private String email;
        private String fullName;
        private String phoneNumber;
        private String gender;
        private Long categoryId;
        private Long locationEntityId;
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
    }




}