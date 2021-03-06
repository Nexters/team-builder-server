package com.nexters.teambuilder.user.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUuid(String uuid);

    Optional<User> findUserById(String id);

    boolean existsById(String id);

    List<User> findAllByUuidIn(List<String> ids);

    long countByDissmissedIsTrue();
}
