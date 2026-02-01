package com.bar.gestiondesfichier.document.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommonDocDetailsRequestDTO {
    private String reference;
    private String description;
    private String status;
    private LocalDateTime dateTime;
    private Long sectionCategoryId;
    private Long doneById;
    private Long documentId;
    private Long statusId;
    private LocalDateTime expirationDate;
}
