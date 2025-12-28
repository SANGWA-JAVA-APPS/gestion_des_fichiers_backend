package com.bar.gestiondesfichier.common.util;

import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.document.repository.SectionCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

//@Service
public class SectionCategorySeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SectionCategorySeeder.class);
    private final SectionCategoryRepository sectionCategoryRepository;

    public SectionCategorySeeder(SectionCategoryRepository sectionCategoryRepository) {
        this.sectionCategoryRepository = sectionCategoryRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Starting SectionCategory seeding...");

        long existingCount = sectionCategoryRepository.count();
        if (existingCount >= 1000) {
            log.info("SectionCategory table already has {} records. Skipping seeding.", existingCount);
            return;
        }

        List<SectionCategory> categoriesToSave = new ArrayList<>();

        IntStream.rangeClosed(1, 1000).forEach(i -> {
            String name = "Category " + i;
            if (!sectionCategoryRepository.existsByNameAndActiveTrue(name)) { // avoid duplicates
                SectionCategory category = new SectionCategory();
                category.setName(name);
                category.setDescription("Description for category " + i);
                category.setActive(true);
                categoriesToSave.add(category);
            }
        });

        if (!categoriesToSave.isEmpty()) {
            sectionCategoryRepository.saveAll(categoriesToSave); // batch save
            log.info("✅ {} SectionCategory records seeded successfully", categoriesToSave.size());
        } else {
            log.info("No new SectionCategory records to seed.");
        }
    }
}
