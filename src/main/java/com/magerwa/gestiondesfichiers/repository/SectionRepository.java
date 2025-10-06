package com.magerwa.gestiondesfichiers.repository;

import com.magerwa.gestiondesfichiers.entity.Section;
import com.magerwa.gestiondesfichiers.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByModule(Module module);
    List<Section> findByModuleId(Long moduleId);
    Optional<Section> findByNameAndModule(String name, Module module);
    boolean existsByNameAndModule(String name, Module module);
}