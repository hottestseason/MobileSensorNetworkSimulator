package mobilesensornetwork;

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
	MobileSensorNetwork sensorNetwork;
	Vector2D originDisplacement = new Vector2D(50, 50);
	Double zoom = 1.0;
	Double minRobotSize = 1.0;

	protected Image buffer;
	protected Graphics bufferG;

	public SensorNetworkCanvas(MobileSensorNetwork sensorNetwork) {
		this.sensorNetwork = sensorNetwork;
		Polygon2D surroundedRectangle = sensorNetwork.obstacles.get(0).getSurroundedRectangle();
		int width = (int) ((surroundedRectangle.vertexes.get(1).x - surroundedRectangle.vertexes.get(0).x) * zoom + originDisplacement.x * 2);
		int height = (int) ((surroundedRectangle.vertexes.get(3).y - surroundedRectangle.vertexes.get(0).y) * zoom + originDisplacement.x * 2);
		setSize(width, height);
	}

	public void paintSensorNetwork() {
		drawCoverages(bufferG);
		drawConnections(bufferG);
		drawRobots(bufferG);
		drawObstacles(bufferG);
		debug(bufferG);
	}

	public void drawCoverages(Graphics g) {
		synchronized (sensorNetwork) {
			for (Robot robot : sensorNetwork.getRobots()) {
				drawCircle(robot.getSensorCircle(), g, new Color(0, 0, 0, 16), false);
				drawCircle(robot.getSensorCircle(), g, new Color(255, 255, 0, 32), true);
			}
		}
	}

	public void drawConnections(Graphics g) {
		synchronized (sensorNetwork) {
			for (Robot robot : sensorNetwork.getRobots()) {
				synchronized (robot.getConnectedNodes()) {
					for (Robot connectedRobot : robot.getConnectedRobots()) {
						drawLineSegment2D(robot, connectedRobot, g, new Color(64, 64, 64, 16));
					}
				}
			}
		}
	}

	public void drawRobots(Graphics g) {
		synchronized (sensorNetwork) {
			for (Robot robot : sensorNetwork.getRobots()) {
				drawRobot(robot, g);
			}
		}
	}

	public void drawRobot(Robot robot, Graphics g) {
		if (robot.isRunning()) {
			drawCircle(new Circle(robot, Math.max(robot.getSize(), minRobotSize)), g, Color.black, true);
			drawCircle(robot.getWirelessCircle(), g, new Color(0, 255, 0, 16), false);
		} else {
			drawCircle(new Circle(robot, Math.max(robot.getSize(), minRobotSize)), g, Color.red, true);
		}
		// drawVector(robot, fixForce(robot.virutalForce), g, Color.magenta);
		// drawVector(robot, fixForce(robot.dampingForce), g, Color.blue);
		drawString(robot.getId().toString(), robot, g, Color.black);
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
		return new Point2D(point.x, getHeight() - point.y);
	}

	protected void debug(Graphics g) {

	}

	protected Vector2D fixForce(Vector2D force) {
		return force.expandTo(force.getNorm());
	}
}
