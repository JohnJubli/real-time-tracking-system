package com.tracking.backend.controller;

import com.tracking.backend.dto.LocationDTO;
import com.tracking.backend.service.LocationService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketLocationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LocationService locationService;

    public WebSocketLocationController(
            SimpMessagingTemplate messagingTemplate,
            LocationService locationService) {

        this.messagingTemplate = messagingTemplate;
        this.locationService = locationService;
    }

    /**
     * Tracker sends location to:
     *
     * /app/sendLocation
     */
    @MessageMapping("/sendLocation")
    public void receiveLocation(
            LocationDTO dto,
            Principal principal) {

        System.out.println(
                "========================================"
        );

        System.out.println(
                "WebSocket location message received"
        );

        /*
         * Principal must exist because the
         * STOMP CONNECT was authenticated.
         */
        if (principal == null) {

            System.out.println(
                    "ERROR: WebSocket Principal is null"
            );

            throw new IllegalStateException(
                    "Unauthenticated WebSocket session"
            );
        }

        /*
         * Validate incoming DTO.
         */
        if (dto == null) {

            throw new IllegalArgumentException(
                    "Location data cannot be null"
            );
        }

        if (dto.getLatitude() == null ||
                dto.getLongitude() == null) {

            throw new IllegalArgumentException(
                    "Latitude and longitude are required"
            );
        }

        System.out.println(
                "Authenticated user: "
                        + principal.getName()
        );

        System.out.println(
                "Latitude: "
                        + dto.getLatitude()
        );

        System.out.println(
                "Longitude: "
                        + dto.getLongitude()
        );

        /*
         * Save location using the authenticated
         * username, not a username supplied
         * by the browser.
         */
        LocationDTO savedLocation =
                locationService.saveLocation(
                        principal.getName(),
                        dto
                );

        System.out.println(
                "Location saved successfully"
        );

        System.out.println(
                "Database user ID: "
                        + savedLocation.getUserId()
        );

        /*
         * Broadcast saved location to every
         * subscriber of /topic/locations.
         */
        System.out.println(
                "Broadcasting location to /topic/locations"
        );

        messagingTemplate.convertAndSend(
                "/topic/locations",
                savedLocation
        );

        System.out.println(
                "Location broadcast completed"
        );

        System.out.println(
                "========================================"
        );
    }
}