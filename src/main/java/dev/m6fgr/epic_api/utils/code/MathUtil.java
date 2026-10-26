package dev.m6fgr.epic_api.utils.code;

import net.minecraft.util.Mth;

import java.util.concurrent.ThreadLocalRandom;

public final class MathUtil {

    private MathUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static int toHex(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    public static int toHex(int r, int g, int b) {
        return toHex(r, g, b, 255);
    }

    public static boolean isChance(double percent) {
        if (percent <= 0.0) return false;
        if (percent >= 100.0) return true;
        return ThreadLocalRandom.current().nextDouble(100.0) < percent;
    }

    public static boolean isChance(double percent, double basePercentage) {
        if (percent <= 0.0) return false;
        if (percent >= 100.0) return true;
        return ThreadLocalRandom.current().nextDouble(basePercentage) < percent;
    }

    public static boolean isProbability(double probability) {
        if (probability <= 0.0) return false;
        if (probability >= 1.0) return true;
        return ThreadLocalRandom.current().nextDouble() < Mth.clamp(probability, 0, 1.0);
    }

    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max");
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static double randomDouble(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max");
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static int clamp(int val, int min, int max) {
        return Math.clamp(val, min, max);
    }

    public static double clamp(double val, double min, double max) {
        return Math.clamp(val, min, max);
    }

    public static double lerp(double start, double end, double t) {
        return start + t * (end - start);
    }

    public static double map(double value, double fromMin, double fromMax, double toMin, double toMax) {
        return toMin + (value - fromMin) * (toMax - toMin) / (fromMax - fromMin);
    }
}