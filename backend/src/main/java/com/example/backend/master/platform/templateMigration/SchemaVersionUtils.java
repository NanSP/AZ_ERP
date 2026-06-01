package com.example.backend.master.platform.templateMigration;

final class SchemaVersionUtils {

    private SchemaVersionUtils() {
    }

    static String fromFlywayVersion(String version) {
        if (version == null || version.isBlank()) {
            return null;
        }

        return version.startsWith("V") ? version.toUpperCase() : "V" + version.toUpperCase();
    }

    static int compare(String left, String right) {
        return Integer.compare(parse(left), parse(right));
    }

    static int parse(String version) {
        if (version == null || version.isBlank()) {
            return 0;
        }

        return Integer.parseInt(version.replaceFirst("^[Vv]", ""));
    }
}
