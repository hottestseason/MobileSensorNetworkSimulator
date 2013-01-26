package geom;
public class Line2D extends Vector2D implements Cloneable {
	Point2D point;

	public Line2D(Vector2D vector) {
		super(vector.x, vector.y);
	}

	public Line2D(Point2D point, Vector2D vector) {
		this(vector);
		this.point = point;
	}

	public Line2D clone() {
		return new Line2D(point, this);
	}

	public Boolean contains(Point2D point) {
		return this.point.getVector2DTo(this).isSameDirection(this);
	}

	public Point2D getIntersectionPoint(Line2D line) {
		Double exteriorProduct = exteriorProduct(line);
		if (exteriorProduct == 0) {
			return null;
		} else {
			Double ratioFromStart = point.getVector2DTo(line.point).exteriorProduct(line) / exteriorProduct;
			return point.add(multiply(ratioFromStart));
		}
	}
}
