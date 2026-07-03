package com.example.backend.security;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class SensitiveDataSanitizer {

    private static final int MAX_COLLECTION_ITEMS = 25;
    private static final int MAX_STRING_LENGTH = 300;
    private static final String REDACTED = "[REDACTED]";

    public Map<String, Object> sanitizeMap(Map<String, Object> value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        Map<String, Object> sanitized = new LinkedHashMap<>();
        int count = 0;

        for (Map.Entry<String, Object> entry : value.entrySet()) {
            if (count >= MAX_COLLECTION_ITEMS) {
                sanitized.put("_truncated", true);
                break;
            }

            String key = entry.getKey();
            sanitized.put(key, sanitizeValue(key, entry.getValue()));
            count++;
        }

        return sanitized;
    }

    public String sanitizeUserAgent(String userAgent) {
        if (userAgent == null) {
            return null;
        }

        String normalized = userAgent.trim();
        if (normalized.isBlank()) {
            return null;
        }

        return normalized.length() > MAX_STRING_LENGTH
                ? normalized.substring(0, MAX_STRING_LENGTH)
                : normalized;
    }

    private Object sanitizeValue(String key, Object value) {
        if (shouldRedact(key)) {
            return REDACTED;
        }

        if (value instanceof Map<?, ?> mapValue) {
            Map<String, Object> normalized = new LinkedHashMap<>();
            int count = 0;

            for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                if (count >= MAX_COLLECTION_ITEMS) {
                    normalized.put("_truncated", true);
                    break;
                }

                String nestedKey = String.valueOf(entry.getKey());
                normalized.put(nestedKey, sanitizeValue(nestedKey, entry.getValue()));
                count++;
            }

            return normalized;
        }

        if (value instanceof List<?> listValue) {
            List<Object> sanitized = new ArrayList<>();

            for (int i = 0; i < listValue.size() && i < MAX_COLLECTION_ITEMS; i++) {
                sanitized.add(sanitizeValue(key, listValue.get(i)));
            }

            if (listValue.size() > MAX_COLLECTION_ITEMS) {
                sanitized.add("[TRUNCATED]");
            }

            return sanitized;
        }

        if (value instanceof String stringValue) {
            String normalized = stringValue.trim();

            if (normalized.length() > MAX_STRING_LENGTH) {
                return normalized.substring(0, MAX_STRING_LENGTH);
            }

            return normalized;
        }

        return value;
    }

    private boolean shouldRedact(String key) {
        if (key == null) {
            return false;
        }

        String normalized = key.toLowerCase(Locale.ROOT);

        return normalized.contains("senha")
                || normalized.contains("password")
                || normalized.contains("token")
                || normalized.contains("secret")
                || normalized.contains("authorization")
                || normalized.contains("cookie")
                || normalized.contains("cpf")
                || normalized.contains("cnpj")
                || normalized.contains("documento")
                || normalized.contains("push")
                || normalized.contains("credential");
    }
}
