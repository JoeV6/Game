package org.lpc.utils;


public class SystemUtils {
    public static boolean retryTask(int maxRetries, int baseDelayMillis, Runnable task) {
        int retries = 0;
        boolean success = false;

        while (retries < maxRetries && !success) {
            try {
                task.run(); // Try running the task
                success = true;
            } catch (Exception e) {
                retries++;
                if (retries < maxRetries) {
                    long waitTime = (long) Math.pow(2, retries) * baseDelayMillis;
                    System.out.println("Task failed. Retrying in " + (waitTime / 1000) + " seconds...");
                    try {
                        Thread.sleep(waitTime); // Wait before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // Restore interrupted status
                    }
                }
            }
        }
        return success;
    }
}
