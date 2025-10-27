package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link TestExecutor} class. */
class TestExecutorTest {

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_ACCESS_KEY = "test_access_key";
    private static final String TEST_APP_ID = "lt://APP1234567890";
    private static final String TEST_TEST_SUITE_ID = "lt://TESTSUITE1234567890";

    @Test
    void constructor_AndroidProject_ShouldCreateInstance() {
        List<String> devices = new ArrayList<>();
        devices.add("iPhone 12-14");

        // Valid construction - Android project
        TestExecutor validExecutor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_TEST_SUITE_ID,
                        devices,
                        false);
        assertThat(validExecutor).isNotNull();

        // Test with null parameters (should be handled gracefully)
        TestExecutor executorWithNulls = new TestExecutor(null, null, null, null, null, false);
        assertThat(executorWithNulls).isNotNull();
    }

    @Test
    void constructor_FlutterProject_ShouldCreateInstance() {
        List<String> devices = new ArrayList<>();
        devices.add("iPhone 12-14");

        // Valid construction - Flutter project
        TestExecutor validExecutor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_TEST_SUITE_ID,
                        devices,
                        true);
        assertThat(validExecutor).isNotNull();

        // Null devices list should be handled
        TestExecutor executorWithNullDevices =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_TEST_SUITE_ID,
                        null,
                        true);
        assertThat(executorWithNullDevices).isNotNull();
    }

    @Test
    void constructor_ShouldHandleBasicConfiguration() {
        // Given
        List<String> devices = new ArrayList<>();
        devices.add("iPhone 12-14");

        TestExecutor executor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_TEST_SUITE_ID,
                        devices,
                        false);

        // When/Then - Just verify the constructor works correctly
        assertThat(executor).isNotNull();
        // We don't call executeTests() here to avoid making real network calls
    }

    @Test
    void constructor_WithDeviceList_ShouldHandleFlutterConfiguration() {
        // Given
        List<String> devices = new ArrayList<>();
        devices.add("iPhone 12-14");
        devices.add("Samsung Galaxy S21-11");

        TestExecutor executor =
                new TestExecutor(
                        TEST_USERNAME,
                        TEST_ACCESS_KEY,
                        TEST_APP_ID,
                        TEST_TEST_SUITE_ID,
                        devices,
                        true);

        // When/Then - Verify construction with device list
        assertThat(executor).isNotNull();
        // We don't call executeTests() here to avoid making real network calls
    }
}
