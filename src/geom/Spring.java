package geom;

public class Spring extends Vector2D {
	Double naturalLength, springConstant;

	public static Vector2D getForce(Vector2D vector, Double naturalLength, Double springConstant) {
		return new Spring(vector, naturalLength, springConstant).getForce();
	}

	public static Vector2D getForce(Point2D start, Point2D end, Double naturalLength, Double springConstant) {
		return getForce(start.getVector2DTo(end), naturalLength, springConstant);
	}

	public Spring(Double naturalLength, Double springConstant) {
		this.naturalLength = naturalLength;
		this.springConstant = springConstant;
	}

	public Spring(Vector2D vector, Double naturalLength, Double springConstant) {
		this(naturalLength, springConstant);
		setVector2D(vector);
	}

	public Vector2D getForce() {
		return normalize().multiply(springConstant * (getNorm() - naturalLength));
	}

	public Spring setVector2D(Vector2D vector) {
		x = vector.x;
		y = vector.y;
		return this;
	}
}
