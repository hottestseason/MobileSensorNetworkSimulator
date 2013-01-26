package geom;

public class CircularSector extends Circle {
	public Double startAngle = -Math.PI;
	public Double endAngle = Math.PI;

	public CircularSector() {
		super();
	}

	public CircularSector(Point2D center, Double radius) {
		super(center, radius);
	}

	public CircularSector(Double startAngle, Double endAngle) {
		super();
		this.startAngle = startAngle;
		this.endAngle = endAngle;
	}

	public CircularSector(Point2D center, Double radius, Double startAngle, Double endAngle) {
		super(center, radius);
		this.startAngle = startAngle;
		this.endAngle = endAngle;
	}

	public String toString() {
		return "Circle(" + center + " - " + radius + ", startAngle: " + startAngle + ", endAngle: " + endAngle + ", angle : " + getAngle() + ")";
	}

	public Double getAngle() {
		return getAngle(true);
	}

	public Double getAngle(Boolean directed) {
		Double angle;
		if (startAngle <= endAngle) {
			angle = endAngle - startAngle;
		} else {
			angle = (endAngle - (-Math.PI)) + (Math.PI - startAngle);
		}
		if (!directed && angle > Math.PI) {
			angle = 2 * Math.PI - angle;
		}
		return angle;
	}

	public Boolean contains(Double angle) {
		if (startAngle < endAngle) {
			return startAngle <= angle && angle <= endAngle;
		} else {
			return angle <= endAngle || startAngle <= angle;
		}
	}

	public Boolean contains(Point2D point) {
		return contains(center.getVector2DTo(point).getAngle());
	}

	public Boolean contains(LineSegment2D lineSegment) {
		return contains(lineSegment.getStart()) || contains(lineSegment.getEnd());
	}

	public Boolean containsAll(LineSegment2D lineSegment) {
		return contains(lineSegment.getStart()) && contains(lineSegment.getEnd());
	}

}
