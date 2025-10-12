package com.bar.gestiondesfichier.document.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CommFollowupAudit projection for audit follow-ups
 */
public interface CommFollowupAuditProjection extends BaseDocumentRelatedProjection {

    String getReference();

    String getDescription();

    LocalDateTime getDateAudit();

    String getAuditor();

    Integer getNumNonConform();

    String getTypeConform();

    BigDecimal getPercentComplete();

    String getDocAttach();

    SectionInfo getSection();

    interface SectionInfo {

        Long getId();

        String getName();
    }
}
