package com.bar.gestiondesfichier.document.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommThirdPartyCreateRequest {

    private String name;
    private String location;
    private String validity;
    private String activities;
    private Long sectionId;
    private Long statusId;

}