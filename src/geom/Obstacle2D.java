package geom;

import java.util.ArrayList;
import java.util.HashSet;

public class Obstacle2D extends Polygon2D {
	public Boolean innerBlank = false;
	public Boolean preknown = false;

	static public ArrayList<Obstacle2D> getType1(double width, double height) {
		ArrayList<Obstacle2D> obstacles = new ArrayList<Obstacle2D>();
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 1 / 10, height * 0 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 3 / 10, height * 0 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 4.5 / 10, height * 0 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 6 / 10, height * 0 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 8 / 10, height * 0 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 1 / 10, height * 6 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 3 / 10, height * 6 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 4.5 / 10, height * 6 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 6 / 10, height * 6 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 8 / 10, height * 6 / 10), width * 1 / 10, height * 4 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 0 / 10, height * 4.5 / 10), width * 3 / 10, height * 1 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 7 / 10, height * 4.5 / 10), width * 3 / 10, height * 1 / 10)));
		return obstacles;
	}

	static public ArrayList<Obstacle2D> getType2() {
		ArrayList<Obstacle2D> obstacles = new ArrayList<Obstacle2D>();
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(25.0, 25.0), 12.5, 12.5)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(0, 50), 37.5, 12.5)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(12.5, 68.775), 30.0, 31.225)));
		// obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(12.5,
		// 87.5), 25.0, 12.5)));
		// obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(12.5,
		// 75.0), 12.5, 12.5)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(75, 50), 12.5, 12.5)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(50, 75), 12.5, 12.5)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(75, 75), 12.5, 12.5)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(62.5, 0.0), 12.5, 25.0)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(75.0, 12.5), 12.5, 12.5)));
		// obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(75.0, 0.0),
		// 50.0, 25.0)));
		// obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(0, 75),
		// 50.0, 25.0)));
		// obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(100.0,
		// 75.0), 25.0, 50.0)));
		return obstacles;
	}

	public Obstacle2D(Polygon2D polygon) {
		center = polygon.center;
		vertexes = polygon.vertexes;
	}

	public Obstacle2D(Polygon2D polygon, Boolean innerBlank) {
		this(polygon);
		this.innerBlank = innerBlank;
	}

	public Obstacle2D(Polygon2D polygon, Boolean innerBlank, Boolean preknown) {
		this(polygon, innerBlank);
		this.preknown = preknown;
	}

	public Obstacle2D clone() {
		Obstacle2D obstacle = new Obstacle2D(super.clone());
		obstacle.innerBlank = innerBlank;
		obstacle.preknown = preknown;
		return obstacle;
	}

	public HashSet<Point2D> getPoints() {
		return getPoints(1);
	}

	public HashSet<Point2D> getPoints(int interval) {
		HashSet<Point2D> points = new HashSet<Point2D>();
		Polygon2D surroundedRectangle = getSurroundedRectangle();
		int maxX = surroundedRectangle.vertexes.get(1).x.intValue();
		int minX = ((int) Math.floor(surroundedRectangle.vertexes.get(0).x / interval)) * interval;
		int maxY = surroundedRectangle.vertexes.get(2).y.intValue();
		int minY = ((int) Math.floor(surroundedRectangle.vertexes.get(0).y / interval)) * interval;
		for (int x = minX; x <= maxX; x += interval) {
			for (int y = minY; y <= maxY; y += interval) {
				Point2D point = new Point2D(x, y);
				if ((innerBlank && !contains(point)) || contains(point)) {
					points.add(point);
				}
			}
		}
		return points;
	}

	public Boolean contains(Point2D point) {
		return contains(point, true);
	}

	public Boolean contains(Point2D point, Boolean includeEdge) {
		if (innerBlank) {
			return !super.contains(point, !includeEdge);
		} else {
			return super.contains(point, includeEdge);
		}
	}

	public Obstacle2D expandBy(Double size) {
		Obstacle2D obstacle = clone();
		obstacle.vertexes = new ArrayList<Point2D>();
		ArrayList<LineSegment2D> edges = getEdges();
		for (int i = 0; i < edges.size(); i++) {
			LineSegment2D edgeA = edges.get(i).clone();
			LineSegment2D edgeB;
			if (i == edges.size() - 1) {
				edgeB = edges.get(0).clone();
			} else {
				edgeB = edges.get(i + 1).clone();
			}
			Vector2D centerToEdgeA = center.getVector2DTo(edgeA);
			edgeA.point = center.add(centerToEdgeA.expandTo(centerToEdgeA.getNorm() + size));
			Vector2D centerToEdgeB = center.getVector2DTo(edgeB);
			edgeB.point = center.add(centerToEdgeB.expandTo(centerToEdgeB.getNorm() + size));
			Point2D intersection = edgeA.toLine2D().getIntersectionPoint(edgeB.toLine2D());
			obstacle.addVertex(intersection);
		}
		return obstacle;
	}

	public Vector2D getDisplacementToAvoidCollisionWithEdge(Point2D point, Vector2D displacement, LineSegment2D edge) {
		Point2D end = point.add(displacement);
		if (displacement.x.equals(Double.NaN) || displacement.y.equals(Double.NaN)) {
			System.err.println("nan displacement");
			return new Vector2D();
		}
		if (!(edge.contains(point) && ((innerBlank && !end.onTheRightSideOf(edge)) || (!innerBlank && !end.onTheLeftSideOf(edge))))) {
			displacement = point.getDisplacementToAvoidCollisionFrom(displacement, edge, innerBlank);
		}
		return displacement;
	}

	public Vector2D getDisplacementToAvoidCollision(Point2D point, Vector2D displacement) {
		if (contains(point, false)) {
			return point.getVector2DTo(nearestPointFrom(point));
		} else {
			for (LineSegment2D edge : getEdges()) {
				displacement = getDisplacementToAvoidCollisionWithEdge(point, displacement, edge);
			}
			return displacement;
		}
	}

	public Vector2D getDisplacementToAvoidCollision(Circle circle, Vector2D displacement) {
		Double expandLength;
		if (innerBlank) {
			expandLength = -circle.radius;
		} else {
			expandLength = circle.radius;
		}
		return expandBy(expandLength).getDisplacementToAvoidCollision(circle.center, displacement);
	}
}
