package com.magerwa.gestiondesfichiers.service;

import com.magerwa.gestiondesfichiers.entity.*;
import com.magerwa.gestiondesfichiers.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganizationService {
    
    @Autowired
    private CountryRepository countryRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private MagerwaEntityRepository entityRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private SectionRepository sectionRepository;

    // Country operations
    public List<Country> findAllCountries() {
        return countryRepository.findAll();
    }
    
    public Optional<Country> findCountryById(Long id) {
        return countryRepository.findById(id);
    }
    
    public Country createCountry(Country country) {
        if (countryRepository.existsByName(country.getName())) {
            throw new RuntimeException("Country name already exists");
        }
        if (country.getCode() != null && countryRepository.existsByCode(country.getCode())) {
            throw new RuntimeException("Country code already exists");
        }
        return countryRepository.save(country);
    }
    
    public Country updateCountry(Country country) {
        return countryRepository.save(country);
    }
    
    public void deleteCountry(Long id) {
        countryRepository.deleteById(id);
    }

    // Department operations
    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
    }
    
    public List<Department> findDepartmentsByCountryId(Long countryId) {
        return departmentRepository.findByCountryId(countryId);
    }
    
    public Optional<Department> findDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }
    
    public Department createDepartment(Department department) {
        if (departmentRepository.existsByNameAndCountry(department.getName(), department.getCountry())) {
            throw new RuntimeException("Department name already exists in this country");
        }
        return departmentRepository.save(department);
    }
    
    public Department updateDepartment(Department department) {
        return departmentRepository.save(department);
    }
    
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    // Entity operations
    public List<MagerwaEntity> findAllEntities() {
        return entityRepository.findAll();
    }
    
    public List<MagerwaEntity> findEntitiesByDepartmentId(Long departmentId) {
        return entityRepository.findByDepartmentId(departmentId);
    }
    
    public Optional<MagerwaEntity> findEntityById(Long id) {
        return entityRepository.findById(id);
    }
    
    public MagerwaEntity createEntity(MagerwaEntity entity) {
        if (entityRepository.existsByNameAndDepartment(entity.getName(), entity.getDepartment())) {
            throw new RuntimeException("Entity name already exists in this department");
        }
        return entityRepository.save(entity);
    }
    
    public MagerwaEntity updateEntity(MagerwaEntity entity) {
        return entityRepository.save(entity);
    }
    
    public void deleteEntity(Long id) {
        entityRepository.deleteById(id);
    }

    // Module operations
    public List<com.magerwa.gestiondesfichiers.entity.Module> findAllModules() {
        return moduleRepository.findAll();
    }
    
    public List<com.magerwa.gestiondesfichiers.entity.Module> findModulesByEntityId(Long entityId) {
        return moduleRepository.findByEntityId(entityId);
    }
    
    public Optional<com.magerwa.gestiondesfichiers.entity.Module> findModuleById(Long id) {
        return moduleRepository.findById(id);
    }
    
    public com.magerwa.gestiondesfichiers.entity.Module createModule(com.magerwa.gestiondesfichiers.entity.Module module) {
        if (moduleRepository.existsByNameAndEntity(module.getName(), module.getEntity())) {
            throw new RuntimeException("Module name already exists in this entity");
        }
        return moduleRepository.save(module);
    }
    
    public com.magerwa.gestiondesfichiers.entity.Module updateModule(com.magerwa.gestiondesfichiers.entity.Module module) {
        return moduleRepository.save(module);
    }
    
    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    // Section operations
    public List<Section> findAllSections() {
        return sectionRepository.findAll();
    }
    
    public List<Section> findSectionsByModuleId(Long moduleId) {
        return sectionRepository.findByModuleId(moduleId);
    }
    
    public Optional<Section> findSectionById(Long id) {
        return sectionRepository.findById(id);
    }
    
    public Section createSection(Section section) {
        if (sectionRepository.existsByNameAndModule(section.getName(), section.getModule())) {
            throw new RuntimeException("Section name already exists in this module");
        }
        return sectionRepository.save(section);
    }
    
    public Section updateSection(Section section) {
        return sectionRepository.save(section);
    }
    
    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }
}