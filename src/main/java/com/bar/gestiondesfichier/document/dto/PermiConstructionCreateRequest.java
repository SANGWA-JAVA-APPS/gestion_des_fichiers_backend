package com.bar.gestiondesfichier.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermiConstructionCreateRequest {

    /* =========================
       Core permit information
       ========================= */

    @NotBlank
    private String numeroPermis;

    @NotBlank
    private String projet;

    @NotBlank
    private String localisation;

    @NotNull
    private LocalDateTime dateDelivrance;

    @NotNull
    private LocalDateTime dateExpiration;

    @NotBlank
    private String autoriteDelivrance;

    /* =========================
       Legal / administrative refs
       ========================= */

    @NotBlank
    private String referenceTitreFoncier;

    @NotBlank
    private String refPermisConstuire;

    /* =========================
       Workflow dates
       ========================= */

    private LocalDateTime dateValidation;

    @NotNull
    private LocalDateTime dateEstimeeTravaux;

    /* =========================
       Relations (IDs only)
       ========================= */

    @NotNull
    private Long doneById;

    @NotNull
    private Long statusId;

    @NotNull
    private Long sectionCategoryId;
}

