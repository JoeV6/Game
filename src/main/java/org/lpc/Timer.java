package org.lpc;

public class Timer {
    private static final int UPDATES_PER_SECOND = 20;
    private static final double NANOSECONDS_PER_UPDATE = 1_000_000_000.0 / UPDATES_PER_SECOND;

    private double lag = 0.0;
    private double previousTime = System.nanoTime();

    private int frameCount = 0;
    private long lastFpsTime = System.currentTimeMillis();

    public boolean shouldUpdate() {
        double currentTime = System.nanoTime();
        double elapsedTime = currentTime - previousTime;
        previousTime = currentTime;
        lag += elapsedTime;

        if (lag >= NANOSECONDS_PER_UPDATE) {
            lag -= NANOSECONDS_PER_UPDATE;
            return true;
        }
        return false;
    }

    public void incrementFrameCount() {
        frameCount++;
    }

    public int getFPS() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFpsTime >= 1000) {
            int fps = frameCount;
            frameCount = 0;
            lastFpsTime += 1000;
            return fps;
        }
        return -1; // Return -1 if it's not time to update the FPS
    }
}
