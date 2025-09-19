package com.magerwa.gestiondesfichiers.repository;

import com.magerwa.gestiondesfichiers.entity.MagerwaEntity;
import com.magerwa.gestiondesfichiers.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MagerwaEntityRepository extends JpaRepository<MagerwaEntity, Long> {
    List<MagerwaEntity> findByDepartment(Department department);
    List<MagerwaEntity> findByDepartmentId(Long departmentId);
    Optional<MagerwaEntity> findByNameAndDepartment(String name, Department department);
    boolean existsByNameAndDepartment(String name, Department department);
}