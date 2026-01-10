package com.webx.randomizer.utils;

import java.util.Random;

public class RandomUtils {
    
    private static final Random random = new Random();
    
    public static double getRandomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    
    public static int getRandomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
