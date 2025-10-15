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

    private static final String DEFAULT_API_URL =
            "https://manual-api.lambdatest.com/app/uploadFramework";
    private static final String DEFAULT_BUILD_URL =
            "https://mobile-api.lambdatest.com/framework/v1/espresso/build";
    private static final String DEFAULT_FLUTTER_BUILD_URL =
            "https://mobile-api.lambdatest.com/framework/v1/flutter/build";

    // For testing purposes - allows URL override
    private static String testApiUrl = null;
    private static String testBuildUrl = null;
    private static String testFlutterBuildUrl = null;

    public static String getApiUrl() {
        return testApiUrl != null ? testApiUrl : DEFAULT_API_URL;
    }

    public static String getBuildUrl() {
        return testBuildUrl != null ? testBuildUrl : DEFAULT_BUILD_URL;
    }

    public static String getFlutterBuildUrl() {
        return testFlutterBuildUrl != null ? testFlutterBuildUrl : DEFAULT_FLUTTER_BUILD_URL;
    }

    // Public methods for testing
    public static void setTestUrls(String apiUrl, String buildUrl, String flutterBuildUrl) {
        testApiUrl = apiUrl;
        testBuildUrl = buildUrl;
        testFlutterBuildUrl = flutterBuildUrl;
    }

    public static void resetUrls() {
        testApiUrl = null;
        testBuildUrl = null;
        testFlutterBuildUrl = null;
    }

    // Backward compatibility - deprecated
    @Deprecated public static final String API_URL = DEFAULT_API_URL;
    @Deprecated public static final String BUILD_URL = DEFAULT_BUILD_URL;
    @Deprecated public static final String FLUTTER_BUILD_URL = DEFAULT_FLUTTER_BUILD_URL;
}
