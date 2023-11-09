package io.github.lambdatest.gradle;

import okhttp3.*;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.concurrent.CompletableFuture;

public class TestSuiteUploader {

    private String username;
    private String accessKey;
    private String testSuiteFilePath;

    public TestSuiteUploader(String username, String accessKey, String testSuiteFilePath) {
        this.username = username;
        this.accessKey = accessKey;
        this.testSuiteFilePath = testSuiteFilePath;
    }

    public CompletableFuture<String> uploadTestSuiteAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("appFile", testSuiteFilePath,
                                RequestBody.create(mediaType, new java.io.File(testSuiteFilePath)))
                        .addFormDataPart("type", "espresso-android")
                        .build();
                Request request = new Request.Builder()
                        .url(Constants.API_URL)
                        .method("POST", body)
                        .addHeader("Authorization", Credentials.basic(username, accessKey))
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    String responseBody = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                    String testSuiteId = jsonObject.get("app_id").getAsString();

                    System.out.println("Uploaded test suite ID : " + testSuiteId);
                    return testSuiteId;
                }
            } catch (IOException e) {
                System.err.println("Error uploading test suite app: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
}
