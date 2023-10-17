package com.lambdatest.gradle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.io.IOException;
import java.util.logging.Logger;

public class AppUploader {
        
    private final static Logger logger = Logger.getLogger(AppUploader.class.getName());
    private String username;
    private String accessKey;
    private String appFilePath;

    public AppUploader(String username, String accessKey, String appFilePath) {
        this.username = username;
        this.accessKey = accessKey;
        this.appFilePath = appFilePath;
    }

    public String uploadApp() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("appFile", appFilePath,
                            RequestBody.create(
                                    MediaType.parse("application/octet-stream"), new java.io.File(appFilePath)))
                    .addFormDataPart("type", "espresso-android")
                    .build();
            Request request = new Request.Builder()
                    .url(Constants.API_URL)
                    .method("POST", body)
                    .addHeader("Authorization", Credentials.basic(username, accessKey))
                    .build();
            Response response = client.newCall(request).execute();
            
            // Parse the JSON response and extract the app_id
            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            String appId = jsonObject.get("app_id").getAsString();
            
            logger.info("Uploaded app ID : " + appId);

            return appId;
        } catch (IOException e) {
            logger.severe("Error uploading app: " + e.getMessage());
            return null;
        }
    }
}
