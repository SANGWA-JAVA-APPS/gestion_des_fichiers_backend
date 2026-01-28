package com.bar.gestiondesfichier.document.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDTO {
    private Long id;
    private String name;
    private String code; // optional, depends on your entity
    private String blockName;
    // optional

    public PermissionDTO() {}

    public PermissionDTO(Long id, String name, String code, String blockName) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.blockName = blockName;

    }
}
