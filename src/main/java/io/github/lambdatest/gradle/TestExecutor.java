package io.github.lambdatest.gradle;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class TestExecutor {
    private static final Logger logger = LogManager.getLogger(TestExecutor.class);

    private String username;
    private String accessKey;
    private String appId;
    private String testSuiteId;
    private List<String> device;
    private Boolean isFlutter;

    public TestExecutor(String username, String accessKey, String appId, String testSuiteId, List<String> device, Boolean isFlutter) {
        this.username = username;
        this.accessKey = accessKey;
        this.appId = appId;
        this.testSuiteId = testSuiteId;
        this.device = device;
        this.isFlutter = isFlutter;
    }

    public void executeTests(Map<String, String> params) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");

            String devicesJsonArray = device.stream()
            .map(device -> "\"" + device + "\"")
            .collect(Collectors.joining(",", "[", "]"));

            StringBuilder jsonBodyBuilder = new StringBuilder(String.format("{\n"
                    + "    \"app\" : \"%s\",\n"
                    + "    \"testSuite\": \"%s\",\n"
                    + "    \"device\" :  %s", appId, testSuiteId, devicesJsonArray));


            if (params.get("build") != null) jsonBodyBuilder.append(String.format(",\n    \"build\": \"%s\"", params.get("build")));
            if (params.get("deviceLog") != null) jsonBodyBuilder.append(String.format(",\n    \"deviceLog\": %s", params.get("deviceLog")));
            if (params.get("IdleTimeout") != null) jsonBodyBuilder.append(String.format(",\n    \"IdleTimeout\": %s", params.get("IdleTimeout")));
            if (params.get("queueTimeout") != null) jsonBodyBuilder.append(String.format(",\n    \"queueTimeout\": %s", params.get("queueTimeout")));
            if (params.get("video") != null) jsonBodyBuilder.append(String.format(",\n    \"video\": %s", params.get("video")));
            if (params.get("network") != null) jsonBodyBuilder.append(String.format(",\n    \"network\": %s", params.get("network")));
            if (params.get("tunnel") != null) jsonBodyBuilder.append(String.format(",\n    \"tunnel\": %s", params.get("tunnel")));
            if (params.get("tunnelName") != null) jsonBodyBuilder.append(String.format(",\n    \"tunnelName\": \"%s\"", params.get("tunnelName")));
            if (params.get("geoLocation") != null) jsonBodyBuilder.append(String.format(",\n    \"geoLocation\": \"%s\"", params.get("geoLocation")));
            if (params.get("fixedIp") != null) jsonBodyBuilder.append(String.format(",\n    \"fixedIp\": \"%s\"", params.get("fixedIp")));
            if (params.get("globalHttpProxy") != null) jsonBodyBuilder.append(String.format(",\n    \"globalHttpProxy\": %s", params.get("globalHttpProxy")));
            if (params.get("singleRunnerInvocation") != null) jsonBodyBuilder.append(String.format(",\n    \"singleRunnerInvocation\": %s", params.get("singleRunnerInvocation")));
            if (params.get("clearPackageData") != null) jsonBodyBuilder.append(String.format(",\n    \"clearPackageData\": %s", params.get("clearPackageData")));
            if (params.get("disableAnimation") != null) jsonBodyBuilder.append(String.format(",\n    \"disableAnimation\": %s", params.get("disableAnimation")));

            jsonBodyBuilder.append("\n}");
            logger.info("Capabilities: {}", jsonBodyBuilder);

            String url = (isFlutter == null || !isFlutter) ? Constants.BUILD_URL : Constants.FLUTTER_BUILD_URL;
            RequestBody body = RequestBody.create(mediaType, jsonBodyBuilder.toString());

            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Authorization", Credentials.basic(username, accessKey))
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();

            logger.info("Running Tests");
            logger.info(response.body().string());
        } catch (IOException e) {
            logger.error("Error executing tests: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
