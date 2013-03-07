package geom;

public class Rectangle2D extends Polygon2D {
	public Rectangle2D(Point2D point, Double width, Double height) {
		center = point.add(width / 2, height / 2);
		addVertex(point);
		addVertex(point.add(width, 0.0));
		addVertex(point.add(0.0, height));
		addVertex(point.add(width, height));
	}

	public Double getWidth() {
		for (LineSegment2D edge : getEdges()) {
			Double xDiff = Math.abs(edge.getStart().x - edge.getEnd().x);
			if (xDiff > 0) {
				return xDiff;
			}
		}
		return 0.0;
	}

	public Double getHeight() {
		for (LineSegment2D edge : getEdges()) {
			Double yDiff = Math.abs(edge.getStart().y - edge.getEnd().y);
			if (yDiff > 0) {
				return yDiff;
			}
		}
		return 0.0;
	}
}
