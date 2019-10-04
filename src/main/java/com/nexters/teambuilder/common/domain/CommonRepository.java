package com.nexters.teambuilder.common.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonRepository extends JpaRepository<Common, Integer> {
    Optional<Common> findTopByOrderByIdDesc();
}
