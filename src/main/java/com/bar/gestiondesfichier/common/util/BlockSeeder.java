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

@Component
@RequiredArgsConstructor
@Transactional
@Order(5) // Run after CountrySeeder(1), DocStatusSeeder(2), but before DataInitializationService(10)
public class BlockSeeder implements CommandLineRunner {

    private final BlockRepository blockRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {
        seedNormsBlock();
        seedAssetLandBlock();
        seedAssetEstateBlock();
        seedAssetEquipmentBlock();
        seedCertificatesLicensesBlock();
        seedCompanyPoliciesBlock();
        seedHsePoliciesBlock();
        seedEthicsCompliancePoliciesBlock();
        seedThirdPartyContractsBlock();
        seedRisksBlock();
    }

    private void seedNormsBlock() {
        Block block = getOrCreateBlock(
                "NORMS_LAWS_REGULATION",
                "Norms, Laws and Regulations",
                "Manage all norms, legal regulations, and compliance rules for the organization."
        );

        createBasicCrudPermissions(block, "NORMS");

    }

    private void seedAssetLandBlock() {
        Block block = getOrCreateBlock(
                "ASSET_LAND",
                "Asset Land",
                "Track land assets including titles, permits, concessions, and related documents."
        );

        // Access to the Asset Land module
        createPermissionIfNotExists(block, "ASSET_LAND_ACCESS",
                "Access Asset Land Module",
                "Allows access to all asset land related data and documents.");

        // Titres Fonciers
        createPermissionIfNotExists(block, "ASSET_LAND_TITRES_FONCIERS",
                "Titres Fonciers",
                "Manage land titles including description, reference, date of obtaining, GPS coordinates, location, status, and uploads.");

        // Permis de Constructions
        createPermissionIfNotExists(block, "ASSET_LAND_PERMIS_CONSTRUCTIONS",
                "Permis de Constructions",
                "Manage construction permits linked to land titles including reference, validation date, estimated work date, and status.");

        // Décisions Fonciers
        createPermissionIfNotExists(block, "ASSET_LAND_DECISIONS_FONCIERS",
                "Décisions Fonciers",
                "Track land decisions including reference to land title, location, GPS coordinates, and status.");

        // Accord de Concession
        createPermissionIfNotExists(block, "ASSET_LAND_ACCORD_CONCESSION",
                "Accord de Concession",
                "Manage concession agreements including contract details, location, GPS coordinates, management transfer report, start and end dates, and status.");

        // Héritage / Legacy
        createPermissionIfNotExists(block, "ASSET_LAND_HERITAGE_LEGACY",
                "Héritage / Legacy",
                "Track inherited land assets including description, location, GPS coordinates, comments, status, start and end dates.");
    }


    private void seedAssetEstateBlock() {
        Block block = getOrCreateBlock(
                "ASSET_ESTATE",
                "Asset Estate",
                "Manage estates such as houses, apartments, offices, yards, and related property documents."
        );

        createBasicCrudPermissions(block, "ESTATE");
        createPermissionIfNotExists(block, "ASSET_ESTATE_ID",
                "Estate ID",
                "Manage estates including type (house, apartment, office, yard), reference, location, GPS coordinates, status, date of building, and comments.");
    }

    private void seedAssetEquipmentBlock() {
        Block block = getOrCreateBlock(
                "ASSET_EQUIPMENT",
                "Asset Equipment",
                "Manage physical equipment, machinery, tools, and related asset documentation."
        );

        createBasicCrudPermissions(block, "EQUIPMENT");
        createPermissionIfNotExists(block, "ASSET_EQUIPMENT_ID",
                "Equipment ID",
                "Manage equipment including type (vehicle, forklift), serial number, plate number, equipment state, purchase date, technical inspection date, insurance, and associated documents (vehicle + registration).");
    }

    private void seedCertificatesLicensesBlock() {
        Block block = getOrCreateBlock(
                "CERTIFICATES_LICENSES",
                "Certificates & Licences",
                "Manage all certificates, licenses, and official authorizations required by the organization."
        );

        createBasicCrudPermissions(block, "CERT_LICENSE");
    }

    private void seedCompanyPoliciesBlock() {
        Block block = getOrCreateBlock(
                "COMPANY_POLICIES",
                "Company Policies",
                "Store and manage internal company policies, procedures, and governance documents."
        );

        createBasicCrudPermissions(block, "COMPANY_POLICY");

        createPermissionIfNotExists(block, "COMPANY_POLICY", "Full Access to Company Policies", "Full access to all company policies");

        createPermissionIfNotExists(block, "COMPANY_POLICY_FIN", "Financial Policies Access", "Full access to all Financial Policies");
        createPermissionIfNotExists(block, "COMPANY_POLICY_PROC", "Procurement Policies Access", "Full access to all Procurement Policies");
        createPermissionIfNotExists(block, "COMPANY_POLICY_HR", "HR Policies Access", "Full access to all HR Policies");
        createPermissionIfNotExists(block, "COMPANY_POLICY_TECH", "Technical Maintenance Policies Access", "Full access to all Technical Maintenance Policies");
        createPermissionIfNotExists(block, "COMPANY_POLICY_IT", "IT Policies Access", "Full access to all IT Policies");
        createPermissionIfNotExists(block, "COMPANY_POLICY_RE", "Real Estate Policies Access", "Full access to all Real Estate Policies");
        createPermissionIfNotExists(block, "COMPANY_POLICY_SH", "Shareholders Policies Access", "Full access to all Shareholders Policies");
        createPermissionIfNotExists(block, "COMPANY_POLICY_LEGAL", "Legal Policies Access", "Full access to all Legal Policies");
    }

