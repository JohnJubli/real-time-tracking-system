package com.tracking.backend.service;

import com.tracking.backend.dto.LocationDTO;
import com.tracking.backend.entity.Location;
import com.tracking.backend.entity.User;
import com.tracking.backend.repository.LocationRepository;
import com.tracking.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public LocationService(
            LocationRepository locationRepository,
            UserRepository userRepository) {

        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }


    // =========================================================
    // SAVE LOCATION
    // =========================================================

    @Transactional
    public LocationDTO saveLocation(
            String username,
            LocationDTO dto) {

        validateCoordinates(
                dto.getLatitude(),
                dto.getLongitude()
        );

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + username
                        )
                );

        Location location = new Location();

        location.setLatitude(
                dto.getLatitude()
        );

        location.setLongitude(
                dto.getLongitude()
        );

        location.setTimestamp(
                LocalDateTime.now()
        );

        location.setUser(user);

        Location savedLocation =
                locationRepository.save(location);

        return convertToDTO(savedLocation);
    }


    // =========================================================
    // USER - GET OWN HISTORY
    // =========================================================

    @Transactional(readOnly = true)
    public List<LocationDTO> getUserHistory(
            String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + username
                        )
                );

        return locationRepository
                .findByUserOrderByTimestampDesc(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================================================
    // ADMIN - GET USER HISTORY
    // =========================================================

    @Transactional(readOnly = true)
    public List<LocationDTO> getLocationHistory(
            Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + userId
                        )
                );

        return locationRepository
                .findByUserOrderByTimestampDesc(user)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================================================
    // ADMIN - GET LATEST LOCATION
    // =========================================================

    @Transactional(readOnly = true)
    public Optional<LocationDTO> getLatestLocation(
            Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + userId
                        )
                );

        return locationRepository
                .findFirstByUserOrderByTimestampDesc(user)
                .map(this::convertToDTO);
    }


    // =========================================================
    // ADMIN - GET LOCATION HISTORY BY DATE RANGE
    // =========================================================

    @Transactional(readOnly = true)
    public List<LocationDTO> getLocationHistoryByRange(
            Long userId,
            LocalDateTime start,
            LocalDateTime end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    "Start and end dates are required"
            );
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + userId
                        )
                );

        return locationRepository
                .findByUserAndTimestampBetweenOrderByTimestampDesc(
                        user,
                        start,
                        end
                )
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================================================
    // ENTITY -> DTO
    // =========================================================

    private LocationDTO convertToDTO(
            Location location) {

        LocationDTO dto = new LocationDTO();

        dto.setLatitude(
                location.getLatitude()
        );

        dto.setLongitude(
                location.getLongitude()
        );

        dto.setUserId(
                location.getUser().getId()
        );

        dto.setUsername(
                location.getUser().getUsername()
        );

        dto.setTimestamp(
                location.getTimestamp().toString()
        );

        return dto;
    }


    // =========================================================
    // LOCATION VALIDATION
    // =========================================================

    private void validateCoordinates(
            Double latitude,
            Double longitude) {

        if (latitude == null ||
                longitude == null) {

            throw new IllegalArgumentException(
                    "Latitude and longitude are required"
            );
        }

        if (latitude < -90 ||
                latitude > 90) {

            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90"
            );
        }

        if (longitude < -180 ||
                longitude > 180) {

            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180"
            );
        }
    }
}