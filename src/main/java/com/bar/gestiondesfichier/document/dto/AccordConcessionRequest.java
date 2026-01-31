package com.bar.gestiondesfichier.document.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AccordConcessionRequest {

    private String contratConcession;
    private String numeroAccord;
    private String objetConcession;
    private String concessionnaire;
    private Integer dureeAnnees;
    private String conditionsFinancieres;
    private String emplacement;
    private String coordonneesGps;
    private String rapportTransfertGestion;
    private LocalDateTime dateDebutConcession;
    private LocalDateTime dateFinConcession;

    // Relationship IDs
    private Long sectionCategoryId;
    private Long statusId;

    // Account who created it (optional if you want to override)
    private Long doneById;
}