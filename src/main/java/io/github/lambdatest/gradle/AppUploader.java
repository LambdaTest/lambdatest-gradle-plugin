package io.github.lambdatest.gradle;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppUploader {

    private static final Logger logger = LogManager.getLogger(AppUploader.class);

    private String username;
    private String accessKey;
    private String appFilePath;

    public AppUploader(String username, String accessKey, String appFilePath) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");
        if (accessKey == null) throw new IllegalArgumentException("Access Key cannot be null");
        if (appFilePath == null) throw new IllegalArgumentException("App File Path cannot be null");

        this.username = username;
        this.accessKey = accessKey;
        this.appFilePath = appFilePath;
    }

    public CompletableFuture<String> uploadAppAsync() {
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        String appId =
                                UploaderUtil.uploadAndGetId(username, accessKey, appFilePath);
                        logger.info("Uploaded app ID: {}", appId);
                        return appId;
                    } catch (IOException e) {
                        logger.error("Error uploading app: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }
}
