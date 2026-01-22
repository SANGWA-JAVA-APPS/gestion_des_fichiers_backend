package com.bar.gestiondesfichier.location.model;

import com.bar.gestiondesfichier.common.entity.NamedEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission extends NamedEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @Column(nullable = false, unique = true)
    private String code;
}