package com.magerwa.gestiondesfichiers.repository;

import com.magerwa.gestiondesfichiers.entity.Department;
import com.magerwa.gestiondesfichiers.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByCountry(Country country);
    List<Department> findByCountryId(Long countryId);
    Optional<Department> findByNameAndCountry(String name, Country country);
    boolean existsByNameAndCountry(String name, Country country);
}