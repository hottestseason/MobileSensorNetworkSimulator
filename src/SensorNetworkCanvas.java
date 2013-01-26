import geom.Circle;
import geom.CircularSector;
import geom.LineSegment2D;
import geom.Obstacle2D;
import geom.Point2D;
import geom.Polygon2D;
import geom.Vector2D;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

@SuppressWarnings("serial")
public class SensorNetworkCanvas extends Canvas {
	SensorNetwork sensorNetwork;
	Vector2D originDisplacement = new Vector2D(50, 50);
	Integer width, height;
	Double zoom = 4.0;
	Double minRobotSize = 0.0;

	protected Image buffer;
	protected Graphics bufferG;

	public SensorNetworkCanvas(SensorNetwork sensorNetwork) {
		this.sensorNetwork = sensorNetwork;
		Polygon2D surroundedRectangle = sensorNetwork.obstacles.get(0).getSurroundedRectangle();
		this.width = (int) ((surroundedRectangle.vertexes.get(1).x - surroundedRectangle.vertexes.get(0).x) * zoom + originDisplacement.x * 2);
		this.height = (int) ((surroundedRectangle.vertexes.get(3).y - surroundedRectangle.vertexes.get(0).y) * zoom + originDisplacement.x * 2);
		setSize(width, height);
	}

	public void paintSensorNetwork() {
		drawCoverages(bufferG);
		drawConnections(bufferG);
		drawRobots(bufferG);
		drawObstacles(bufferG);
		drawString("iterate No. : " + sensorNetwork.iterateNo, new Point2D(1, -2), bufferG, Color.black);
		drawString("all movded distance : " + sensorNetwork.sumMovedDistance, new Point2D(1, -4), bufferG, Color.black);
		drawString("connectivity : " + sensorNetwork.isConnected, new Point2D(1, -6), bufferG, Color.black);
		debug(bufferG);
	}

	public void drawWirelessRanges(Graphics g) {
		synchronized (sensorNetwork) {
			for (Node node : sensorNetwork) {
				Robot robot = (Robot) node;
				drawCircle(new Circle(robot, robot.wirelessRange), g, new Color(0, 255, 0, 32), false);
			}
		}
	}

	public void drawCoverages(Graphics g) {
		synchronized (sensorNetwork) {
			for (Node node : sensorNetwork) {
				Robot robot = (Robot) node;
				drawCircle(new Circle(robot, robot.sensorRange), g, new Color(0, 0, 0, 32), false);
				drawCircle(new Circle(robot, robot.sensorRange), g, new Color(255, 255, 0, 64), true);
			}
		}
	}

	public void drawConnections(Graphics g) {
		synchronized (sensorNetwork) {
			for (Robot robot : sensorNetwork.getRobots()) {
				synchronized (robot.connectedNodes) {
					for (Robot connectedRobot : robot.getConnectedRobots()) {
						drawLineSegment2D(robot, connectedRobot, g, Color.gray);
					}
				}
			}
		}
	}

	public void drawRobots(Graphics g) {
		synchronized (sensorNetwork) {
			for (Node node : sensorNetwork) {
				Robot robot = (Robot) node;
				drawRobot(robot, g);
			}
		}
	}

	public void drawRobot(Robot robot, Graphics g) {
		drawCircle(new Circle(robot, Math.max(robot.size, minRobotSize)), g, Color.black, true);
		if (robot.isEdge) {
			drawCircle(new Circle(robot, Math.max(robot.size, minRobotSize)), g, Color.red, true);
		}
		drawCircle(new Circle(robot, robot.wirelessRange), g, new Color(0, 255, 0, 64), false);
		drawString(robot.id.toString(), robot, g, Color.black);
	}

	public void drawObstacles(Graphics g) {
		for (Obstacle2D obstacle : sensorNetwork.obstacles) {
			drawPolygon2D(obstacle, g, Color.black);
		}
	}

	public void resetBuffer() {
		if (buffer == null) {
			buffer = createImage(getWidth(), getHeight());
			bufferG = buffer.getGraphics();
		}
		bufferG.setColor(Color.white);
		bufferG.fillRect(0, 0, getWidth(), getHeight());
	}

	public void drawCircle(Circle circle, Graphics g, Color color, Boolean fill) {
		g.setColor(color);
		Point2D center = fixPoint(circle.center).add(-circle.radius * zoom, -circle.radius * zoom);
		if (fill) {
			g.fillOval(center.x.intValue(), center.y.intValue(), (int) (circle.radius * zoom * 2), (int) (circle.radius * zoom * 2));
		} else {
			g.drawOval(center.x.intValue(), center.y.intValue(), (int) (circle.radius * zoom * 2), (int) (circle.radius * zoom * 2));
		}
	}

	public void drawCircularSector(CircularSector circularSector, Graphics g, Color color) {
		g.setColor(color);
		Point2D center = fixPoint(circularSector.center);
		g.fillArc(center.x.intValue(), center.y.intValue(), circularSector.radius.intValue(), circularSector.radius.intValue(), circularSector.startAngle.intValue(), circularSector.getAngle().intValue());
	}

	protected void drawString(String string, Point2D point, Graphics g, Color color) {
		g.setColor(color);
		g.setFont(new Font(null, Font.PLAIN, 8));
		point = fixPoint(point);
		g.drawString(string, point.x.intValue(), point.y.intValue() + 10);
	}

	public void drawLineSegment2D(Point2D start, Point2D end, Graphics g, Color color) {
		g.setColor(color);
		start = fixPoint(start);
		end = fixPoint(end);
		g.drawLine(start.x.intValue(), start.y.intValue(), end.x.intValue(), end.y.intValue());
	}

	public void drawLineSegment2D(LineSegment2D lineSegment, Graphics g, Color color) {
		drawLineSegment2D(lineSegment.getStart(), lineSegment.getEnd(), g, color);
	}

	public void drawPolygon2D(Polygon2D polygon, Graphics g, Color color) {
		for (LineSegment2D lineSegment : polygon.getEdges()) {
			drawLineSegment2D(lineSegment, g, color);
		}
	}

	public void fillPolygon2D(Polygon2D polygon, Graphics g, Color color) {
		int[] xPoints = new int[polygon.vertexes.size()];
		int[] yPoints = new int[polygon.vertexes.size()];
		for (int i = 0; i < polygon.vertexes.size(); i++) {
			Point2D vertex = fixPoint(polygon.vertexes.get(i));
			xPoints[i] = vertex.x.intValue();
			yPoints[i] = vertex.y.intValue();
		}
		g.setColor(color);
		g.fillPolygon(xPoints, yPoints, xPoints.length);
	}

	public void drawVector(Point2D start, Vector2D vector, Graphics g, Color color) {
		g.setColor(color);
		drawLineSegment2D(new LineSegment2D(start, vector), g, color);
	}

	public void paint(Graphics g) {
		resetBuffer();
		paintSensorNetwork();
		g.drawImage(buffer, 0, 0, this);
	}

	protected Point2D fixPoint(Point2D point) {
		point = point.multiply(zoom).toPoint2D().add(originDisplacement);
		return new Point2D(point.x, height - point.y);
	}

	protected void debug(Graphics g) {

	}

	protected Vector2D fixForce(Vector2D force) {
		return force.expandTo(Math.sqrt(force.getNorm()));
	}
}
