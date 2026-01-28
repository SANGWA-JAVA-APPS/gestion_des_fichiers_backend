package com.bar.gestiondesfichier.common.util;

import com.bar.gestiondesfichier.location.model.Block;
import com.bar.gestiondesfichier.location.model.Permission;
import com.bar.gestiondesfichier.location.repository.BlockRepository;
import com.bar.gestiondesfichier.location.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional
@Order(5) // Run after CountrySeeder(1), DocStatusSeeder(2), but before DataInitializationService(10)
public class BlockSeeder implements CommandLineRunner {

    private final BlockRepository blockRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {
                Map<String, Block> blocks = seedDocumentBlocks();
                seedDocumentPermissions(blocks);
    }

    /**
     * Seed the default blocks list for documents.
     */
    private Map<String, Block> seedDocumentBlocks() {
        Map<String, Block> blocks = new LinkedHashMap<>();

        blocks.put("NORME_LOI", getOrCreateBlock(
                "NORME_LOI",
                "Norms, Laws and Regulation",
                "Norms, laws and regulations documentation"
        ));

        blocks.put("ASSET_LAND", getOrCreateBlock(
                "ASSET_LAND",
                "Asset Land",
                "Land asset management and documentation"
        ));

        blocks.put("ASSET_ESTATE", getOrCreateBlock(
                "ASSET_ESTATE",
                "Asset Estate",
                "Estate asset management and documentation"
        ));

        blocks.put("ASSET_EQUIPMENT", getOrCreateBlock(
                "ASSET_EQUIPMENT",
                "Asset Equipment",
                "Equipment asset management and documentation"
        ));

        blocks.put("CERT_LICENSES", getOrCreateBlock(
                "CERT_LICENSES",
                "Certificates et Licences",
                "Organizational certificates and licenses"
        ));

        blocks.put("COMPANY_POLICIES", getOrCreateBlock(
                "COMPANY_POLICIES",
                "Companies Policies (Similar)",
                "Company policies and procedures"
        ));

        blocks.put("HSE_POLICIES", getOrCreateBlock(
                "HSE_POLICIES",
                "HSE Policies",
                "Health, safety and environment policies"
        ));

        blocks.put("ETHICS_COMPLIANCE", getOrCreateBlock(
                "ETHICS_COMPLIANCE",
                "Ethics & Compliance",
                "Ethics and compliance documentation"
        ));

        blocks.put("THIRD_PARTY_CONTRACTS", getOrCreateBlock(
                "THIRD_PARTY_CONTRACTS",
                "Third Party Contracts",
                "Third party contracts and agreements"
        ));

        blocks.put("RISKS", getOrCreateBlock(
                "RISKS",
                "Risks",
                "Risk management documentation"
        ));

        return blocks;
    }

