package geom;

import java.util.ArrayList;

public class Polygon2D implements Cloneable {
	Point2D center;
	public ArrayList<Point2D> vertexes = new ArrayList<Point2D>();
	protected ArrayList<LineSegment2D> edges = null;

	public String toString() {
		return getEdges().toString();
	}

	public Polygon2D clone() {
		Polygon2D polygon = new Polygon2D();
		polygon.center = center;
		polygon.vertexes = vertexes;
		return polygon;
	}

	public ArrayList<LineSegment2D> getEdges() {
		if (edges == null) {
			edges = new ArrayList<LineSegment2D>();
			for (int i = 0; i < vertexes.size(); i++) {
				Point2D start = vertexes.get(i), end;
				if (i == vertexes.size() - 1) {
					end = vertexes.get(0);
				} else {
					end = vertexes.get(i + 1);
				}
				edges.add(new LineSegment2D(start, end));
			}
		}
		return edges;
	}

	public void addVertex(Point2D point) {
		for (int i = 0; i < vertexes.size(); i++) {
			if (center.getVector2DTo(vertexes.get(i)).getAngle() > center.getVector2DTo(point).getAngle()) {
				vertexes.add(i, point);
				return;
			}
		}
		vertexes.add(point);
		edges = null;
	}

	public Rectangle2D getSurroundedRectangle() {
		Double maxX = Double.MIN_VALUE, minX = Double.MAX_VALUE, maxY = Double.MIN_VALUE, minY = Double.MAX_VALUE;
		for (Point2D vertex : vertexes) {
			maxX = Math.max(maxX, vertex.x.intValue());
			minX = Math.min(minX, vertex.x.intValue());
			maxY = Math.max(maxY, vertex.y.intValue());
			minY = Math.min(minY, vertex.y.intValue());
		}
		return new Rectangle2D(new Point2D(minX, minY), maxX - minX, maxY - minY);
	}

	public Double getAreaSize() {
		Double size = 0.0;
		for (LineSegment2D edge : getEdges()) {
			Vector2D a = center.getVector2DTo(edge.getStart());
			Vector2D b = center.getVector2DTo(edge.getEnd());
			size += Math.sqrt(Math.pow(a.getNorm() * b.getNorm(), 2) - Math.pow(a.innerProduct(b), 2)) / 2;
		}
		return size;
	}

	public Boolean contains(Point2D point) {
		return contains(point, true);
	}

	public Boolean contains(Point2D point, Boolean includeEdge) {
		if (includeEdge) {
			for (LineSegment2D edge : getEdges()) {
				if (point.onTheRightSideOf(edge)) {
					return false;
				}
			}
			return true;
		} else {
			for (LineSegment2D edge : getEdges()) {
				if (!point.onTheLeftSideOf(edge)) {
					return false;
				}
			}
			return true;
		}
	}

	public Boolean contains(LineSegment2D lineSegment) {
		for (LineSegment2D edge : getEdges()) {
			if (edge.contains(lineSegment)) {
				return true;
			}
		}
		return false;
	}

	public Point2D nearestPointFrom(Point2D point) {
		Point2D nearestPoint = vertexes.get(0);
		Double nearestDistance = nearestPoint.getDistanceFrom(point);
		for (LineSegment2D edge : getEdges()) {
			Point2D nearPoint = point.getNearestPointOn(edge);
			Double nearDistance = nearPoint.getDistanceFrom(point);
			if (nearDistance < nearestDistance) {
				nearestPoint = nearPoint;
				nearestDistance = nearDistance;
			}
		}
		return nearestPoint;
	}
}
