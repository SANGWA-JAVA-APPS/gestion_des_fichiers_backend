package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDateTime;

/**
 * ThirdPartyClaims projection for third party claims
 */
public interface ThirdPartyClaimsProjection extends BaseDocumentRelatedProjection {
    String getReference();
    String getDescription();
    LocalDateTime getDateClaim();
    String getDepartmentInCharge();
}