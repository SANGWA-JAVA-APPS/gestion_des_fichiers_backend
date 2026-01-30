package com.bar.gestiondesfichier.document.reporting;

import com.bar.gestiondesfichier.document.model.Document;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ReportingRepository extends Repository<Document, Long> {

    @Query(value = """
        SELECT
            (SELECT COUNT(*) FROM countries) AS totalCountries,
            (SELECT COUNT(*) FROM countries c
                LEFT JOIN location_entities le ON le.country_id = c.id AND le.active = 1
                WHERE c.active = 1 AND le.id IS NULL) AS countriesWithNoEntities,
            (SELECT COUNT(*) FROM location_entities) AS totalEntities,
            (SELECT COUNT(*) FROM location_entities le
                LEFT JOIN modules m ON m.location_entity_id = le.id AND m.active = 1
                WHERE le.active = 1 AND m.id IS NULL) AS entitiesWithNoModules,
            (SELECT COUNT(*) FROM modules) AS totalModules,
            (SELECT COUNT(*) FROM sections) AS totalSections,
            (SELECT COUNT(*) FROM section_category) AS totalSectionCategories,
            (SELECT COUNT(*) FROM files WHERE active = 1) AS totalFiles,
            (SELECT COUNT(*) FROM files WHERE active = 1 AND status = 'ARCHIVED') AS archivedFiles,
            (SELECT COUNT(*) FROM files WHERE active = 1 AND status = 'EXPIRED') AS expiredFiles,
            (SELECT COUNT(*) FROM files WHERE active = 1 AND expiration_date IS NULL) AS filesMissingExpiration,
            (SELECT COUNT(*) FROM (
                SELECT id FROM accord_concession WHERE active = 1
                UNION ALL SELECT id FROM comm_asset_land WHERE active = 1
                UNION ALL SELECT id FROM comm_comp_policies WHERE active = 1
                UNION ALL SELECT id FROM comm_followup_audit WHERE active = 1
                UNION ALL SELECT id FROM comm_third_party WHERE active = 1
                UNION ALL SELECT id FROM due_diligence WHERE active = 1
                UNION ALL SELECT id FROM norme_loi WHERE active = 1
                UNION ALL SELECT id FROM permi_construction WHERE active = 1
                UNION ALL SELECT id FROM estate WHERE active = 1
                UNION ALL SELECT id FROM cert_licenses WHERE active = 1
                UNION ALL SELECT id FROM cargo_damage WHERE active = 1
                UNION ALL SELECT id FROM insurance WHERE active = 1
                UNION ALL SELECT id FROM litigation_followup WHERE active = 1
                UNION ALL SELECT id FROM third_party_claims WHERE active = 1
                UNION ALL SELECT id FROM equipemt_id WHERE active = 1
            ) d) AS totalDocRecords,
            (SELECT COUNT(*) FROM (
                SELECT doc_id FROM accord_concession WHERE active = 1
                UNION ALL SELECT doc_id FROM comm_asset_land WHERE active = 1
                UNION ALL SELECT doc_id FROM comm_comp_policies WHERE active = 1
                UNION ALL SELECT doc_id FROM comm_followup_audit WHERE active = 1
                UNION ALL SELECT doc_id FROM comm_third_party WHERE active = 1
                UNION ALL SELECT doc_id FROM due_diligence WHERE active = 1
                UNION ALL SELECT doc_id FROM norme_loi WHERE active = 1
                UNION ALL SELECT doc_id FROM permi_construction WHERE active = 1
                UNION ALL SELECT doc_id FROM estate WHERE active = 1
                UNION ALL SELECT doc_id FROM cert_licenses WHERE active = 1
                UNION ALL SELECT doc_id FROM cargo_damage WHERE active = 1
                UNION ALL SELECT doc_id FROM insurance WHERE active = 1
                UNION ALL SELECT doc_id FROM litigation_followup WHERE active = 1
                UNION ALL SELECT doc_id FROM third_party_claims WHERE active = 1
                UNION ALL SELECT doc_id FROM equipemt_id WHERE active = 1
            ) d WHERE d.doc_id IS NULL) AS documentsWithoutFileLink,
            (SELECT COUNT(*) FROM (
                SELECT statut_id FROM accord_concession WHERE active = 1
                UNION ALL SELECT statut_id FROM comm_asset_land WHERE active = 1
                UNION ALL SELECT statut_id FROM comm_comp_policies WHERE active = 1
                UNION ALL SELECT statut_id FROM comm_followup_audit WHERE active = 1
                UNION ALL SELECT statut_id FROM comm_third_party WHERE active = 1
                UNION ALL SELECT statut_id FROM due_diligence WHERE active = 1
                UNION ALL SELECT statut_id FROM norme_loi WHERE active = 1
                UNION ALL SELECT statut_id FROM permi_construction WHERE active = 1
                UNION ALL SELECT statut_id FROM estate WHERE active = 1
                UNION ALL SELECT statut_id FROM cert_licenses WHERE active = 1
                UNION ALL SELECT statut_id FROM cargo_damage WHERE active = 1
                UNION ALL SELECT statut_id FROM insurance WHERE active = 1
                UNION ALL SELECT statut_id FROM litigation_followup WHERE active = 1
                UNION ALL SELECT statut_id FROM third_party_claims WHERE active = 1
                UNION ALL SELECT statut_id FROM equipemt_id WHERE active = 1
            ) s LEFT JOIN docstatus d ON d.id = s.statut_id
            WHERE s.statut_id IS NULL OR d.id IS NULL) AS documentsMissingStatus,
            (SELECT COUNT(*) FROM (
                SELECT le.id AS entity_id,
                    COALESCE(SUM(section_docs.total_docs), 0) AS total_docs
                FROM location_entities le
                LEFT JOIN modules m ON m.location_entity_id = le.id AND m.active = 1
                LEFT JOIN sections s ON s.module_id = m.id AND s.active = 1
                LEFT JOIN (
                    SELECT section_id, SUM(doc_count) AS total_docs
                    FROM (
                        SELECT section_id, COUNT(*) AS doc_count FROM comm_asset_land WHERE active = 1 GROUP BY section_id
                        UNION ALL SELECT section_id, COUNT(*) FROM comm_comp_policies WHERE active = 1 GROUP BY section_id
                        UNION ALL SELECT section_id, COUNT(*) FROM comm_followup_audit WHERE active = 1 GROUP BY section_id
                        UNION ALL SELECT section_id, COUNT(*) FROM comm_third_party WHERE active = 1 GROUP BY section_id
                        UNION ALL SELECT section_id, COUNT(*) FROM due_diligence WHERE active = 1 GROUP BY section_id
                    ) x
                    GROUP BY section_id
                ) section_docs ON section_docs.section_id = s.id
                WHERE le.active = 1
                GROUP BY le.id
                HAVING total_docs > :docThreshold
            ) overloaded_entities) AS entitiesWithTooManyDocuments
        """, nativeQuery = true)
    ReportingSummaryProjection getSummary(@Param("docThreshold") int docThreshold);

    @Query(value = """
        SELECT doc_type AS docType, total AS total FROM (
            SELECT 'accord_concession' AS doc_type, COUNT(*) AS total FROM accord_concession WHERE active = 1
            UNION ALL SELECT 'permi_construction', COUNT(*) FROM permi_construction WHERE active = 1
            UNION ALL SELECT 'norme_loi', COUNT(*) FROM norme_loi WHERE active = 1
            UNION ALL SELECT 'comm_asset_land', COUNT(*) FROM comm_asset_land WHERE active = 1
            UNION ALL SELECT 'comm_comp_policies', COUNT(*) FROM comm_comp_policies WHERE active = 1
            UNION ALL SELECT 'comm_followup_audit', COUNT(*) FROM comm_followup_audit WHERE active = 1
            UNION ALL SELECT 'comm_third_party', COUNT(*) FROM comm_third_party WHERE active = 1
            UNION ALL SELECT 'due_diligence', COUNT(*) FROM due_diligence WHERE active = 1
            UNION ALL SELECT 'estate', COUNT(*) FROM estate WHERE active = 1
            UNION ALL SELECT 'cert_licenses', COUNT(*) FROM cert_licenses WHERE active = 1
            UNION ALL SELECT 'cargo_damage', COUNT(*) FROM cargo_damage WHERE active = 1
            UNION ALL SELECT 'insurance', COUNT(*) FROM insurance WHERE active = 1
            UNION ALL SELECT 'litigation_followup', COUNT(*) FROM litigation_followup WHERE active = 1
            UNION ALL SELECT 'third_party_claims', COUNT(*) FROM third_party_claims WHERE active = 1
            UNION ALL SELECT 'equipment_id', COUNT(*) FROM equipemt_id WHERE active = 1
        ) t
        ORDER BY total DESC
        """, nativeQuery = true)
    List<DocumentTypeCountProjection> getDocumentTypeCounts();

    @Query(value = """
        SELECT COALESCE(status, 'UNKNOWN') AS status, COUNT(*) AS total
        FROM files
        WHERE active = 1
        GROUP BY COALESCE(status, 'UNKNOWN')
        ORDER BY total DESC
        """, nativeQuery = true)
    List<FileStatusCountProjection> getFileStatusCounts();
}
