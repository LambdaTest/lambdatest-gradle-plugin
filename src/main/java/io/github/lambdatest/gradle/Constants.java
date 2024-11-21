package io.github.lambdatest.gradle;

/**
 * Constants used throughout the LambdaTest Gradle plugin. This utility class provides centralized
 * access to API endpoints and other constant values.
 */
public class Constants {
    /** Private constructor to prevent instantiation of this utility class. */
    private Constants() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static final String API_URL = "https://manual-api.lambdatest.com/app/uploadFramework";
    public static final String BUILD_URL =
            "https://mobile-api.lambdatest.com/framework/v1/espresso/build";
    public static final String FLUTTER_BUILD_URL =
            "https://mobile-api.lambdatest.com/framework/v1/flutter/build";
}
