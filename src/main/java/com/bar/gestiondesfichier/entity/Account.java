package com.bar.gestiondesfichier.entity;

import com.bar.gestiondesfichier.document.model.SectionCategory;
import com.bar.gestiondesfichier.location.model.LocationEntity;
import com.bar.gestiondesfichier.location.model.Permission;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Setter
@Getter
@Entity
@Table(name = "accounts")
public class Account {
    // Setters
    // Getters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_entity_id")
    @JsonBackReference("entity-accounts")
    private LocationEntity locationEntity;
    @Column()
    private String gender;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_category_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "accounts"})
    private AccountCategory accountCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "account_section_categories",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "section_category_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"account_id", "section_category_id"}
            )
    )

    @JsonIgnoreProperties("accounts")
    private Set<SectionCategory> sectionCategories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "account_permissions",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"account_id", "permission_id"}
            )
    )
    private Set<Permission> permissions = new HashSet<>();

    public void addSectionCategory(SectionCategory sectionCategory) {
        this.sectionCategories.add(sectionCategory);
    }
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public void clearPermissions() {
        this.permissions.clear();
    }

    public void removeSectionCategory(SectionCategory sectionCategory) {
        this.sectionCategories.remove(sectionCategory);
    }

    public void clearSectionCategories() {
        this.sectionCategories.clear();
    }

    // Default constructor
    public Account() {}

    // Constructor with all fields
    public Account(Long id, String username, String password, String email, String fullName,
                  String phoneNumber, String gender, boolean active, LocalDateTime createdAt, 
                  LocalDateTime updatedAt, AccountCategory accountCategory) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accountCategory = accountCategory;
    }

    // Constructor for creating new accounts
    public Account(String username, String password, String email, String fullName,
            String phoneNumber, String gender, AccountCategory accountCategory) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.accountCategory = accountCategory;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }



    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}