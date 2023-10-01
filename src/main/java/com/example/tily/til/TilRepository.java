package com.example.tily.til;

import com.example.tily.step.Step;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TilRepository extends JpaRepository<Til, Long>{
}
