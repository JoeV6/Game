package org.lpc.utils;
import java.io.Serializable;
import java.util.Random;

public class PerlinNoise {
    private final int[] p;
    private static final int PERMUTATION_SIZE = 256;

    public PerlinNoise() {
        p = new int[PERMUTATION_SIZE * 2];
        Random random = new Random();

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            p[i] = i;
        }

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            int j = random.nextInt(PERMUTATION_SIZE);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }

        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            p[PERMUTATION_SIZE + i] = p[i];
        }
    }

    public double noise(double x, double y) {
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        double u = fade(xf);
        double v = fade(yf);

        int aa = p[p[xi] + yi];
        int ab = p[p[xi] + yi + 1];
        int ba = p[p[xi + 1] + yi];
        int bb = p[p[xi + 1] + yi + 1];

        double x1 = lerp(grad(aa, xf, yf), grad(ab, xf, yf - 1), v);
        double x2 = lerp(grad(ba, xf - 1, yf), grad(bb, xf - 1, yf - 1), v);

        return (lerp(x1, x2, u) + 1) / 2; // normalize to [0, 1]
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        switch (hash & 3) {
            case 0: return x + y;
            case 1: return x - y;
            case 2: return x * 2 + y;
            case 3: return -x + y;
            default: return 0; // never happens
        }
    }
}

