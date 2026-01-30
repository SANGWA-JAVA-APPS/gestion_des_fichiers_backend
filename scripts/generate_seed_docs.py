from pathlib import Path

base = Path(r"d:/Apache/DEV/SPRING BOOT/gestion_des_fichiers_backend/src/main/resources/seed-files")
base.mkdir(parents=True, exist_ok=True)

doc_types = {
    "norme_loi": "Norme_Loi",
    "accord_concession": "Accord_Concession",
    "comm_asset_land": "Comm_Asset_Land",
    "permi_construction": "Permi_Construction",
    "estate": "Estate",
    "cert_licenses": "Cert_Licenses",
    "cargo_damage": "Cargo_Damage",
    "comm_third_party": "Comm_Third_Party",
}

exts = ["pdf", "docx", "xlsx"]

pdf_template = "%PDF-1.4\n1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 300 144] /Contents 4 0 R >>\nendobj\n4 0 obj\n<< /Length 60 >>\nstream\nBT\n/F1 12 Tf\n72 100 Td\n({text}) Tj\nET\nendstream\nendobj\n5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Name /F1 >>\nendobj\nxref\n0 6\n0000000000 65535 f \n0000000010 00000 n \n0000000060 00000 n \n0000000115 00000 n \n0000000205 00000 n \n0000000300 00000 n \ntrailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n380\n%%EOF\n"

sql_lines = []
sql_lines.append("\n-- Seed accounts/sections for document file data\n")
sql_lines.append("INSERT INTO account_categories (id, name, description) VALUES (1, 'Seed Category', 'Seed data category');\n")
sql_lines.append("INSERT INTO accounts (id, username, password, email, full_name, phone_number, gender, active, created_at, updated_at, account_category_id)\n"
                 "VALUES (1, 'seed_user', 'seed_password', 'seed@example.com', 'Seed User', '0000000000', 'N/A', 1, NOW(), NOW(), 1);\n")
sql_lines.append("INSERT INTO section_category (id, name, description, code, active, created_at, updated_at)\n"
                 "VALUES (1, 'Default Section', 'Seed section category', 'DEFAULT', 1, NOW(), NOW());\n\n")

