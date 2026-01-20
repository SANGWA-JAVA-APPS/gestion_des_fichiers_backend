package com.bar.gestiondesfichier.common.util;

import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.document.repository.SectionCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class SectionCategorySeeder implements CommandLineRunner {

    private final SectionCategoryRepository sectionCategoryRepository;

    @Override
    public void run(String... args) {

        seedIfNotExists(
                "ORG_FIN",
                "Financial",
                "Handles financial records, budgets, accounting, and financial reports."
        );

        seedIfNotExists(
                "ORG_PROC",
                "Procurement",
                "Manages purchasing processes, supplier contracts, and procurement documentation."
        );

        seedIfNotExists(
                "ORG_HR",
                "HR",
                "Responsible for human resources records, staff management, and personnel documentation."
        );

        seedIfNotExists(
                "ORG_TECH",
                "Technical",
                "Covers technical operations, engineering documents, and system technical files."
        );

        seedIfNotExists(
                "ORG_IT",
                "IT",
                "Manages information technology systems, infrastructure, and IT documentation."
        );

        seedIfNotExists(
                "ORG_RE",
                "Real Estate",
                "Handles property, facilities, leases, and real estate related records."
        );

        seedIfNotExists(
                "ORG_SH",
                "Shareholders",
                "Contains shareholder information, ownership records, and strategic governance documents."
        );

        seedIfNotExists(
                "ORG_LEGAL",
                "Legal",
                "Stores legal documents, contracts, compliance records, and regulatory files."
        );

        seedIfNotExists(
                "ORG_QUAL",
                "Quality",
                "Manages quality assurance processes, audits, and compliance documentation."
        );

        seedIfNotExists(
                "ORG_HSE",
                "HSE",
                "Handles health, safety, and environmental policies, incidents, and compliance records."
        );

        seedIfNotExists(
                "ORG_EQUIP",
                "Equipment",
                "Manages equipment inventories, maintenance records, and technical specifications."
        );

        seedIfNotExists(
                "ORG_DA",
                "Drug & Alcohol",
                "Stores drug and alcohol policy documents, testing records, and compliance reports."
        );

        seedIfNotExists(
                "ORG_INC",
                "Incident / News",
                "Records incidents, internal communications, notices, and organizational announcements."
        );

        seedIfNotExists(
                "ORG_SOP",
                "SOP",
                "Contains standard operating procedures, internal guidelines, and process documentation."
        );

        seedIfNotExists(
                "ORG_SUPP",
                "Suppliers Contracts",
                "Contains all supplier contracts, agreements, and procurement arrangements."
        );

        seedIfNotExists(
                "ORG_RENT_CON",
                "Rental Assets Contracts",
                "Holds rental contracts for assets and properties."
        );

        seedIfNotExists(
                "ORG_CLIENT",
                "Client Commercial",
                "Manages commercial client agreements, contracts, and records."
        );

        seedIfNotExists(
                "ORG_RENT_ASSET",
                "Rental Assets",
                "Stores rental asset inventories, agreements, and related documentation."
        );
    }

    private void seedIfNotExists(
            String code,
            String name,
            String description
    ) {
        if (sectionCategoryRepository.findByCode(code).isPresent()) {
            return;
        }

        SectionCategory category = new SectionCategory();
        category.setCode(code);
        category.setName(name);
        category.setDescription(description);
        sectionCategoryRepository.save(category);
    }
}
