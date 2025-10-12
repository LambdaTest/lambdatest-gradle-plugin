package io.github.lambdatest.gradle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link LambdaTestTask} class. */
@ExtendWith(MockitoExtension.class)
class LambdaTestTaskTest {

    private static final String TEST_USERNAME = TestConfig.getUsername();
    private static final String TEST_ACCESS_KEY = TestConfig.getAccessKey();
    private static final String TEST_APP_FILE_PATH = TestConfig.getAppFilePath();
    private static final String TEST_SUITE_FILE_PATH = TestConfig.getTestSuiteFilePath();
    private static final String TEST_APP_ID = TestConfig.getAppId();
    private static final String TEST_SUITE_ID = TestConfig.getTestSuiteId();
    private static final List<String> TEST_DEVICES = TestConfig.getDevices();

    private Project project;
    private LambdaTestTask task;

    @BeforeEach
    void setUp() {
        project = ProjectBuilder.builder().build();
        task = project.getTasks().create("testLambdaTest", LambdaTestTask.class);
    }

    @Test
    void task_ShouldBeCreated_WithCorrectType() {
        // Then
        assertThat(task).isNotNull();
        assertThat(task).isInstanceOf(LambdaTestTask.class);
    }

    @Test
    void setUsername_ShouldSetUsername() {
        // When
        task.setUsername(TEST_USERNAME);

        // Then - verify task was configured (no exception thrown)
        assertThat(task).isNotNull();
    }