def content_type(ext):
    return {
        "pdf": "application/pdf",
        "docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "xlsx": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    }[ext]

bases = {
    "norme_loi": (100001, 200001),
    "accord_concession": (110001, 210001),
    "comm_asset_land": (120001, 220001),
    "permi_construction": (130001, 230001),
    "estate": (140001, 240001),
    "cert_licenses": (150001, 250001),
    "cargo_damage": (160001, 260001),
    "comm_third_party": (170001, 270001),
}

for folder, prefix in doc_types.items():
    folder_path = base / folder
    folder_path.mkdir(parents=True, exist_ok=True)
    doc_id_base, entity_id_base = bases[folder]

    sql_lines.append(f"\n-- Seed {folder} documents\n")

    for i in range(1, 51):
        ext = exts[(i - 1) % len(exts)]
        filename = f"{folder}_{i:03d}.{ext}"
        file_path = folder_path / filename

        if ext == "pdf":
            content = pdf_template.format(text=f"Seed {folder} file {i:03d}")
        else:
            content = f"Seed {folder} file {i:03d} ({ext})\n"

        file_path.write_text(content, encoding="utf-8")
        size = file_path.stat().st_size

        doc_id = doc_id_base + (i - 1)
        entity_id = entity_id_base + (i - 1)

        original_name = f"{prefix}_{i:03d}.{ext}"
        file_rel_path = f"seed-files/{folder}/{filename}"

        sql_lines.append(
            "INSERT INTO files (id, file_name, original_file_name, content_type, file_size, file_path, owner_id, created_at, updated_at, expiration_date, status, expiry_date, expiry_alert_sent, version, active) VALUES "
            f"({doc_id}, '{filename}', '{original_name}', '{content_type(ext)}', {size}, '{file_rel_path}', 1, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 5 YEAR), 'ACTIVE', NULL, 0, '1.0', 1);\n"
        )

        if folder == "norme_loi":
            sql_lines.append(
                "INSERT INTO norme_loi (id, date_time, doneby, doc_id, statut_id, reference, description, date_vigueur, domaine_application, active, created_at, updated_at) VALUES "
                f"({entity_id}, NOW(), 1, {doc_id}, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'NL-{i:03d}', 'Seed norme loi {i:03d}', NOW(), 'General', 1, NOW(), NOW());\n"
            )
        elif folder == "accord_concession":
            sql_lines.append(
                "INSERT INTO accord_concession (id, date_time, doneby, doc_id, statut_id, contrat_concession, numero_accord, objet_concession, concessionnaire, duree_annees, conditions_financieres, emplacement, coordonnees_gps, rapport_transfert_gestion, date_debut_concession, date_fin_concession, section_category_id, active, created_at, updated_at) VALUES "
                f"({entity_id}, NOW(), 1, {doc_id}, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'Contrat {i:03d}', 'ACC-{i:03d}', 'Objet {i:03d}', 'Concessionnaire {i:03d}', 5, 'Conditions {i:03d}', 'Emplacement {i:03d}', '0,0', 'Rapport {i:03d}', NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), (SELECT id FROM section_category WHERE code='DEFAULT' LIMIT 1), 1, NOW(), NOW());\n"
            )
        elif folder == "comm_asset_land":
            sql_lines.append(
                "INSERT INTO comm_asset_land (id, date_time, doneby, doc_id, statut_id, description, reference, date_obtention, coordonnees_gps, emplacement, section_id, active, created_at, updated_at) VALUES "
                f"({entity_id}, NOW(), 1, {doc_id}, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'Description {i:03d}', 'CAL-{i:03d}', NOW(), '0,0', 'Emplacement {i:03d}', (SELECT id FROM section_category WHERE code='DEFAULT' LIMIT 1), 1, NOW(), NOW());\n"
            )
        elif folder == "permi_construction":
            sql_lines.append(
                "INSERT INTO permi_construction (id, date_time, doneby, doc_id, statut_id, numero_permis, projet, localisation, date_delivrance, date_expiration, autorite_delivrance, section_category_id, reference_titre_foncier, refe_permis_construire, date_validation, date_estimee_travaux, active, created_at, updated_at) VALUES "
                f"({entity_id}, NOW(), 1, {doc_id}, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'PC-{i:03d}', 'Projet {i:03d}', 'Localisation {i:03d}', NOW(), DATE_ADD(NOW(), INTERVAL 2 YEAR), 'Autorite {i:03d}', (SELECT id FROM section_category WHERE code='DEFAULT' LIMIT 1), 'TF-{i:03d}', 'RPC-{i:03d}', NOW(), NOW(), 1, NOW(), NOW());\n"
            )
        elif folder == "estate":
            sql_lines.append(
                "INSERT INTO estate (id, date_time, doneby, doc_id, statut_id, reference, estate_type, emplacement, coordonnees_gps, date_of_building, comments, active, created_at, updated_at) VALUES "
                f"({entity_id}, NOW(), 1, {doc_id}, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'EST-{i:03d}', 'Type {i:03d}', 'Emplacement {i:03d}', '0,0', NOW(), 'Comments {i:03d}', 1, NOW(), NOW());\n"
            )
        elif folder == "cert_licenses":
            sql_lines.append(
                "INSERT INTO cert_licenses (id, date_time, doneby, doc_id, statut_id, description, agent_certifica, numero_agent, date_certificate, duree_certificat, active, created_at, updated_at) VALUES "
                f"({entity_id}, NOW(), 1, {doc_id}, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'Cert {i:03d}', 'Agent {i:03d}', 'AG-{i:03d}', NOW(), 12, 1, NOW(), NOW());\n"
            )
        elif folder == "cargo_damage":
            sql_lines.append(
                "INSERT INTO cargo_damage (id, date_time, doneby, doc_id, statut_id, refe_request, description, quotation_contract_num, date_request, date_contract, active, created_at, updated_at) VALUES "
                f"({entity_id}, NOW(), 1, {doc_id}, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'CD-{i:03d}', 'Desc {i:03d}', 'QC-{i:03d}', NOW(), NOW(), 1, NOW(), NOW());\n"
            )
        elif folder == "comm_third_party":
            sql_lines.append(
                "INSERT INTO comm_third_party (id, date_time, doneby, doc_id, statut_id, name, location, validity, activities, section_id, active, created_at, updated_at) VALUES "
                f"({entity_id}, NOW(), 1, {doc_id}, (SELECT id FROM docstatus WHERE name='Valid' LIMIT 1), 'ThirdParty {i:03d}', 'Location {i:03d}', '2026-12-31', 'Activities {i:03d}', (SELECT id FROM section_category WHERE code='DEFAULT' LIMIT 1), 1, NOW(), NOW());\n"
            )

sql_path = Path(r"d:/Apache/DEV/SPRING BOOT/gestion_des_fichiers_backend/src/main/resources/seed-documents.sql")
sql_path.write_text("".join(sql_lines), encoding="utf-8")
print(f"Generated SQL: {sql_path}")
print("Files created under seed-files/")
