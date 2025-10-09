package io.github.lambdatest.gradle;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import org.jetbrains.annotations.NotNull;

/**
 * A RequestBody wrapper that tracks upload progress and displays it in the console. This class uses
 * OkHttp's native Sink and BufferedSink APIs to monitor the upload progress.
 */
public class ProgressRequestBody extends RequestBody {

    private final RequestBody delegate;
    private final ProgressCallback progressCallback;

    /** Interface for progress callbacks. */
    public interface ProgressCallback {
        void onProgress(long bytesWritten, long totalBytes, float percentage);
    }

    /**
     * Creates a new ProgressRequestBody that wraps the given RequestBody.
     *
     * @param delegate The original RequestBody to wrap
     * @param progressCallback Callback to receive progress updates
     */
    public ProgressRequestBody(RequestBody delegate, ProgressCallback progressCallback) {
        this.delegate = delegate;
        this.progressCallback = progressCallback;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) throws IOException {
        long contentLength = contentLength();
        ProgressSink progressSink = new ProgressSink(sink, contentLength, progressCallback);
        BufferedSink bufferedSink = Okio.buffer(progressSink);

        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    /**
     * Custom Sink implementation that tracks progress while forwarding data to the original sink.
     */
    private static class ProgressSink extends ForwardingSink {
        private final long totalBytes;
        private final ProgressCallback progressCallback;
        private long bytesWritten = 0L;
        private long lastLoggedPercentage = -1L;
        private long lastUpdateTime = System.currentTimeMillis();

        public ProgressSink(Sink delegate, long totalBytes, ProgressCallback progressCallback) {
            super(delegate);
            this.totalBytes = totalBytes;
            this.progressCallback = progressCallback;
        }

        @Override
        public void write(@NotNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;

            if (progressCallback != null) {
                float percentage = totalBytes > 0 ? (bytesWritten * 100.0f) / totalBytes : 0f;
                long currentTime = System.currentTimeMillis();

                // Update every 1% or every 250ms for more real-time updates
                long currentPercentage = Math.round(percentage);
                boolean timeBased = (currentTime - lastUpdateTime) >= 250; // 250ms intervals

                if (currentPercentage != lastLoggedPercentage || timeBased) {
                    progressCallback.onProgress(bytesWritten, totalBytes, percentage);
                    lastLoggedPercentage = currentPercentage;
                    lastUpdateTime = currentTime;
                }
            }
        }
    }

    /**
     * Creates a console-based progress callback that displays upload progress using the
     * ProgressTracker for clean, fixed-line output.
     *
     * @param uploadId The unique identifier for this upload (e.g., "App", "Test Suite")
     * @param fileName The name of the file being uploaded
     * @return A ProgressCallback that logs to console
     */
    public static ProgressCallback createConsoleCallback(String uploadId, String fileName) {
        return (bytesWritten, totalBytes, percentage) -> {
            ProgressTracker.updateProgress(
                    uploadId, fileName, percentage, bytesWritten, totalBytes);

            if (percentage >= 100.0f) {
                ProgressTracker.completeUpload(uploadId);
            }
        };
    }

    /**
     * Creates a console-based progress callback (legacy version for backward compatibility).
     *
     * @param fileName The name of the file being uploaded
     * @return A ProgressCallback that logs to console
     * @deprecated Use {@link #createConsoleCallback(String, String)} instead
     */
    @Deprecated
    public static ProgressCallback createConsoleCallback(String fileName) {
        return createConsoleCallback("Upload", fileName);
    }

    /**
     * Formats bytes into human-readable format.
     *
     * @param bytes The number of bytes to format
     * @return Formatted string (e.g., "1.5 MB", "512 KB")
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
