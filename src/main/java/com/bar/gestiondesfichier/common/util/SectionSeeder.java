package com.bar.gestiondesfichier.common.util;

import com.bar.gestiondesfichier.location.model.Section;
import com.bar.gestiondesfichier.location.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class SectionSeeder implements CommandLineRunner {

    private final SectionRepository sectionRepository;

    @Override
    public void run(String... args) {

        seedIfNotExists(
                "Financial",
                "ORG_FIN",
                "Handles financial records, budgets, accounting, and financial reports.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.CONFIDENTIAL
        );

        seedIfNotExists(
                "Procurement",
                "ORG_PROC",
                "Manages purchasing processes, supplier contracts, and procurement documentation.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.CONFIDENTIAL
        );

        seedIfNotExists(
                "HR",
                "ORG_HR",
                "Responsible for human resources records, staff management, and personnel documentation.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.CONFIDENTIAL
        );

        seedIfNotExists(
                "Technical",
                "ORG_TECH",
                "Covers technical operations, engineering documents, and system technical files.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.RESTRICTED
        );

        seedIfNotExists(
                "IT",
                "ORG_IT",
                "Manages information technology systems, infrastructure, and IT documentation.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.RESTRICTED
        );

        seedIfNotExists(
                "Real Estate",
                "ORG_RE",
                "Handles property, facilities, leases, and real estate related records.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.RESTRICTED
        );

        seedIfNotExists(
                "Shareholders",
                "ORG_SH",
                "Contains shareholder information, ownership records, and strategic governance documents.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.TOP_SECRET
        );

        seedIfNotExists(
                "Legal",
                "ORG_LEGAL",
                "Stores legal documents, contracts, compliance records, and regulatory files.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.CONFIDENTIAL
        );

        seedIfNotExists(
                "Quality",
                "ORG_QUAL",
                "Manages quality assurance processes, audits, and compliance documentation.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.RESTRICTED
        );

        seedIfNotExists(
                "HSE",
                "ORG_HSE",
                "Handles health, safety, and environmental policies, incidents, and compliance records.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.RESTRICTED
        );

        seedIfNotExists(
                "Equipment",
                "ORG_EQUIP",
                "Manages equipment inventories, maintenance records, and technical specifications.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.RESTRICTED
        );

        seedIfNotExists(
                "Drug & Alcohol",
                "ORG_DA",
                "Stores drug and alcohol policy documents, testing records, and compliance reports.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.CONFIDENTIAL
        );

        seedIfNotExists(
                "Incident / News",
                "ORG_INC",
                "Records incidents, internal communications, notices, and organizational announcements.",
                Section.SectionType.OFFICE,
                Section.AccessLevel.RESTRICTED
        );

        seedIfNotExists(
                "SOP",
                "ORG_SOP",
                "Contains standard operating procedures, internal guidelines, and process documentation.",
                Section.SectionType.ARCHIVE,
                Section.AccessLevel.CONFIDENTIAL
        );
    }

    private void seedIfNotExists(
            String name,
            String sectionCode,
            String description,
            Section.SectionType sectionType,
            Section.AccessLevel accessLevel
    ) {

        if (sectionRepository.existsBySectionCode(sectionCode)) {
            return;
        }

        Section section = new Section();
        section.setName(name);                     // from NamedEntity
        section.setDescription(description);       // from NamedEntity
        section.setSectionCode(sectionCode);
        section.setSectionType(sectionType);
        section.setAccessLevel(accessLevel);
        section.setModule(null);                   // explicitly global

        sectionRepository.save(section);
    }
}
