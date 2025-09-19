package com.magerwa.gestiondesfichiers.repository;

import com.magerwa.gestiondesfichiers.entity.Module;
import com.magerwa.gestiondesfichiers.entity.MagerwaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByEntity(MagerwaEntity entity);
    List<Module> findByEntityId(Long entityId);
    Optional<Module> findByNameAndEntity(String name, MagerwaEntity entity);
    boolean existsByNameAndEntity(String name, MagerwaEntity entity);
}