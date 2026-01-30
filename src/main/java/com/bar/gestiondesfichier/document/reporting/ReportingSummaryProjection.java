package com.bar.gestiondesfichier.document.reporting;

public interface ReportingSummaryProjection {
    Long getTotalCountries();
    Long getCountriesWithNoEntities();
    Long getTotalEntities();
    Long getEntitiesWithNoModules();
    Long getTotalModules();
    Long getTotalSections();
    Long getTotalSectionCategories();
    Long getTotalFiles();
    Long getArchivedFiles();
    Long getExpiredFiles();
    Long getFilesMissingExpiration();
    Long getTotalDocRecords();
    Long getDocumentsWithoutFileLink();
    Long getDocumentsMissingStatus();
    Long getEntitiesWithTooManyDocuments();
}
