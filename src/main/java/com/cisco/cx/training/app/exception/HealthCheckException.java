package com.cisco.cx.training.app.exception;

import java.util.Map;

@SuppressWarnings("serial")
public class HealthCheckException extends Exception {
    private final Map<String, String> healthStatus;

    public HealthCheckException(Map<String, String> healthStatus) {
        this.healthStatus = healthStatus;
    }

    public Map<String, String> getHealthStatus() { return healthStatus; }
}
