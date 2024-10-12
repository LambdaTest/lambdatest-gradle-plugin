package io.github.lambdatest.gradle;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestSuiteUploader {

    private static final Logger logger = LogManager.getLogger(TestSuiteUploader.class);

    private String username;
    private String accessKey;
    private String testSuiteFilePath;

    public TestSuiteUploader(String username, String accessKey, String testSuiteFilePath) {
        this.username = username;
        this.accessKey = accessKey;
        this.testSuiteFilePath = testSuiteFilePath;
    }

    public CompletableFuture<String> uploadTestSuiteAsync() {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        String testSuiteId =
                                UploaderUtil.uploadAndGetId(username, accessKey, testSuiteFilePath);
                        logger.info("Uploaded test suite ID: {}", testSuiteId);
                        return testSuiteId;
                    } catch (IOException e) {
                        logger.error("Error uploading test suite app: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }
}
