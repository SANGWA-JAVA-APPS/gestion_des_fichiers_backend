package com.bar.gestiondesfichier.document.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Insurance projection for insurance records
 */
public interface InsuranceProjection extends BaseDocumentRelatedProjection {
    String getConcerns();
    String getCoverage();
    BigDecimal getInsuredValue();
    LocalDateTime getDateValidity();
    LocalDateTime getRenewalDate();
}