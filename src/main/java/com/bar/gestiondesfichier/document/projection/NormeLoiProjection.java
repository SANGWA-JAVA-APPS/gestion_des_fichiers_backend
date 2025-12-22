package com.bar.gestiondesfichier.document.projection;

import java.time.LocalDateTime;

/**
 * NormeLoi projection for listing norms and laws
 */
public interface NormeLoiProjection extends BaseDocumentRelatedProjection {
    Long getId();
    LocalDateTime getDateTime();
    String getReference();
    String getDescription();
    LocalDateTime getDateVigueur();
    String getDomaineApplication();
}