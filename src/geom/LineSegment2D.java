package geom;

public class LineSegment2D extends Line2D {
	public LineSegment2D(Point2D start, Vector2D vector) {
		super(start, vector);
	}

	public LineSegment2D(Point2D start, Point2D end) {
		super(start, start.getVector2DTo(end));
	}

	public LineSegment2D clone() {
		return new LineSegment2D(getStart(), this);
	}

	public String toString() {
		return getStart() + " - " + getEnd();
	}

	public Line2D toLine2D() {
		return new Line2D(point, this);
	}

	public Point2D getStart() {
		return point;
	}

	public Point2D getEnd() {
		return point.add(this);
	}

	public Double getLength() {
		return getNorm();
	}

	public Point2D getCenter() {
		return point.add(multiply(0.5));
	}

	public Point2D getIntersectionPoint(LineSegment2D lineSegment, Boolean includeEdge) {
		if (exteriorProduct(lineSegment) == 0) {
			return null;
		} else {
			Double ratioFromStart = point.getVector2DTo(lineSegment.point).exteriorProduct(lineSegment) / exteriorProduct(lineSegment);
			Double ratioFromAnotherStart = point.getVector2DTo(lineSegment.point).exteriorProduct(this) / exteriorProduct(lineSegment);
			if (includeEdge) {
				if (ratioFromStart < 0 || ratioFromStart > 1 || ratioFromAnotherStart < 0 || ratioFromAnotherStart > 1) {
					return null;
				}
			} else {
				if (ratioFromStart <= 0 || ratioFromStart >= 1 || ratioFromAnotherStart <= 0 || ratioFromAnotherStart >= 1) {
					return null;
				}
			}
			return point.add(this.multiply(ratioFromStart));
		}
	}

	public Point2D getIntersectionPoint(LineSegment2D lineSegment) {
		return getIntersectionPoint(lineSegment, true);
	}

	public Point2D getIntersectionPoint(Line2D line, Boolean includeEdge) {
		Double ratioFromStart = point.getVector2DTo(line.point).exteriorProduct(line) / exteriorProduct(line);
		if (includeEdge) {
			if (ratioFromStart < 0 || ratioFromStart > 1) {
				return null;
			}
		} else {
			if (ratioFromStart <= 0 || ratioFromStart >= 1) {
				return null;
			}
		}
		return point.add(this.multiply(ratioFromStart));
	}

	public Point2D getIntersectionPoint(Line2D line) {
		return getIntersectionPoint(line, true);
	}

	public Boolean contains(Point2D point) {
		return super.contains(point) && point.getDistanceFrom(this) == 0;
	}

	public Boolean contains(LineSegment2D lineSegment) {
		return contains(lineSegment, true);
	}

	public Boolean contains(LineSegment2D lineSegment, Boolean includeEdge) {
		return getIntersectionPoint(lineSegment, includeEdge) != null;
	}

	public Boolean isVisibleFrom(Point2D point, LineSegment2D wall) {
		if (point.getDistanceFrom(this) > point.getDistanceFrom(wall)) {
			Double startAngle = null, endAngle = null;
			if (point.getVector2DTo(wall.getStart()).onTheRightSideOf(point.getVector2DTo(wall.getEnd()))) {
				startAngle = point.getAngle(wall.getStart());
				endAngle = point.getAngle(wall.getEnd());
			} else {
				startAngle = point.getAngle(wall.getEnd());
				endAngle = point.getAngle(wall.getStart());
			}
			if (new CircularSector(point, null, startAngle, endAngle).containsAll(this)) {
				return false;
			}
		}
		return true;
	}
}