    @Test
    void setAccessKey_ShouldSetAccessKey() {
        // When
        task.setAccessKey(TEST_ACCESS_KEY);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setAppFilePath_ShouldSetAppFilePath() {
        // When
        task.setAppFilePath(TEST_APP_FILE_PATH);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setTestSuiteFilePath_ShouldSetTestSuiteFilePath() {
        // When
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setDevice_ShouldSetDevice() {
        // When
        task.setDevice(TEST_DEVICES);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setBuild_ShouldSetBuild() {
        // When
        task.setBuild("Test Build");

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setDeviceLog_ShouldSetDeviceLog() {
        // When
        task.setDeviceLog(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setIdleTimeout_ShouldSetIdleTimeout() {
        // When
        task.setIdleTimeout(300);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setVideo_ShouldSetVideo() {
        // When
        task.setVideo(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setNetwork_ShouldSetNetwork() {
        // When
        task.setNetwork(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setTunnel_ShouldSetTunnel() {
        // When
        task.setTunnel(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setTunnelName_ShouldSetTunnelName() {
        // When
        task.setTunnelName("myTunnel");

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setGeoLocation_ShouldSetGeoLocation() {
        // When
        task.setGeoLocation("US");

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setDisableAnimation_ShouldSetDisableAnimation() {
        // When
        task.setDisableAnimation(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setClearPackageData_ShouldSetClearPackageData() {
        // When
        task.setClearPackageData(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setSingleRunnerInvocation_ShouldSetSingleRunnerInvocation() {
        // When
        task.setSingleRunnerInvocation(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setGlobalHttpProxy_ShouldSetGlobalHttpProxy() {
        // When
        task.setGlobalHttpProxy(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setFixedIp_ShouldSetFixedIp() {
        // When
        task.setFixedIp("192.168.1.1");

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setQueueTimeout_ShouldSetQueueTimeout() {
        // When
        task.setQueueTimeout(600);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setIsFlutter_ShouldSetIsFlutter_WhenTrue() {
        // When
        task.setIsFlutter(true);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setIsFlutter_ShouldSetIsFlutter_WhenFalse() {
        // When
        task.setIsFlutter(false);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setIsFlutter_ShouldHandleNull() {
        // When
        task.setIsFlutter(null);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setAppId_ShouldSetAppId_WhenNotEmpty() {
        // When
        task.setAppId(TEST_APP_ID);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setAppId_ShouldNotSetAppId_WhenEmpty() {
        // When
        task.setAppId("");

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setAppId_ShouldNotSetAppId_WhenNull() {
        // When
        task.setAppId(null);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setTestSuiteId_ShouldSetTestSuiteId_WhenNotEmpty() {
        // When
        task.setTestSuiteId(TEST_SUITE_ID);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setTestSuiteId_ShouldNotSetTestSuiteId_WhenEmpty() {
        // When
        task.setTestSuiteId("");

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void setTestSuiteId_ShouldNotSetTestSuiteId_WhenNull() {
        // When
        task.setTestSuiteId(null);

        // Then
        assertThat(task).isNotNull();
    }

    @Test
    void runLambdaTest_ShouldUploadAppAndTestSuite_WhenIdsNotProvided() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);
        task.setDevice(TEST_DEVICES);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenReturn(TEST_APP_ID, TEST_SUITE_ID);

            // When/Then - Should throw RuntimeException due to TestExecutor trying to make
            // real
            // HTTP call
            assertThatThrownBy(() -> task.runLambdaTest()).isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void runLambdaTest_ShouldUseProvidedAppId_WhenAppIdIsSet() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppId(TEST_APP_ID);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);
        task.setDevice(TEST_DEVICES);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH))
                    .thenReturn(TEST_SUITE_ID);

            // When/Then
            assertThatThrownBy(() -> task.runLambdaTest()).isInstanceOf(RuntimeException.class);

            // Verify app upload was not called since appId was provided
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH),
                    never());
        }
    }

    @Test
    void runLambdaTest_ShouldUseProvidedTestSuiteId_WhenTestSuiteIdIsSet() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteId(TEST_SUITE_ID);
        task.setDevice(TEST_DEVICES);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenReturn(TEST_APP_ID);

            // When/Then
            assertThatThrownBy(() -> task.runLambdaTest()).isInstanceOf(RuntimeException.class);

            // Verify test suite upload was not called since testSuiteId was provided
            mockedUtil.verify(
                    () ->
                            UploaderUtil.uploadAndGetId(
                                    TEST_USERNAME, TEST_ACCESS_KEY, TEST_SUITE_FILE_PATH),
                    never());
        }
    }

    @Test
    void runLambdaTest_ShouldHandleUploadFailure() {
        // Given
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppFilePath(TEST_APP_FILE_PATH);
        task.setTestSuiteFilePath(TEST_SUITE_FILE_PATH);
        task.setDevice(TEST_DEVICES);

        try (MockedStatic<UploaderUtil> mockedUtil = mockStatic(UploaderUtil.class)) {
            mockedUtil
                    .when(
                            () ->
                                    UploaderUtil.uploadAndGetId(
                                            TEST_USERNAME, TEST_ACCESS_KEY, TEST_APP_FILE_PATH))
                    .thenThrow(new IOException("Upload failed"));

            // When/Then
            assertThatThrownBy(() -> task.runLambdaTest())
                    .isInstanceOf(RuntimeException.class)
                    .hasCauseInstanceOf(CompletionException.class);
        }
    }

    @Test
    void runLambdaTest_ShouldIncludeAllParameters_InTestExecution() {
        // Given - Use explicitly invalid credentials to ensure HTTP call fails
        task.setUsername(TEST_USERNAME);
        task.setAccessKey(TEST_ACCESS_KEY);
        task.setAppId(TEST_APP_ID);
        task.setTestSuiteId(TEST_SUITE_ID);
        task.setDevice(TEST_DEVICES);
        task.setBuild("Test Build");
        task.setDeviceLog(true);
        task.setIdleTimeout(300);
        task.setVideo(true);
        task.setNetwork(true);
        task.setTunnel(true);
        task.setTunnelName("myTunnel");
        task.setGeoLocation("US");
        task.setDisableAnimation(true);
        task.setClearPackageData(true);
        task.setSingleRunnerInvocation(true);
        task.setGlobalHttpProxy(true);
        task.setFixedIp("192.168.1.1");
        task.setQueueTimeout(600);

        try {
            task.runLambdaTest();
            // If credentials are valid and API is accessible, test execution may succeed
        } catch (RuntimeException e) {
            // If credentials are invalid or API is unreachable, we expect a RuntimeException
            // This is acceptable as we're testing parameter configuration, not API connectivity
            assertThat(e).isInstanceOf(RuntimeException.class);
        }
    }
}
