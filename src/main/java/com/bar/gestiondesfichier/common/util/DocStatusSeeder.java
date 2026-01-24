package com.bar.gestiondesfichier.common.util;

import com.bar.gestiondesfichier.document.model.DocStatus;
import com.bar.gestiondesfichier.document.repository.DocStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Seeds default document status values into the database.
 * Runs on application startup and ensures no duplicates are created.
 */
@Component
@Order(2) // Run after DataSeeder
public class DocStatusSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DocStatusSeeder.class);
    private final DocStatusRepository docStatusRepository;

    public DocStatusSeeder(DocStatusRepository docStatusRepository) {
        this.docStatusRepository = docStatusRepository;
    }

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("Starting DocStatus Seeder...");
        log.info("========================================");

        seedDefaultStatuses();

        log.info("========================================");
        log.info("DocStatus Seeder Completed Successfully!");
        log.info("========================================");
    }

    /**
     * Seeds default document status values in English
     */
    private void seedDefaultStatuses() {
        // Define default statuses with descriptions (no duplicates)
        Map<String, String> defaultStatuses = new LinkedHashMap<>();
        defaultStatuses.put("In Progress", "Document is currently being processed");
        defaultStatuses.put("Validated", "Document has been validated and approved");
        defaultStatuses.put("Rejected", "Document has been rejected");
        defaultStatuses.put("Canceled", "Document has been canceled");
        defaultStatuses.put("Acquired", "Asset or document has been acquired");
        defaultStatuses.put("Sold", "Asset or document has been sold");
        defaultStatuses.put("Transferred", "Document or asset has been transferred");
        defaultStatuses.put("Litigious", "Document is under litigation or dispute");
        defaultStatuses.put("Applicable", "Document is currently applicable");
        defaultStatuses.put("Suspended", "Document status is suspended");
        defaultStatuses.put("Replaced", "Document has been replaced by a newer version");
        defaultStatuses.put("Owner", "Ownership status");
        defaultStatuses.put("Rental", "Rental or lease status");
        defaultStatuses.put("Free", "Free or available status");
        defaultStatuses.put("Expired", "Document has expired");

        int createdCount = 0;
        int existingCount = 0;

        for (Map.Entry<String, String> entry : defaultStatuses.entrySet()) {
            String statusName = entry.getKey();
            String description = entry.getValue();

            try {
                // Check if status already exists (case-insensitive)
                Optional<DocStatus> existing = docStatusRepository.findByNameIgnoreCase(statusName);

                if (existing.isPresent()) {
                    existingCount++;
                    log.debug("✓ Status '{}' already exists (ID: {})", statusName, existing.get().getId());
                } else {
                    // Create new status
                    DocStatus status = new DocStatus();
                    status.setName(statusName);
                    status.setDescription(description);
                    status.setActive(true);

                    DocStatus saved = docStatusRepository.save(status);
                    createdCount++;
                    log.info("✓ Created status: '{}' (ID: {})", statusName, saved.getId());
                }
            } catch (Exception e) {
                log.error("✗ Error creating status '{}': {}", statusName, e.getMessage(), e);
            }
        }

        log.info("========================================");
        log.info("DocStatus Summary:");
        log.info("  - Created: {} new statuses", createdCount);
        log.info("  - Existing: {} statuses", existingCount);
        log.info("  - Total: {} statuses", createdCount + existingCount);
        log.info("========================================");
    }
}
