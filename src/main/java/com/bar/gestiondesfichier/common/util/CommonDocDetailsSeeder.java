package com.bar.gestiondesfichier.common.util;

import com.bar.gestiondesfichier.document.model.CommonDocDetails;
import com.bar.gestiondesfichier.document.repository.CommonDocDetailsRepository;
import com.bar.gestiondesfichier.location.model.Section;
import com.bar.gestiondesfichier.location.repository.SectionRepository;
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
    private final SectionRepository sectionRepository;

    @Override
    public void run(String... args) {

        // Fetch all sections from the database
        List<Section> sections = sectionRepository.findAll();

        for (Section section : sections) {
            seedDocumentsForSection(section, 5); // 5 documents per section
        }
    }

    private void seedDocumentsForSection(Section section, int count) {

        for (int i = 1; i <= count; i++) {

            String reference = section.getSectionCode() + "-DOC-" + i;

            // Skip if a document with this reference already exists
            if (docRepository.existsByReference(reference)) {
                continue;
            }

            CommonDocDetails doc = new CommonDocDetails();
            doc.setReference(reference);
            doc.setDescription("Sample document " + i + " for section " + section.getName());
            doc.setStatus("ACTIVE");
            doc.setDateTime(LocalDateTime.now());
            doc.setVersion("1.0");
            doc.setExpirationDate(LocalDateTime.now().plusYears(1));
            doc.setSection(section); // link to actual section

            docRepository.save(doc);
        }
    }
}
