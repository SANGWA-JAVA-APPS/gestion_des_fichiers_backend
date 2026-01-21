package com.bar.gestiondesfichier.document.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionCategoryDTO {

    private Long id;
    private String name;
    private String code;

    public SectionCategoryDTO(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}