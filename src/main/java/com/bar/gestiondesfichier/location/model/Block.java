package com.bar.gestiondesfichier.location.model;

import com.bar.gestiondesfichier.common.entity.NamedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blocks")
@Setter
@Getter
@Data
public class Block extends NamedEntity {
    @Column(name = "block_code", nullable = false, unique = true)
    private String blockCode;
}

