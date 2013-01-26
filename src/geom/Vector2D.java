package geom;

import java.util.Random;

public class Vector2D implements Cloneable {
	public Double x;
	public Double y;
	static private Random random = new Random();

	static public Vector2D random(Double xRange, Double yRange) {
		return random(xRange, yRange, random);
	}

	static public Vector2D random(Double xRange, Double yRange, Random random) {
		return new Vector2D((random.nextDouble() - random.nextDouble()) * xRange, (random.nextDouble() - random.nextDouble()) * yRange);
	}

	static public Vector2D random() {
		return random(1.0, 1.0);
	}

	public Vector2D() {
		this(0, 0);
	}

	public Vector2D(Integer x, Integer y) {
		this.x = x.doubleValue();
		this.y = y.doubleValue();
	}

	public Vector2D(Double x, Double y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public Vector2D clone() {
		return new Vector2D(x, y);
	}

	public boolean equals(Object obj) {
		if (obj instanceof Vector2D) {
			Vector2D vector = (Vector2D) obj;
			return x.equals(vector.x) && y.equals(vector.y);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return (x.intValue() << 16) ^ (y.intValue() >>> 16);
	}

	public Point2D toPoint2D() {
		return new Point2D(x, y);
	}

	public Double getNorm() {
		return Math.sqrt(x * x + y * y);
	}

	public Vector2D expandTo(Double norm) {
		return normalize().multiply(norm);
	}

	public Vector2D reverse() {
		return new Vector2D(-x, -y);
	}

	public Vector2D add(Double x, Double y) {
		return new Vector2D(this.x + x, this.y + y);
	}

	public Vector2D add(Vector2D vector) {
		return add(vector.x, vector.y);
	}

	public Vector2D multiply(Double product) {
		return new Vector2D(x * product, y * product);
	}

	public Vector2D divide(Double division) {
		return multiply(1.0 / division);
	}

	public Vector2D half() {
		return divide(2.0);
	}

	public Double innerProduct(Vector2D vector) {
		return x * vector.x + y * vector.y;
	}

	public Double exteriorProduct(Vector2D vector) {
		return x * vector.y - vector.x * y;
	}

	public Vector2D normalize() {
		Double norm = getNorm();
		if (norm == 0) {
			return multiply(1.0);
		} else {
			return multiply(1.0 / norm);
		}
	}

	public Vector2D rotate(Double angle) {
		Double cos = Math.cos(angle);
		Double sin = Math.sin(angle);
		return new Vector2D(cos * x - sin * y, x * sin + y * cos);
	}

	public Double getAngle() {
		return Math.atan2(y, x);
	}

	public Double getAngle(Vector2D vector) {
		return Math.acos(innerProduct(vector) / (getNorm() * vector.getNorm()));
	}

	public Boolean onTheRightSideOf(Vector2D vector) {
		return exteriorProduct(vector) > 0;
	}

	public Boolean onTheLeftSideOf(Vector2D vector) {
		return exteriorProduct(vector) < 0;
	}

	public Boolean isSameDirection(Vector2D vector) {
		return exteriorProduct(vector) == 0;
	}

	public Vector2D getForceToMove(Double weight, Double time) {
		return expandTo(getNorm() * 2 * weight / Math.pow(time, 1.0));
	}
}
