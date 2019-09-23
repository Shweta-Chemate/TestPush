package com.cisco.services.app.exception;

import java.util.Map;

public class HealthCheckException extends Exception {
    private final Map<String, String> healthStatus;

    public HealthCheckException(Map<String, String> healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Map<String, String> getHealthStatus() { return healthStatus; }
}
