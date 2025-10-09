package io.github.lambdatest.gradle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages progress tracking for multiple concurrent uploads with clean, fixed-line console output.
 * This class ensures that concurrent uploads don't clutter the terminal by maintaining fixed
 * positions for each upload's progress line.
 */
public class ProgressTracker {
    private static final Map<String, Integer> uploadLines = new ConcurrentHashMap<>();
    private static final AtomicInteger nextLineNumber = new AtomicInteger(0);
    private static final Object consoleLock = new Object();

    /**
     * Registers a new upload and allocates a line number for its progress display.
     *
     * @param uploadId Unique identifier for the upload (e.g., "App", "Test Suite")
     * @return The allocated line number for this upload
     */
    private static int registerUpload(String uploadId) {
        return uploadLines.computeIfAbsent(
                uploadId,
                k -> {
                    int lineNum = nextLineNumber.getAndIncrement();
                    // Print a newline to reserve space for this upload
                    System.out.println();
                    System.out.flush();
                    return lineNum;
                });
    }

    /**
     * Updates the progress display for a specific upload.
     *
     * @param uploadId The unique identifier for the upload
     * @param percentage The upload percentage (0-100)
     * @param bytesWritten Bytes uploaded so far
     * @param totalBytes Total bytes to upload
     */
    public static void updateProgress(
            String uploadId, float percentage, long bytesWritten, long totalBytes) {
        synchronized (consoleLock) {
            // Register upload if not already registered (reserves a line)
            int lineNumber = registerUpload(uploadId);
            int totalLines = uploadLines.size();

            // Move cursor to the appropriate line
            if (lineNumber < totalLines - 1) {
                // Not the last line - need to move up
                System.out.printf("\u001B[%dA", totalLines - lineNumber - 1);
            }

            // Clear the line and print progress
            System.out.print("\r\u001B[K"); // Clear line
            String progressBar = createProgressBar(percentage);
            String formattedBytes = ProgressRequestBody.formatBytes(bytesWritten);
            String formattedTotal = ProgressRequestBody.formatBytes(totalBytes);

            System.out.printf(
                    "\u001B[33mUploading %-15s %s %.1f%% (%s / %s)\u001B[0m",
                    uploadId, progressBar, percentage, formattedBytes, formattedTotal);

            // Move cursor back to bottom
            if (lineNumber < totalLines - 1) {
                System.out.printf("\u001B[%dB", totalLines - lineNumber - 1);
            }

            System.out.flush();
        }
    }

    /**
     * Creates a visual progress bar.
     *
     * @param percentage The completion percentage (0-100)
     * @return A string representing the progress bar
     */
    private static String createProgressBar(float percentage) {
        int barLength = 10;
        int filled = (int) (percentage / 100.0 * barLength);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "#" : "-");
        }
        bar.append("]");
        return bar.toString();
    }

    /**
     * Completes the progress for an upload and moves to a new line.
     *
     * @param uploadId The unique identifier for the upload
     */
    public static void completeUpload(String uploadId) {
        synchronized (consoleLock) {
            Integer lineNumber = uploadLines.get(uploadId);
            if (lineNumber != null && lineNumber == uploadLines.size() - 1) {
                // Only add newline if this is the last upload
                System.out.println();
                System.out.flush();
            }
        }
    }

    /**
     * Cleans up all progress lines from the console and resets the tracker. This should be called
     * after all uploads are complete to clear the progress display.
     */
    public static void cleanup() {
        synchronized (consoleLock) {
            int totalLines = uploadLines.size();
            if (totalLines > 0) {
                // Move to the first line
                System.out.printf("\u001B[%dA", totalLines - 1);
                // Clear all progress lines
                for (int i = 0; i < totalLines; i++) {
                    System.out.print("\r\u001B[K"); // Clear current line
                    if (i < totalLines - 1) {
                        System.out.println(); // Move to next line
                    }
                }
                // Move back to start position
                System.out.printf("\u001B[%dA", totalLines - 1);
                System.out.flush();
            }
            reset();
        }
    }

    /** Resets the progress tracker. Should be called when starting a new set of uploads. */
    public static void reset() {
        synchronized (consoleLock) {
            uploadLines.clear();
            nextLineNumber.set(0);
        }
    }
}
