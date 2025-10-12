package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDateTime;

/**
 * CargoDamage projection for cargo damage claims
 */
public interface CargoDamageProjection extends BaseDocumentRelatedProjection {
    String getRefeRequest();
    String getDescription();
    String getQuotationContractNum();
    LocalDateTime getDateRequest();
    LocalDateTime getDateContract();
}