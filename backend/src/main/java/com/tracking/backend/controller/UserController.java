package com.tracking.backend.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public String userProfile() {
        return "Welcome USER";
    }

    @GetMapping("/locations")
    public String userLocations() {
        return "User location data";
    }
}