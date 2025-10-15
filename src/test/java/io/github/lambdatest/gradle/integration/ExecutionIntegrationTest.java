package io.github.lambdatest.gradle.integration;

import static org.junit.jupiter.api.Assertions.*;

import io.github.lambdatest.gradle.TestExecutor;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExecutionIntegrationTest {
    private MockLambdaTestServer mockServer;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockLambdaTestServer();
        mockServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.stop();
    }

    @Test
    void testEspressoTestExecution() throws Exception {
        // Arrange
        String buildId = "BUILD123456789";
        mockServer.enqueueBuildResponse(buildId);

        TestExecutor executor =
                new TestExecutor(
                        "testuser",
                        "testkey",
                        "lt://APP123",
                        "lt://TEST123",
                        Arrays.asList("Pixel 3-9"),
                        false);

        Map<String, String> params = new HashMap<>();
        params.put("build", "Test Build");
        params.put("video", "true");

        // Act
        executor.executeTests(params);

        // Assert
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertTrue(request.getPath().contains("espresso/build"));
        assertNotNull(request.getHeader("Authorization"));
        assertTrue(request.getBody().readUtf8().contains("Test Build"));
    }

    @Test
    void testFlutterTestExecution() throws Exception {
        // Arrange
        String buildId = "FLUTTER_BUILD123456789";
        mockServer.enqueueBuildResponse(buildId);

        TestExecutor executor =
                new TestExecutor(
                        "testuser",
                        "testkey",
                        "lt://APP123",
                        "lt://TEST123",
                        Arrays.asList("Pixel 3-9"),
                        true);

        // Act
        executor.executeTests(new HashMap<>());

        // Assert
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertTrue(request.getPath().contains("flutter/build"));
    }

    @Test
    void testExecutionWithInvalidCredentials() throws Exception {
        // Arrange
        mockServer.enqueueErrorResponse(401, "Invalid credentials");

        TestExecutor executor =
                new TestExecutor(
                        "baduser",
                        "badkey",
                        "lt://APP123",
                        "lt://TEST123",
                        Arrays.asList("Pixel 3-9"),
                        false);

        // Act
        executor.executeTests(new HashMap<>());

        // Assert - Verify request was made even with invalid credentials
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertTrue(request.getPath().contains("espresso/build"));
    }
}
