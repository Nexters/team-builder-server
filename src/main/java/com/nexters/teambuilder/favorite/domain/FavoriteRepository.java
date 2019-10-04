package com.nexters.teambuilder.favorite.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Optional<Favorite> findFavoriteByIdeaId(Integer ideaId);
    List<Favorite> findAllByUuid(String uuid);
}
