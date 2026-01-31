package com.bar.gestiondesfichier.document.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccordConcessionUpdateRequest {

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

    // RELATIONSHIP IDs
    private Long sectionCategoryId;
    private Long statusId;
}