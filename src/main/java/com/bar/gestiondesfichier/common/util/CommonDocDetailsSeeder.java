package com.bar.gestiondesfichier.common.util;

import com.bar.gestiondesfichier.document.model.CommonDocDetails;
import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.document.repository.CommonDocDetailsRepository;
import com.bar.gestiondesfichier.document.repository.SectionCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class CommonDocDetailsSeeder implements CommandLineRunner {

    private final CommonDocDetailsRepository docRepository;
    private final SectionCategoryRepository sectionCategoryRepository;

    @Override
    public void run(String... args) {

        // Fetch all document categories
        List<SectionCategory> categories = sectionCategoryRepository.findAll();

        for (SectionCategory category : categories) {
            seedDocumentsForCategory(category, 5);
        }
    }

    private void seedDocumentsForCategory(SectionCategory category, int count) {

        for (int i = 1; i <= count; i++) {

            String reference = category.getCode() + "-DOC-" + i;

            if (docRepository.existsByReference(reference)) {
                continue;
            }

            CommonDocDetails doc = new CommonDocDetails();
            doc.setReference(reference);
            doc.setDescription(
                    "Sample document " + i + " for category " + category.getName()
            );
            doc.setStatus("ACTIVE");
            doc.setDateTime(LocalDateTime.now());
            doc.setVersion("1.0");
            doc.setExpirationDate(LocalDateTime.now().plusYears(1));
            doc.setSectionCategory(category);

            docRepository.save(doc);
        }
    }
}