    /**
     * Seed permissions matching the Document menu group items in the frontend.
     * For now, all permissions are assigned to the provided block.
     */
    private void seedDocumentPermissions(Map<String, Block> blocks) {
        Block defaultBlock = blocks.values().stream().findFirst().orElse(null);
        if (defaultBlock == null) {
            return;
        }

        // 1. Section Category
        createPermissionIfNotExists(defaultBlock, "SECTION_CATEGORY",
                "Section Category",
                "Manage section categories for document organization");

        // 2. Norms and Laws
        createPermissionIfNotExists(resolveBlock(blocks, "NORME_LOI", defaultBlock), "NORME_LOI",
                "Norms and Laws",
                "Access to norms, laws and regulations documentation");

        // 3. Asset Land
        createPermissionIfNotExists(resolveBlock(blocks, "ASSET_LAND", defaultBlock), "COMM_ASSET_LAND",
                "Asset Land",
                "Access to land asset management and documentation");

        // 4. Construction Permits
        createPermissionIfNotExists(resolveBlock(blocks, "ASSET_LAND", defaultBlock), "PERMI_CONSTRUCTION",
                "Construction Permits",
                "Manage construction permits and related documents");

        // 5. Concession Agreement
        createPermissionIfNotExists(resolveBlock(blocks, "ASSET_LAND", defaultBlock), "ACCORD_CONCESSION",
                "Concession Agreement",
                "Access to concession agreements and contracts");

        // 6. Estate
        createPermissionIfNotExists(resolveBlock(blocks, "ASSET_ESTATE", defaultBlock), "ESTATE",
                "Estate",
                "Manage estate properties and related documentation");

        // 7. Certificates & Licenses
        createPermissionIfNotExists(resolveBlock(blocks, "CERT_LICENSES", defaultBlock), "CERT_LICENSES",
                "Certificates & Licenses",
                "Access to organizational certificates and licenses");

        // 8. Cargo Damage
        createPermissionIfNotExists(resolveBlock(blocks, "THIRD_PARTY_CONTRACTS", defaultBlock), "CARGO_DAMAGE",
                "Cargo Damage",
                "Manage cargo damage surveys and agreements");

        // 9. Financial Policies (ORG_FIN)
        createPermissionIfNotExists(resolveBlock(blocks, "COMPANY_POLICIES", defaultBlock), "DOC_FINANCIAL",
                "Financial Policies",
                "Access to financial policies and procedures");

        // 10. Procurement Policies (ORG_PROC)
        createPermissionIfNotExists(resolveBlock(blocks, "COMPANY_POLICIES", defaultBlock), "DOC_PROCUREMENT",
                "Procurement Policies",
                "Access to procurement policies and procedures");

        // 11. HR Policies (ORG_HR)
        createPermissionIfNotExists(resolveBlock(blocks, "COMPANY_POLICIES", defaultBlock), "DOC_HR",
                "HR Policies",
                "Access to human resources policies and procedures");

        // 12. Technical Policies (ORG_TECH)
        createPermissionIfNotExists(resolveBlock(blocks, "COMPANY_POLICIES", defaultBlock), "DOC_TECHNICAL",
                "Technical Policies",
                "Access to technical maintenance policies");

        // 13. IT Policies (ORG_IT)
        createPermissionIfNotExists(resolveBlock(blocks, "COMPANY_POLICIES", defaultBlock), "DOC_IT",
                "IT Policies",
                "Access to IT policies and procedures");

        // 14. Real Estate Policies (ORG_RE)
        createPermissionIfNotExists(resolveBlock(blocks, "COMPANY_POLICIES", defaultBlock), "DOC_REAL_ESTATE",
                "Real Estate Policies",
                "Access to real estate policies and procedures");

        // 15. Shareholders Policies (ORG_SH)
        createPermissionIfNotExists(resolveBlock(blocks, "COMPANY_POLICIES", defaultBlock), "DOC_SHAREHOLDERS",
                "Shareholders Policies",
                "Access to shareholders policies and procedures");

        // 16. Legal Policies (ORG_LEGAL)
        createPermissionIfNotExists(resolveBlock(blocks, "COMPANY_POLICIES", defaultBlock), "DOC_LEGAL",
                "Legal Policies",
                "Access to legal policies and procedures");

        // 17. Quality Policies (ORG_QUAL)
        createPermissionIfNotExists(resolveBlock(blocks, "HSE_POLICIES", defaultBlock), "DOC_QUALITY",
                "Quality Policies",
                "Access to quality management policies");

        // 18. HSE Policies (ORG_HSE)
        createPermissionIfNotExists(resolveBlock(blocks, "HSE_POLICIES", defaultBlock), "DOC_HSE",
                "HSE Policies",
                "Access to health, safety and environment policies");

        // 19. Equipment Policies (ORG_EQUIP)
        createPermissionIfNotExists(resolveBlock(blocks, "HSE_POLICIES", defaultBlock), "DOC_EQUIPMENT",
                "Equipment Policies",
                "Access to equipment management policies");

        // 20. Drug & Alcohol Policies (ORG_DA)
        createPermissionIfNotExists(resolveBlock(blocks, "HSE_POLICIES", defaultBlock), "DOC_DRUG_ALCOHOL",
                "Drug & Alcohol Policies",
                "Access to drug and alcohol policies");

        // 21. Incident Policies (ORG_INC)
        createPermissionIfNotExists(resolveBlock(blocks, "HSE_POLICIES", defaultBlock), "DOC_INCIDENT",
                "Incident Newsletters",
                "Access to incident reports and newsletters");

        // 22. SOP Policies (ORG_SOP)
        createPermissionIfNotExists(resolveBlock(blocks, "HSE_POLICIES", defaultBlock), "DOC_SOP",
                "Standard Operating Procedures",
                "Access to standard operating procedures");

        // 23. Suppliers Contracts (ORG_SUPP)
        createPermissionIfNotExists(resolveBlock(blocks, "THIRD_PARTY_CONTRACTS", defaultBlock), "DOC_SUPPLIERS",
                "Suppliers Contracts",
                "Access to supplier contracts and agreements");

        // 24. Rental Assets Contracts (ORG_RENT_CON)
        createPermissionIfNotExists(resolveBlock(blocks, "THIRD_PARTY_CONTRACTS", defaultBlock), "DOC_RENTAL_CONTRACTS",
                "Rental Assets Contracts",
                "Access to rental asset contracts");

        // 25. Client Commercial (ORG_CLIENT)
        createPermissionIfNotExists(resolveBlock(blocks, "THIRD_PARTY_CONTRACTS", defaultBlock), "DOC_CLIENT_COMMERCIAL",
                "Client Commercial Contracts",
                "Access to client and commercial contracts");

        // 26. Rental Assets (ORG_RENT_ASSET)
                createPermissionIfNotExists(resolveBlock(blocks, "THIRD_PARTY_CONTRACTS", defaultBlock), "DOC_RENTAL_ASSETS",
                "Rental Assets",
                "Access to rental assets management");
    }

        private Block resolveBlock(Map<String, Block> blocks, String key, Block fallback) {
                Block block = blocks.get(key);
                return block != null ? block : fallback;
        }

    private Block getOrCreateBlock(String code, String name, String description) {
        return blockRepository.findByBlockCode(code)
                .orElseGet(() -> {
                    Block block = new Block();
                    block.setBlockCode(code);
                    block.setName(name);
                    block.setDescription(description);
                    return blockRepository.save(block);

                });
    }

    private void createPermissionIfNotExists(Block block, String code, String name, String description) {
        if (!permissionRepository.existsByCode(code)) {
            Permission permission = new Permission();
            permission.setBlock(block);
            permission.setCode(code);
            permission.setName(name);
            permission.setDescription(description);
            permissionRepository.save(permission);
        }
    }

}
