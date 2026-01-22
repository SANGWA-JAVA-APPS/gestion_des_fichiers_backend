package com.bar.gestiondesfichier.location.repository;


import com.bar.gestiondesfichier.location.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    // Find a permission by its unique code
    Optional<Permission> findByCode(String code);

    // Check if a permission with the given code exists
    boolean existsByCode(String code);
}