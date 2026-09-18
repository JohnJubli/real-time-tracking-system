package com.tracking.backend.controller;

import com.tracking.backend.dto.LocationDTO;
import com.tracking.backend.service.LocationService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class HistoryController {

    private final LocationService locationService;

    public HistoryController(
            LocationService locationService) {

        this.locationService = locationService;
    }


    // =========================================================
    // ADMIN - GET USER HISTORY
    // =========================================================

    @GetMapping("/history/{userId}")
    public List<LocationDTO> getHistory(
            @PathVariable Long userId) {

        return locationService
                .getLocationHistory(userId);
    }


    // =========================================================
    // ADMIN - GET LATEST LOCATION
    // =========================================================

    @GetMapping("/latest/{userId}")
    public ResponseEntity<LocationDTO> getLatestLocation(
            @PathVariable Long userId) {

        return locationService
                .getLatestLocation(userId)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }


    // =========================================================
    // ADMIN - GET HISTORY BY DATE RANGE
    // =========================================================

    @GetMapping("/history/{userId}/range")
    public List<LocationDTO> getLocationRange(

            @PathVariable Long userId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime end) {

        return locationService
                .getLocationHistoryByRange(
                        userId,
                        start,
                        end
                );
    }
}