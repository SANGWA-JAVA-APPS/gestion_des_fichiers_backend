package com.bar.gestiondesfichier.document.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CommThirdPartyUpdateRequest {

    private String name;
    private String location;
    private String validity;
    private String activities;
    private Long sectionId;
    private Long statusId;
}
