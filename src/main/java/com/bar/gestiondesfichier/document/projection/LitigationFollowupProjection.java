package com.bar.gestiondesfichier.document.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * LitigationFollowup projection for litigation followup records
 */
public interface LitigationFollowupProjection extends BaseDocumentRelatedProjection {
    String getConcern();
    String getStatut();
    LocalDateTime getCreationDate();
    LocalDateTime getExpectedCompletion();
    BigDecimal getRiskValue();
}