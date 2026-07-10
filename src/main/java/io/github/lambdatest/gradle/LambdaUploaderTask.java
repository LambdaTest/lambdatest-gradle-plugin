package io.github.lambdatest.gradle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;

/**
 * Uploader task class for the LambdaTest Gradle plugin that handles uploading of APK . This task
 * manages the upload of applications and test suites.
 *
 * <p>This task coordinates between {@link AppUploader} and {@link TestSuiteUploader}, to upload the
 * apps to Lmabdatest.
 */
public class LambdaUploaderTask extends DefaultTask {

    private static final Logger logger = Logging.getLogger(LambdaUploaderTask.class);
    private String username;
    private String accessKey;
    private String appFilePath;
    private String testSuiteFilePath;
    private Boolean showUploadProgress;

    @TaskAction
    public void uploadApkToLambdaTest() {
        // Generated after upload of app and test suite
        String appId = null;
        String testSuiteId = null;
        CompletableFuture<String> appIdFuture = null;
        CompletableFuture<String> testSuiteIdFuture = null;

        boolean progressEnabled = showUploadProgress != null && showUploadProgress;

        // Only log to lifecycle if progress is disabled
        if (!progressEnabled) {
            logger.lifecycle("Starting LambdaTest APK Uploader task...");
        }

        if (appFilePath != null) {
            if (!progressEnabled) {
                logger.lifecycle("Uploading app ...");
            }
            AppUploader appUploader =
                    new AppUploader(username, accessKey, appFilePath, progressEnabled);
            appIdFuture = appUploader.uploadAppAsync();
        }

        if (testSuiteFilePath != null) {
            if (!progressEnabled) {
                logger.lifecycle("Uploading test suite ...");
            }
            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(username, accessKey, testSuiteFilePath, progressEnabled);
            testSuiteIdFuture = testSuiteUploader.uploadTestSuiteAsync();
        }

        try {
            if (appIdFuture != null) {
                appId = appIdFuture.join();
            }

            if (testSuiteIdFuture != null) {
                testSuiteId = testSuiteIdFuture.join();
            }

            // Clear progress display if enabled, then show success messages
            if (progressEnabled) {
                ProgressTracker.cleanup();
            }

            // Show success messages (unified flow for both progress and non-progress cases)
            if (appIdFuture != null) {
                logger.lifecycle("\u001B[32mApp uploaded successfully with ID: {}\u001B[0m", appId);
            }
            if (testSuiteIdFuture != null) {
                logger.lifecycle(
                        "\u001B[32mTest suite uploaded successfully with ID: {}\u001B[0m",
                        testSuiteId);
            }
        } catch (CompletionException e) {
            // Cleanup progress display on error
            if (progressEnabled) {
                ProgressTracker.cleanup();
            }
            logger.error("Failed to execute LambdaTest APK Uploader task : {}", e);
            throw new RuntimeException(e);
        }

        if (!progressEnabled) {
            logger.lifecycle("Completed LambdaTest APK Uploader task ...");
        }
    }

    // Setter functions for the task
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
    }

    public void setAccessKey(String accessKey) {
        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Access key cannot be null or empty");
        }
        this.accessKey = accessKey;
    }

    public void setAppFilePath(String appFilePath) {
        if (appFilePath != null && appFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("App file path cannot be empty");
        }
        this.appFilePath = appFilePath;
    }

    public void setTestSuiteFilePath(String testSuiteFilePath) {
        if (testSuiteFilePath != null && testSuiteFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Test suite file path cannot be empty");
        }
        this.testSuiteFilePath = testSuiteFilePath;
    }

    public void setShowUploadProgress(Boolean showUploadProgress) {
        this.showUploadProgress = showUploadProgress;
    }
}
