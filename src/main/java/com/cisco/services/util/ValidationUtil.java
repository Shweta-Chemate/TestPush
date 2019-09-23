package com.cisco.services.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private final static Logger LOG = LoggerFactory.getLogger(ValidationUtil.class);

    private ValidationUtil() { }

    public static boolean isValidPageNo(String pageNo, Long minVal) throws IllegalArgumentException {
        Long page = null;

        try {
            page = validatedLong(pageNo);
        } catch (NumberFormatException e) {
            LOG.error("Could not parse page number " + pageNo + " as number");
        } finally {
            return (page == null || page >= minVal);
        }
    }

    public static boolean isValidNumber(String number) {
        try {
            validatedLong(number);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean isValidPattern(String input, String regex, boolean allowEmpty) {
        boolean isValid;

        if (StringUtils.isNotBlank(input)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            isValid = matcher.matches();
        } else {
            isValid = allowEmpty;
        }

        return isValid;
    }

    public static Long validatedLong(String longValue) throws NumberFormatException {
        Long validated = null;
        if (StringUtils.isNotBlank(longValue)) {
            validated = Long.parseLong(longValue.trim());
        }

        return validated;
    }

    public static void checkHealth(Map<String, String> healthStatus, Map<String, Callable<Boolean>> dependencies) {
        dependencies.forEach((source, healthCheckFn)-> {
            try {
                healthStatus.put(source, healthCheckFn.call() ? "OK" : "DOWN");
            } catch (Throwable e) {
                LOG.error("Error calling HealthCheck for " + source, e);
                healthStatus.put(source, "UNKNOWN");
            }
        });
    }
}
