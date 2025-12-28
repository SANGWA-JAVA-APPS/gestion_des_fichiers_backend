package com.bar.gestiondesfichier.location.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequestDTO {
    private String name;
    private String description;
    private String sectionCode;
    private String sectionType;
    private Integer floorNumber;
    private String roomNumber;
    private Integer capacity;
    private String coordinates;
    private String accessLevel;
    private Long moduleId;
}
