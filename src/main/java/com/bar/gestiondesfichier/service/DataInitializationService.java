package com.bar.gestiondesfichier.service;

import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.entity.AccountCategory;
import com.bar.gestiondesfichier.location.model.Country;
import com.bar.gestiondesfichier.location.model.LocationEntity;
import com.bar.gestiondesfichier.location.model.Permission;
import com.bar.gestiondesfichier.location.repository.CountryRepository;
import com.bar.gestiondesfichier.location.repository.LocationEntityRepository;
import com.bar.gestiondesfichier.location.repository.PermissionRepository;
import com.bar.gestiondesfichier.repository.AccountCategoryRepository;
import com.bar.gestiondesfichier.repository.AccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Order(10) // Run after BlockSeeder and other initial seeders
public class DataInitializationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializationService.class);
    private final AccountCategoryRepository accountCategoryRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocationEntityRepository locationEntityRepository;
    private final CountryRepository countryRepository;
    private final PermissionRepository permissionRepository;

    // Explicit constructor for dependency injection
    public DataInitializationService(AccountCategoryRepository accountCategoryRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            LocationEntityRepository locationEntityRepository,
            CountryRepository countryRepository,
            PermissionRepository permissionRepository) {
        this.accountCategoryRepository = accountCategoryRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.locationEntityRepository = locationEntityRepository;
        this.countryRepository = countryRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("========================================");
        log.info("Starting Data Initialization Service...");
        log.info("========================================");

        // Create Account Categories
        AccountCategory adminCategory = createCategoryIfNotExists("ADMIN", "System Administrator Category");
        AccountCategory userCategory = createCategoryIfNotExists("USER", "Regular User Category");

        // Create Default Accounts
        createAccountIfNotExists("mamadou", "admin123", "mamadou@gestiondesfichier.com",
                "Mamadou FALL", "+1234567890", adminCategory);

        createAccountIfNotExists("user", "user123", "user@gestiondesfichier.com", "Regular User", "+1234567892", userCategory);

        log.info("Default users created:");
        log.info("- mamadou/admin123 (ADMIN)");
        log.info("- user/user123 (USER)");

        // Seed MAGERWA company and link admin
        seedDefaultCompany();

        log.info("========================================");
        log.info("Data Initialization Completed Successfully!");
        log.info("========================================");
    }

    private AccountCategory createCategoryIfNotExists(String name, String description) {
        return accountCategoryRepository.findByName(name)
                .orElseGet(() -> {
                    AccountCategory category = new AccountCategory(name, description);
                    AccountCategory saved = accountCategoryRepository.save(category);
                    log.info("Created account category: {}", name);
                    return saved;
                });
    }

    private Account createAccountIfNotExists(String username, String password, String email, String fullName, String phoneNumber, AccountCategory category) {
        return accountRepository.findByUsername(username)
                .orElseGet(() -> {
                    // Encode password using BCrypt
                    String encodedPassword = passwordEncoder.encode(password);
                    Account account = new Account(username, encodedPassword, email, fullName, phoneNumber, "not specified", category);
                    Account saved = accountRepository.saveAndFlush(account);
                    log.info("Created account: {} ({}) with encoded password", username, category.getName());
                    return saved;
                });
    }

    /**
     * Seeds the default MAGERWA company in the location_entities table
     */
    @Transactional
    private void seedDefaultCompany() {
        try {
            // Check if MAGERWA already exists
            Optional<LocationEntity> existingMagerwa = locationEntityRepository
                    .findByNameIgnoreCaseAndActiveTrue("MAGERWA");

            if (existingMagerwa.isPresent()) {
                log.info("✓ MAGERWA company already exists in database (ID: {})",
                        existingMagerwa.get().getId());
                linkAdminToMagerwaAndAssignPermissions();
                return;
            }

            // Find Rwanda country (or use first available country as fallback)
            Optional<Country> rwanda = countryRepository.findByNameIgnoreCaseAndActiveTrue("Rwanda");
            Country country;

            if (rwanda.isPresent()) {
                country = rwanda.get();
                log.info("Found Rwanda country (ID: {}) for MAGERWA", country.getId());
            } else {
                // Fallback: get first available country
                Optional<Country> firstCountry = countryRepository.findAll().stream()
                        .filter(Country::isActive)
                        .findFirst();

                if (!firstCountry.isPresent()) {
                    log.warn("✗ No active countries found in database. Cannot create MAGERWA company.");
                    log.warn("  Please ensure countries are seeded first.");
                    return;
                }

                country = firstCountry.get();
                log.warn("Rwanda not found. Using {} (ID: {}) for MAGERWA",
                        country.getName(), country.getId());
            }

            // Create MAGERWA location entity
            LocationEntity magerwa = new LocationEntity();
            magerwa.setName("MAGERWA");
            magerwa.setDescription("Magazine General Rwanda - National Warehousing and Logistics Company");
            magerwa.setCode("MGR-001");
            magerwa.setPostalCode("KG 11 Ave");
            magerwa.setEntityType(LocationEntity.EntityType.CITY);
            magerwa.setCountry(country);
            magerwa.setActive(true);

            // Save to database
            LocationEntity savedMagerwa = locationEntityRepository.save(magerwa);

            log.info("✓ Successfully created MAGERWA company:");
            log.info("  - ID: {}", savedMagerwa.getId());
            log.info("  - Name: {}", savedMagerwa.getName());
            log.info("  - Code: {}", savedMagerwa.getCode());
            log.info("  - Type: {}", savedMagerwa.getEntityType());
            log.info("  - Country: {}", savedMagerwa.getCountry().getName());
            log.info("  - Description: {}", savedMagerwa.getDescription());

            linkAdminToMagerwaAndAssignPermissions();
        } catch (Exception e) {
            log.error("✗ Error seeding MAGERWA company: {}", e.getMessage(), e);
        }
    }

    /**
     * Links the default admin user to MAGERWA company and assigns all
     * permissions
     *
     * @Transactional ensures: 1. All database operations are executed within a
     * single transaction 2. Many-to-many relationships (account_permissions)
     * are properly persisted to the join table 3. Lazy-loaded collections
     * (permissions, locationEntity) can be accessed and modified 4. Changes are
     * committed atomically - all succeed or all fail together 5. Without it,
     * the join table updates would not be saved to the database
     */
    @Transactional
    private void linkAdminToMagerwaAndAssignPermissions() {
        try {
            log.info("Starting admin account linking process...");
            // Find admin account by username using native query
            Account admin = null;
            try {
                admin = accountRepository.findByUsernameNative("mamadou").orElse(null);
            } catch (Exception e) {
                log.error("Error finding admin by username: {}", e.getMessage());
            }

            if (admin == null) {
                log.error("✗ Admin account with username 'mamadou' not found in database.");
                log.error("  Please ensure the admin account is created before running this seeder.");
                return;
            }

            log.info("✓ Admin account found: {} (ID: {})", admin.getUsername(), admin.getId());

            // Find MAGERWA company
            Optional<LocationEntity> magerwaOpt = locationEntityRepository
                    .findByNameIgnoreCaseAndActiveTrue("MAGERWA");
            if (!magerwaOpt.isPresent()) {
                log.warn("✗ MAGERWA company not found. Cannot link admin.");
                return;
            }

            LocationEntity magerwa = magerwaOpt.get();

            // Check if admin is already linked to MAGERWA
            boolean needsUpdate = false;

            // Use reflection-safe approach or direct setter
            if (admin.getLocationEntity() == null
                    || !magerwa.getId().equals(admin.getLocationEntity().getId())) {
                admin.setLocationEntity(magerwa);
                needsUpdate = true;
                log.info("✓ Linking admin to MAGERWA company (ID: {})", magerwa.getId());
            } else {
                log.info("✓ Admin already linked to MAGERWA company");
            }

            // Get all permissions
            List<Permission> allPermissions = permissionRepository.findAll();
            log.info("Found {} permissions in database", allPermissions.size());

            // Get current permissions count
            int existingPermissions = admin.getPermissions().size();
            log.info("Admin currently has {} permissions", existingPermissions);

            if (existingPermissions < allPermissions.size()) {
                // Clear existing and add all permissions
                admin.getPermissions().clear();
                admin.getPermissions().addAll(allPermissions);
                needsUpdate = true;
                log.info("✓ Assigning all {} permissions to admin (had {} before)",
                        allPermissions.size(), existingPermissions);
            } else {
                log.info("✓ Admin already has all {} permissions", existingPermissions);
            }

            // Save admin if updated
            if (needsUpdate) {
                Account savedAdmin = accountRepository.save(admin);
                log.info("✓ Admin account updated successfully:");
                log.info("  - Username: {}", savedAdmin.getUsername());
                log.info("  - Permissions: {}", savedAdmin.getPermissions().size());

                // Reload with location details to log them
                try {
                    Account reloaded = accountRepository.findById(savedAdmin.getId()).orElse(null);
                    if (reloaded != null && reloaded.getLocationEntity() != null) {
                        log.info("  - Company: {}", reloaded.getLocationEntity().getName());
                        if (reloaded.getLocationEntity().getCountry() != null) {
                            log.info("  - Country: {}", reloaded.getLocationEntity().getCountry().getName());
                        }
                    }
                } catch (Exception ex) {
                    log.debug("Could not load location details for logging: {}", ex.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("✗ Error linking admin to MAGERWA: {}", e.getMessage(), e);
        }
    }
}
