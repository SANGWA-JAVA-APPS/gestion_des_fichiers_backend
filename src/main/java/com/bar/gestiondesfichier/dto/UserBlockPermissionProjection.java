package com.bar.gestiondesfichier.dto;

import lombok.Getter;
import lombok.Setter;


public interface UserBlockPermissionProjection {
    Long getBlockId();
    String getBlockName();
    String getBlockCode();
    Long getPermissionId();
    String getPermissionName();
    String getPermissionCode();
}