    private void seedHsePoliciesBlock() {
        Block block = getOrCreateBlock(
                "HSE_POLICIES",
                "HSE Policies",
                "Health, Safety, and Environment policies and compliance documentation."
        );

        createBasicCrudPermissions(block, "HSE_POLICY");
        // Category-specific permissions
        createPermissionIfNotExists(block, "HSE_POLICY_QUALITY", "Quality Policies Access", "Full access to all Quality Policies");
        createPermissionIfNotExists(block, "HSE_POLICY_HSE", "HSE Policies Access", "Full access to all HSE Policies");
        createPermissionIfNotExists(block, "HSE_POLICY_EQUIP", "Equipment Policies Access", "Full access to all Equipment Policies");
        createPermissionIfNotExists(block, "HSE_POLICY_DA", "Drug & Alcohol Policies Access", "Full access to all Drug & Alcohol Policies");
        createPermissionIfNotExists(block, "HSE_POLICY_INDUCTION", "Induction Policies Access", "Full access to all Induction Policies");
        createPermissionIfNotExists(block, "HSE_POLICY_INCIDENT", "Incident Newsletter Access", "Full access to all Incident Newsletters");
        createPermissionIfNotExists(block, "HSE_POLICY_AUDIT", "Follow-up / Audit Reports Access", "Full access to all Audit Reports and Follow-ups");
        createPermissionIfNotExists(block, "HSE_POLICY_SOP", "SOP (OPS) Access", "Full access to all SOP (OPS) Policies");

    }

    private void seedEthicsCompliancePoliciesBlock() {
        Block block = getOrCreateBlock(
                "ETHICS_COMPLIANCE_POLICIES",
                "Ethics & Compliance Policies",
                "Policies governing ethics, integrity, compliance, and regulatory obligations."
        );

        createBasicCrudPermissions(block, "ETHICS_POLICY");

        createPermissionIfNotExists(block, "ETHICS_POLICY_CODE_OF_CONDUCT", "Code of Conduct Policies Access", "Full access to all Code of Conduct Policies");
        createPermissionIfNotExists(block, "ETHICS_POLICY_AUDIT_REPORT", "Audit report / Follow-up Reports Access", "Full access to all Audit Reports and Follow-ups");
        createPermissionIfNotExists(block, "ETHICS_POLICY_DUE_DILIGENCE", "Due Diligence Policies Access", "Full access to all Due Diligence Policies");
    }

    private void seedThirdPartyContractsBlock() {
        Block block = getOrCreateBlock(
                "THIRD_PARTY_CONTRACTS",
                "Third Party Contracts",
                "Manage supplier, client, rental, and partner contracts and agreements."
        );

        createBasicCrudPermissions(block, "THIRD_PARTY_CONTRACT");

        // Suppliers Contracts
        createPermissionIfNotExists(block, "THIRD_PARTY_CONTRACTS_SUPPLIERS",
                "Suppliers Contracts",
                "Access to supplier contracts details.");

        // Client / Commercial Contracts
        createPermissionIfNotExists(block, "THIRD_PARTY_CONTRACTS_CLIENT",
                "Client / Commercial Contracts",
                "Access to client/commercial contracts.");

        // Rental Assets Contracts
        createPermissionIfNotExists(block, "THIRD_PARTY_CONTRACTS_RENTAL_CONTRACTS",
                "Rental Assets Contracts",
                "Access to rental assets contracts.");

        // Rental Assets themselves
        createPermissionIfNotExists(block, "THIRD_PARTY_CONTRACTS_RENTAL_ASSETS",
                "Rental Assets",
                "Access to rental assets details.");

        // Cargo Damage Survey Agreements
        createPermissionIfNotExists(block, "THIRD_PARTY_CONTRACTS_CARGO_SURVEY",
                "Cargo Damage Survey Agreements",
                "Access to cargo damage survey agreements.");
    }

    private void seedRisksBlock() {
        Block block = getOrCreateBlock(
                "RISKS",
                "Risks",
                "Identify, track, and manage operational, legal, financial, and compliance risks."
        );

        createBasicCrudPermissions(block, "RISK");
        // Litigation follow up
        createPermissionIfNotExists(block, "RISKS_LITIGATION",
                "Litigation Follow Up",
                "Access and track litigation follow-ups including creation date, concern, status, expected completion, and risk value with configurable alerts and weekly reminders.");

        // Insurance follow up
        createPermissionIfNotExists(block, "RISKS_INSURANCE",
                "Insurance Follow Up",
                "Access and manage insurance follow-ups including concerns, coverage, values, validity dates, renewal dates, and asset status.");

        // Third party claims
        createPermissionIfNotExists(block, "RISKS_THIRD_PARTY_CLAIMS",
                "Third Party Claims",
                "Access and manage third party claims including reference, description, claim date, department in charge, status, and attached documents.");
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

    private void createBasicCrudPermissions(Block block, String prefix) {
//        createPermissionIfNotExists(block, prefix + "_VIEW", "View " + block.getName(), "View " + block.getName() + " records");
//        createPermissionIfNotExists(block, prefix + "_CREATE", "Create " + block.getName(), "Create " + block.getName() + " records");
//        createPermissionIfNotExists(block, prefix + "_UPDATE", "Update " + block.getName(), "Update " + block.getName() + " records");
//        createPermissionIfNotExists(block, prefix + "_DELETE", "Delete " + block.getName(), "Delete " + block.getName() + " records");
//        createPermissionIfNotExists(
//                block,
//                prefix + "_ALL",
//                "Full Access to " + block.getName(),
//                "Full access to all actions on " + block.getName()
//        );
    }
}
