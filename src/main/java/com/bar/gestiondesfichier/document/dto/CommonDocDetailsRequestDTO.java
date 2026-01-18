package com.bar.gestiondesfichier.document.dto;



import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommonDocDetailsRequestDTO {
    private String reference;
    private String description;
    private String status;
    private LocalDateTime dateTime;
    private String version;
    private Long sectionId; // just the ID to link section
    private LocalDateTime expirationDate;
}
