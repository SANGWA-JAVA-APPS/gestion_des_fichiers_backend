package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDateTime;

/**
 * CommCompPolicies projection for commercial company policies
 */
public interface CommCompPoliciesProjection extends BaseDocumentRelatedProjection {
    String getReference();
    String getDescription();
    String getPolicyStatus(); // This is the String policyStatus field from CommCompPolicies
    String getVersion();
    LocalDateTime getExpirationDate();
    SectionCategoryInfo getSection();
}