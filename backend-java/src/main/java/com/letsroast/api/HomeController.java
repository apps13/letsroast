package com.letsroast.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Root controller for a simple sanity-check endpoint.
 *
 * <p>This is a lightweight landing route so opening the base URL confirms the backend is up.
 */
@RestController
public class HomeController {

    /**
     * Handles GET / and returns a short status message.
     *
     * @return JSON payload with a friendly backend status message
     */
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of("message", "LetsRoast backend is working.");
    }
}
