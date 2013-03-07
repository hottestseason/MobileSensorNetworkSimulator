package utils;

public class MathUtils {
	public static Double getPoisson(Double lambda, Integer k) {
		return Math.pow(lambda, k) * Math.pow(Math.E, -lambda) / (double) factorial(k);
	}

	static int factorial(int n) {
		if (n <= 1) {
			return 1;
		} else {
			return n * factorial(n - 1);
		}
	}
}
