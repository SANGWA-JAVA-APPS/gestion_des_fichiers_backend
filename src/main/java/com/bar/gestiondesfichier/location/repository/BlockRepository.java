package com.bar.gestiondesfichier.location.repository;

import com.bar.gestiondesfichier.location.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    // Optional: find block by its code
    Optional<Block> findByBlockCode(String blockCode);

    // Optional: check if a block with a given code exists
    boolean existsByBlockCode(String blockCode);
}
