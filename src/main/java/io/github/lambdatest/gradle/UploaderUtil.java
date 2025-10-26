package io.github.lambdatest.gradle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Utility class providing common upload functionality for the LambdaTest Gradle plugin. This class
 * handles the actual file upload process and response parsing.
 *
 * <p>This utility is used by both {@link AppUploader} and {@link TestSuiteUploader} to handle file
 * uploads to the LambdaTest platform.
 */
public final class UploaderUtil {
    /** Private constructor to prevent instantiation of this utility class. */
    private UploaderUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    /**
     * Uploads a file to LambdaTest and returns its ID.
     *
     * @implNote This method sends the file to {@link Constants#API_URL} and handles the multipart
     *     form data construction and response parsing.
     * @param username The LambdaTest account username
     * @param accessKey The LambdaTest account access key
     * @param filePath The path to the file to be uploaded
     * @return The ID of the uploaded file
     * @throws IOException if there's an error during file upload or response parsing
     */
    public static String uploadAndGetId(String username, String accessKey, String filePath)
            throws IOException {
        return uploadAndGetId(username, accessKey, filePath, false, null);
    }

    /**
     * Uploads a file to LambdaTest and returns its ID with optional progress tracking.
     *
     * @implNote This method sends the file to {@link Constants#API_URL} and handles the multipart
     *     form data construction and response parsing. When showProgress is true, it uses {@link
     *     ProgressRequestBody} to track and display upload progress.
     * @param username The LambdaTest account username
     * @param accessKey The LambdaTest account access key
     * @param filePath The path to the file to be uploaded
     * @param showProgress Whether to display upload progress in the console
     * @return The ID of the uploaded file
     * @throws IOException if there's an error during file upload or response parsing
     */
    public static String uploadAndGetId(
            String username, String accessKey, String filePath, boolean showProgress)
            throws IOException {
        return uploadAndGetId(username, accessKey, filePath, showProgress, null);
    }

    /**
     * Uploads a file to LambdaTest and returns its ID with optional progress tracking and custom
     * prefix.
     *
     * @implNote This method sends the file to {@link Constants#API_URL} and handles the multipart
     *     form data construction and response parsing. When showProgress is true, it uses {@link
     *     ProgressRequestBody} to track and display upload progress.
     * @param username The LambdaTest account username
     * @param accessKey The LambdaTest account access key
     * @param filePath The path to the file to be uploaded
     * @param showProgress Whether to display upload progress in the console
     * @param progressPrefix Optional prefix for progress display (e.g., "App", "Test Suite")
     * @return The ID of the uploaded file
     * @throws IOException if there's an error during file upload or response parsing
     */
    public static String uploadAndGetId(
            String username,
            String accessKey,
            String filePath,
            boolean showProgress,
            String progressPrefix)
            throws IOException {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES) // Increase connection timeout
                        .readTimeout(0, TimeUnit.MILLISECONDS) // Increase read timeout
                        .writeTimeout(0, TimeUnit.MILLISECONDS) // Increase write timeout
                        .build();

        File file = new File(filePath);
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody fileRequestBody = RequestBody.create(file, mediaType);

        RequestBody body =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("appFile", filePath, fileRequestBody)
                        .addFormDataPart("type", "espresso-android")
                        .build();

        // Wrap the entire multipart body with progress tracking if requested
        if (showProgress) {
            String uploadId = progressPrefix != null ? progressPrefix : "Upload";
            ProgressRequestBody.ProgressCallback callback =
                    ProgressRequestBody.createConsoleCallback(uploadId);
            body = new ProgressRequestBody(body, callback);
        }
        Request request =
                new Request.Builder()
                        .url(Constants.API_URL)
                        .addHeader("Authorization", Credentials.basic(username, accessKey))
                        .post(body)
                        .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
            String id = jsonObject.get("app_id").getAsString();

            return id;
        }
    }
}
