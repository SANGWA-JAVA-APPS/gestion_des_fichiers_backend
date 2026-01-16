package com.bar.gestiondesfichier.location.model;

import com.bar.gestiondesfichier.common.entity.NamedEntity;
import com.bar.gestiondesfichier.entity.Account;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "location_entities", indexes = {
    @Index(name = "idx_entity_country", columnList = "country_id"),
    @Index(name = "idx_entity_name_country", columnList = "name, country_id")
})
public class LocationEntity extends NamedEntity {

    @Column(name = "entity_type", length = 100)
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "code", unique = true, length = 100)
    private String code;

    @Column(name = "postal_code", length = 100)
    private String postalCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false)
    @JsonBackReference("country-entities")
    private Country country;
    @OneToMany(mappedBy = "locationEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("entity-accounts")
    private List<Account> accounts;
    @OneToMany(mappedBy = "locationEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("entity-modules")
    private List<Module> modules;

    public enum EntityType {
        PROVINCE,
        STATE,
        REGION,
        DISTRICT,
        CITY,
        TOWN,
        VILLAGE
    }
}