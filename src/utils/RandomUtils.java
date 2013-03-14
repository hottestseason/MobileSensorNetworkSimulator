package utils;

import geom.Vector2D;

import java.util.Random;

public class RandomUtils {
	static public Random random = new Random(0);

	static public void init(long seed) {
		random = new Random(seed);
	}

	static public double nextDouble() {
		return random.nextDouble();
	}

	static public Vector2D nextVector(Double xRange, Double yRange) {
		return new Vector2D((random.nextDouble() - random.nextDouble()) * xRange, (random.nextDouble() - random.nextDouble()) * yRange);
	}

	static public Vector2D nextVector() {
		return nextVector(1.0, 1.0);
	}
}
