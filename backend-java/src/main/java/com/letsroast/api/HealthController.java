package com.letsroast.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Manual health-check controller for basic API uptime checks.
 *
 * <p>Routes in this class are prefixed with /api.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Handles GET /api/health and reports a simple "ok" status.
     *
     * @return JSON payload indicating the application is reachable
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
