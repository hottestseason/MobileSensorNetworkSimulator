package geom;

import java.util.HashSet;
import java.util.Random;

public class Circle {
	public Point2D center;
	public Double radius;
	static private Random random = new Random(0);

	public Circle() {
		center = new Point2D();
		radius = 0.0;
	}

	public Circle(Point2D center, Double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Circle(LineSegment2D diameter) {
		this.center = diameter.getCenter();
		this.radius = diameter.getLength() / 2;
	}

	public Circle(Point2D point1, Point2D point2, Point2D point3) {
		Line2D perpendicular12 = new Line2D(point1.add(point2).half().toPoint2D(), point1.getVector2DTo(point2).rotate(Math.PI / 2));
		Line2D perpendicular13 = new Line2D(point1.add(point3).half().toPoint2D(), point1.getVector2DTo(point3).rotate(Math.PI / 2));
		center = perpendicular12.getIntersectionPoint(perpendicular13);
		if (center == null) {
			center = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
			radius = 0.0;
		} else {
			radius = center.getDistanceFrom(point1);
		}
	}

	public String toString() {
		return "Circle(" + center + " - " + radius + ")";
	}

	public HashSet<Point2D> getPoints() {
		return getPoints(1);
	}

	public HashSet<Point2D> getPoints(int interval) {
		HashSet<Point2D> points = new HashSet<Point2D>();
		for (int x = (int) Math.floor(center.x - radius); x <= Math.floor(center.x + radius); x += interval) {
			for (int y = (int) Math.floor(center.y - radius); y <= Math.floor(center.y + radius); y += interval) {
				Point2D point = new Point2D(x, y);
				if (contains(point)) {
					points.add(point);
				}
			}
		}
		return points;
	}

	public Boolean contains(Point2D point) {
		return center.getDistanceFrom(point) <= radius;
	}

	public Boolean containsExcludeEdge(Point2D point) {
		return center.getDistanceFrom(point) < radius;
	}

	public Boolean contains(LineSegment2D lineSegment) {
		return center.getDistanceFrom(lineSegment) <= radius;
	}

	public Vector2D getDisplacementToAvoidCollisionFrom(Vector2D displacement, LineSegment2D lineSegment) {
		Vector2D lineDisplacement = center.getVector2DTo(lineSegment).reverse().normalize().multiply(radius);
		return center.getDisplacementToAvoidCollisionFrom(displacement, new LineSegment2D(lineSegment.getStart().add(lineDisplacement), lineSegment));
	}

	public Vector2D getDisplacementToAvoidCollisionFrom(Vector2D displacement, Circle circle) {
		Vector2D oldDisplacement = displacement.clone();
		if (center.add(displacement).getDistanceFrom(circle.center) >= radius + circle.radius) {
			return displacement;
		} else if (center.getDistanceFrom(circle.center) < radius + circle.radius) {
			Vector2D vectorFromCircleCenter = null;
			if (center.equals(circle.center)) {
				vectorFromCircleCenter = Vector2D.random().expandTo(radius);
			} else {
				vectorFromCircleCenter = circle.center.getVector2DTo(center);
			}
			Point2D destination = circle.center.add(vectorFromCircleCenter.expandTo(radius + circle.radius));
			displacement = center.getVector2DTo(destination);
		} else {
			Point2D h = circle.center.getNearestPointOn(new Line2D(center, displacement));
			Double backDistance = Math.sqrt(Math.pow(radius + circle.radius, 2) - Math.pow(circle.center.getDistanceFrom(h), 2));
			displacement = center.getVector2DTo(h).expandTo(center.getDistanceFrom(h) - backDistance);
		}
		if (displacement.x.equals(Double.NaN) || displacement.y.equals(Double.NaN)) {
			System.err.println("something wrong");
			return new Vector2D();
		}
		return displacement;
	}

	public Double cutedLength(LineSegment2D lineSegment) {
		Point2D nearestPoint = center.getNearestPointOn(lineSegment);
		if (center.getDistanceFrom(nearestPoint) > radius) {
			return 0.0;
		} else {
			Double distanceFromNearestPoint = Math.sqrt(radius * radius - Math.pow(center.getDistanceFrom(nearestPoint), 2));
			return Math.min(nearestPoint.getDistanceFrom(lineSegment.getStart()), distanceFromNearestPoint) + Math.min(nearestPoint.getDistanceFrom(lineSegment.getEnd()), distanceFromNearestPoint);
		}
	}

	public static void test() {
		// Circle circle = new Circle(new Point2D(), new Point2D(1, 0), new
		// Point2D(0, 1));
	}
}
