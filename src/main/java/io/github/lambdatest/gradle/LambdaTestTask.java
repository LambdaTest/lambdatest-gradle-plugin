package io.github.lambdatest.gradle;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Main task class for the LambdaTest Gradle plugin that handles test execution on the LambdaTest
 * platform. This task manages the upload of applications and test suites, followed by test
 * execution with specified configurations.
 *
 * <p>This task coordinates between {@link AppUploader}, {@link TestSuiteUploader}, and {@link
 * TestExecutor} to manage the complete test execution lifecycle.
 */
public class LambdaTestTask extends DefaultTask {

    private static final Logger logger = LogManager.getLogger(LambdaTestTask.class);

    private String username;
    private String accessKey;
    private String appFilePath;
    private String testSuiteFilePath;
    private List<String> device;
    private String build;
    private Boolean deviceLog;
    private Integer idleTimeout;
    private Boolean video;
    private Boolean network;
    private Boolean tunnel;
    private String tunnelName;
    private String geoLocation;
    private Boolean disableAnimation;
    private Boolean clearPackageData;
    private Boolean singleRunnerInvocation;
    private Boolean globalHttpProxy;
    private String fixedIp;
    private Boolean isFlutter;
    private String appId;
    private String testSuiteId;
    private Integer queueTimeout;
    private Boolean showUploadProgress;

    /**
     * Executes the LambdaTest task, which includes uploading the application and test suite,
     * followed by test execution on the LambdaTest platform.
     *
     * @implNote This method handles the task execution in three main phases: 1. Asynchronous upload
     *     of the application using {@link AppUploader#uploadAppAsync()} 2. Asynchronous upload of
     *     the test suite using {@link TestSuiteUploader#uploadTestSuiteAsync()} 3. Test execution
     *     with {@link TestExecutor#executeTests(Map)}
     * @throws RuntimeException if any upload or test execution fails
     */
    @TaskAction
    public void runLambdaTest() {
        boolean progressEnabled = showUploadProgress != null && showUploadProgress;

        if (!progressEnabled) {
            logger.info("Starting LambdaTest task...");
        }

        // Upload app
        CompletableFuture<String> appIdFuture = null;
        CompletableFuture<String> testSuiteIdFuture = null;

        if (appId == null && appFilePath != null) {
            if (!progressEnabled) {
                logger.info("Uploading app...");
            }
            AppUploader appUploader =
                    new AppUploader(username, accessKey, appFilePath, progressEnabled);
            appIdFuture = appUploader.uploadAppAsync();
        }

        if (testSuiteId == null && testSuiteFilePath != null) {
            if (!progressEnabled) {
                logger.info("Uploading test suite...");
            }
            TestSuiteUploader testSuiteUploader =
                    new TestSuiteUploader(username, accessKey, testSuiteFilePath, progressEnabled);
            testSuiteIdFuture = testSuiteUploader.uploadTestSuiteAsync();
        }

        // Ensure both uploads are completed before continuing
        try {
            if (appIdFuture != null) {
                appId = appIdFuture.join();
                if (!progressEnabled) {
                    logger.info("App uploaded successfully with ID: {}", appId);
                }
            }

            if (testSuiteIdFuture != null) {
                testSuiteId = testSuiteIdFuture.join();
                if (!progressEnabled) {
                    logger.info("Test suite uploaded successfully with ID: {}", testSuiteId);
                }
            }

            // Clear progress display if enabled
            if (progressEnabled) {
                ProgressTracker.cleanup();
                // Show success messages after progress cleanup
                if (appIdFuture != null) {
                    logger.info("App uploaded successfully with ID: {}", appId);
                }
                if (testSuiteIdFuture != null) {
                    logger.info("Test suite uploaded successfully with ID: {}", testSuiteId);
                }
            }
        } catch (CompletionException e) {
            // Cleanup progress display on error
            if (progressEnabled) {
                ProgressTracker.cleanup();
            }
            logger.error("Failed to execute tasks: {}", e);
            throw new RuntimeException(e);
        }

        // Execute tests
        logger.info("Executing tests...");
        TestExecutor testExecutor =
                new TestExecutor(username, accessKey, appId, testSuiteId, device, isFlutter);
        Map<String, String> params = new HashMap<>();

        if (build != null) params.put("build", build);
        if (deviceLog != null) params.put("deviceLog", deviceLog.toString());
        if (idleTimeout != null) params.put("IdleTimeout", idleTimeout.toString());
        if (video != null) params.put("video", video.toString());
        if (network != null) params.put("network", network.toString());
        if (tunnel != null) params.put("tunnel", tunnel.toString());
        if (tunnelName != null) params.put("tunnelName", tunnelName);
        if (geoLocation != null) params.put("geoLocation", geoLocation);
        if (disableAnimation != null) params.put("disableAnimation", disableAnimation.toString());
        if (clearPackageData != null) params.put("clearPackageData", clearPackageData.toString());
        if (singleRunnerInvocation != null)
            params.put("singleRunnerInvocation", singleRunnerInvocation.toString());
        if (globalHttpProxy != null) params.put("globalHttpProxy", globalHttpProxy.toString());
        if (fixedIp != null) params.put("fixedIp", fixedIp);
        if (queueTimeout != null) params.put("queueTimeout", queueTimeout.toString());

        try {
            testExecutor.executeTests(params);
        } catch (IOException e) {
            logger.error("Failed to execute tests: {}", e);
            throw new RuntimeException(e);
        }
        logger.info("LambdaTest task completed.");
    }

    // setter methods for the properties

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setAppFilePath(String appFilePath) {
        this.appFilePath = appFilePath;
    }

    public void setTestSuiteFilePath(String testSuiteFilePath) {
        this.testSuiteFilePath = testSuiteFilePath;
    }

    public void setDevice(List<String> device) {
        this.device = device;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public void setDeviceLog(Boolean deviceLog) {
        this.deviceLog = deviceLog;
    }

    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public void setNetwork(Boolean network) {
        this.network = network;
    }

    public void setTunnel(Boolean tunnel) {
        this.tunnel = tunnel;
    }

    public void setTunnelName(String tunnelName) {
        this.tunnelName = tunnelName;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public void setDisableAnimation(Boolean disableAnimation) {
        this.disableAnimation = disableAnimation;
    }

    public void setClearPackageData(Boolean clearPackageData) {
        this.clearPackageData = clearPackageData;
    }

    public void setSingleRunnerInvocation(Boolean singleRunnerInvocation) {
        this.singleRunnerInvocation = singleRunnerInvocation;
    }

    public void setGlobalHttpProxy(Boolean globalHttpProxy) {
        this.globalHttpProxy = globalHttpProxy;
    }

    public void setFixedIp(String fixedIp) {
        this.fixedIp = fixedIp;
    }

    public void setQueueTimeout(Integer queueTimeout) {
        this.queueTimeout = queueTimeout;
    }

    public void setIsFlutter(Boolean isFlutter) {
        this.isFlutter = (isFlutter != null && isFlutter);
    }

    public void setAppId(String appId) {
        if (appId != null && !appId.trim().isEmpty()) {
            this.appId = appId;
        }
    }

    public void setTestSuiteId(String testSuiteId) {
        if (testSuiteId != null && !testSuiteId.trim().isEmpty()) {
            this.testSuiteId = testSuiteId;
        }
    }

    public void setShowUploadProgress(Boolean showUploadProgress) {
        this.showUploadProgress = showUploadProgress;
    }
}
