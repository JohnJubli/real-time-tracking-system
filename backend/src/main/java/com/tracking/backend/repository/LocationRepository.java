package com.tracking.backend.repository;

import com.tracking.backend.entity.Location;
import com.tracking.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LocationRepository
        extends JpaRepository<Location, Long> {

    // User location history
    List<Location> findByUserOrderByTimestampDesc(
            User user
    );

    // Latest location
    Optional<Location> findFirstByUserOrderByTimestampDesc(
            User user
    );

    // Location history between two dates
    List<Location> findByUserAndTimestampBetweenOrderByTimestampDesc(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );
}