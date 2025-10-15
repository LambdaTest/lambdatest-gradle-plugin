package io.github.lambdatest.gradle;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Arrays;
import java.util.List;

/** Test configuration helper that loads credentials from .env file. */
public class TestConfig {

    private static final Dotenv dotenv;

    static {
        // Try to load .env file, but don't fail if it doesn't exist
        dotenv =
                Dotenv.configure()
                        .directory(System.getProperty("user.dir"))
                        .ignoreIfMissing()
                        .load();
    }

    /**
     * Get test username from environment or .env file.
     *
     * @return test username
     */
    public static String getUsername() {
        return getEnvOrDefault("TEST_USERNAME", "testuser");
    }

    /**
     * Get test access key from environment or .env file.
     *
     * @return test access key
     */
    public static String getAccessKey() {
        return getEnvOrDefault("TEST_ACCESS_KEY", "test_access_key_placeholder");
    }

    /**
     * Get test app file path from environment or .env file.
     *
     * @return test app file path
     */
    public static String getAppFilePath() {
        return getEnvOrDefault("TEST_APP_FILE_PATH", "./sample-app.apk");
    }

    /**
     * Get test suite file path from environment or .env file.
     *
     * @return test suite file path
     */
    public static String getTestSuiteFilePath() {
        return getEnvOrDefault("TEST_SUITE_FILE_PATH", "./sample-test.apk");
    }

    /**
     * Get test app ID from environment or .env file.
     *
     * @return test app ID
     */
    public static String getAppId() {
        return getEnvOrDefault("TEST_APP_ID", "lt://APP1016043531760091916696614");
    }

    /**
     * Get test suite ID from environment or .env file.
     *
     * @return test suite ID
     */
    public static String getTestSuiteId() {
        return getEnvOrDefault("TEST_SUITE_ID", "lt://APP1016043531760091916696615");
    }

    /**
     * Get test devices list from environment or .env file.
     *
     * @return list of test devices
     */
    public static List<String> getDevices() {
        String devicesStr = getEnvOrDefault("TEST_DEVICES", "Pixel 6-12,Galaxy S21-11");
        return Arrays.asList(devicesStr.split(","));
    }

    /**
     * Helper method to get environment variable or default value.
     *
     * @param key environment variable key
     * @param defaultValue default value if not found
     * @return environment variable value or default
     */
    private static String getEnvOrDefault(String key, String defaultValue) {
        // First check system environment
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        // Then check .env file
        if (dotenv != null) {
            value = dotenv.get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }

        // Return default value
        return defaultValue;
    }
}
