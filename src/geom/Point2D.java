package geom;

public class Point2D extends Vector2D {
	public Point2D() {
		super();
	}

	public Point2D(Integer x, Integer y) {
		super(x, y);
	}

	public Point2D(Double x, Double y) {
		super(x, y);
	}

	public Point2D clone() {
		return (Point2D) super.clone();
	}

	public Point2D add(Vector2D vector) {
		return super.add(vector).toPoint2D();
	}

	public Point2D add(Double x, Double y) {
		return super.add(x, y).toPoint2D();
	}

	public Vector2D getVector2DTo(Point2D point) {
		return reverse().add(point);
	}

	public Boolean onTheRightSideOf(Line2D line) {
		return line.point.getVector2DTo(this).onTheRightSideOf(line);
	}

	public Boolean onTheLeftSideOf(Line2D line) {
		return line.point.getVector2DTo(this).onTheLeftSideOf(line);
	}

	public Vector2D getVector2DTo(Line2D line) {
		if (onTheRightSideOf(line)) {
			return line.normalize().rotate(Math.PI / 2).multiply(getDistanceFrom(line));
		} else {
			return line.normalize().rotate(-Math.PI / 2).multiply(getDistanceFrom(line));
		}
	}

	public Double getAngle(Point2D point) {
		return getVector2DTo(point).getAngle();
	}

	public Point2D getNearestPointOn(Line2D line) {
		return add(getVector2DTo(line));
	}

	public Point2D getNearestPointOn(LineSegment2D lineSegment) {
		if (lineSegment.getStart().getVector2DTo(this).innerProduct(lineSegment) < 0) {
			return lineSegment.getStart();
		} else if (lineSegment.getEnd().getVector2DTo(this).innerProduct(lineSegment.reverse()) < 0) {
			return lineSegment.getEnd();
		} else {
			return add(getVector2DTo(lineSegment));
		}
	}

	public Double getDistanceFrom(Point2D point) {
		return getVector2DTo(point).getNorm();
	}

	public Double getDistanceFrom(Line2D line) {
		return Math.abs(line.point.getVector2DTo(this).exteriorProduct(line.normalize()));
	}

	public Double getDistanceFrom(LineSegment2D lineSegment) {
		return getDistanceFrom(getNearestPointOn(lineSegment));
	}

	public Vector2D getDisplacementToAvoidCollisionFrom(Vector2D displacement, LineSegment2D lineSegment, Boolean stopOnEdge) {
		Vector2D oldDisplacement = displacement.clone();
		Point2D intersection = new LineSegment2D(this, displacement).getIntersectionPoint(lineSegment);
		if (intersection != null) {
			Vector2D displacementGoThroughLine = intersection.getVector2DTo(add(displacement).toPoint2D());
			Vector2D displacementOnLine = lineSegment.expandTo(displacementGoThroughLine.innerProduct(lineSegment.normalize()));
			Point2D destination = intersection.add(displacementOnLine).toPoint2D();
			if (stopOnEdge) {
				displacement = getVector2DTo(destination.getNearestPointOn(lineSegment));
			} else {
				displacement = getVector2DTo(destination);
			}
		}
		if (displacement.x.equals(Double.NaN) || displacement.y.equals(Double.NaN)) {
			System.err.println("nan displacement");
			return new Vector2D();
		}
		return displacement;
	}

	public Vector2D getDisplacementToAvoidCollisionFrom(Vector2D displacement, LineSegment2D lineSegment) {
		return getDisplacementToAvoidCollisionFrom(displacement, lineSegment, true);
	}

	public Boolean isVisibleFrom(Point2D point, LineSegment2D wall) {
		if (point.getDistanceFrom(this) > point.getDistanceFrom(wall)) {
			Double startAngle = Math.min(point.getAngle(wall.getStart()), point.getAngle(wall.getEnd()));
			Double endAngle = Math.max(point.getAngle(wall.getStart()), point.getAngle(wall.getEnd()));
			if (new CircularSector(point, null, startAngle, endAngle).contains(this)) {
				return false;
			}
		}
		return true;
	}

	static public void test() {
		System.out.println(new Point2D().hashCode() == new Point2D(0, 0).hashCode());
		System.out.println(new Point2D().equals(new Point2D(0, 0)));
		System.out.println(new Point2D().hashCode() == new Point2D(0, 1).hashCode());
		System.out.println(!new Point2D().equals(new Point2D(0, 1)));
	}
}
