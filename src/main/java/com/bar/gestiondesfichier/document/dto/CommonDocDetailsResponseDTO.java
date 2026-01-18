package com.bar.gestiondesfichier.document.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommonDocDetailsResponseDTO {
    private Long id;
    private String reference;
    private String description;
    private String status;
    private LocalDateTime dateTime;
    private String version;
    private Long sectionId;
    private LocalDateTime expirationDate;
}
