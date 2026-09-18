package com.tracking.backend.controller;

import com.tracking.backend.dto.LocationDTO;
import com.tracking.backend.dto.LocationRequest;
import com.tracking.backend.service.LocationService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class LocationController {

    private final LocationService locationService;

    public LocationController(
            LocationService locationService) {

        this.locationService = locationService;
    }


    // =========================================================
    // USER - UPDATE CURRENT LOCATION
    // =========================================================

    @PostMapping("/location")
    public LocationDTO updateLocation(
            @RequestBody LocationRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        LocationDTO locationDTO =
                new LocationDTO(
                        request.getLatitude(),
                        request.getLongitude()
                );

        return locationService.saveLocation(
                username,
                locationDTO
        );
    }


    // =========================================================
    // USER - GET OWN LOCATION HISTORY
    // =========================================================

    @GetMapping("/history")
    public List<LocationDTO> getHistory(
            Authentication authentication) {

        String username = authentication.getName();

        return locationService.getUserHistory(
                username
        );
    }
}