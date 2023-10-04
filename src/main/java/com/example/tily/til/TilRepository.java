package com.example.tily.til;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TilRepository extends JpaRepository<Til, Long>{

    Til findTilById(Long id);
    Optional<Til> findById(Long id);
}
