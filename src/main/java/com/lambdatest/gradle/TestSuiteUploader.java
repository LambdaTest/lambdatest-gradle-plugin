package com.lambdatest.gradle;

import okhttp3.*;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.logging.Logger;

public class TestSuiteUploader {
    private final static Logger logger = Logger.getLogger(TestSuiteUploader.class.getName());
    private String username;
    private String accessKey;
    private String testSuiteFilePath;

    public TestSuiteUploader(String username, String accessKey, String testSuiteFilePath) {
        this.username = username;
        this.accessKey = accessKey;
        this.testSuiteFilePath = testSuiteFilePath;
    }

    public String uploadTestSuite() throws IOException {
        try{
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("appFile", testSuiteFilePath,
                            RequestBody.create(
                                    MediaType.parse("application/octet-stream"),new java.io.File(testSuiteFilePath)))
                    .addFormDataPart("type", "espresso-android")
                    .build();
            Request request = new Request.Builder()
                    .url(Constants.API_URL)
                    .method("POST", body)
                    .addHeader("Authorization", Credentials.basic(username, accessKey))
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            String appId = jsonObject.get("app_id").getAsString();

            logger.info("Uploaded test suite app ID : " + appId);

            return appId;
    } catch (IOException e) {
        logger.severe("Error uploading test suite app: " + e.getMessage());
        return null;
    }
    }
}
