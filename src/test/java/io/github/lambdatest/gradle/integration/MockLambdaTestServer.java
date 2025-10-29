package io.github.lambdatest.gradle.integration;

import io.github.lambdatest.gradle.Constants;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/** Utility class for setting up MockWebServer to simulate LambdaTest API responses. */
public class MockLambdaTestServer {
    private MockWebServer server;
    private String baseUrl;

    public void start() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = server.url("/").toString();

        // Configure Constants to use mock URLs
        Constants.setTestUrls(
                baseUrl + "app/uploadFramework",
                baseUrl + "framework/v1/espresso/build",
                baseUrl + "framework/v1/flutter/build");
    }

    public void stop() throws IOException {
        if (server != null) {
            server.shutdown();
            Constants.resetUrls();
        }
    }

    public void enqueueUploadResponse(String appId) {
        server.enqueue(
                new MockResponse().setResponseCode(200).setBody("{\"app_id\":\"" + appId + "\"}"));
    }

    public void enqueueBuildResponse(String buildId) {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody("{\"build_id\":\"" + buildId + "\",\"status\":\"success\"}"));
    }

    public void enqueueErrorResponse(int code, String message) {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(code)
                        .setBody("{\"error\":\"" + message + "\"}"));
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
