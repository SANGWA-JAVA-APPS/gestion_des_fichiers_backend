package com.magerwa.gestiondesfichiers.repository;

import com.magerwa.gestiondesfichiers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    List<User> findByCountryId(Long countryId);
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByEntityId(Long entityId);
    List<User> findByModuleId(Long moduleId);
    List<User> findBySectionId(Long sectionId);
    
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findAllEnabledUsers();
}