package com.bar.gestiondesfichier.document.projection;

import com.bar.gestiondesfichier.document.model.SectionCategory;

/**
 * CommThirdParty projection for commercial third party records
 */
public interface CommThirdPartyProjection extends BaseDocumentRelatedProjection {
    String getName();
    String getLocation();
    String getValidity();
    String getActivities();
    
    // Section relationship - using full object instead of just name
    SectionCategory getSection();
}