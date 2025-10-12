package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDateTime;

/**
 * CertLicenses projection for certificates and licenses
 */
public interface CertLicensesProjection extends BaseDocumentRelatedProjection {

    String getDescription();

    String getAgentCertifica();

    String getNumeroAgent();

    LocalDateTime getDateCertificate();

    Integer getDureeCertificat();

    //cons1
    //cons2
}
