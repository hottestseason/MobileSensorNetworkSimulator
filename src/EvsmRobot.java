import geom.Circle;
import geom.CircularSector;
import geom.LineSegment2D;
import geom.Obstacle2D;
import geom.Point2D;
import geom.Spring;
import geom.Vector2D;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class EvsmRobot extends SpringVFRobot {
	Boolean isNearObstacle = false;
	Vector2D exploratoryForce = new Vector2D();
	Vector2D selfOrganizingForce = new Vector2D();

	public EvsmRobot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval) {
		super(sensorNetwork, id, wirelessRange, sensorRange, weight, size, iterateInterval);
		this.springConstant = 25.0;
		Double angularFrequency = Math.sqrt(2 * springConstant / weight);
		this.dampingCoefficient = 2 * weight * angularFrequency;
		this.idealDistance = sensorRange * Math.sqrt(3);
	}

	public String toString() {
		return super.toString() + "\nselfOrganizingForce: " + selfOrganizingForce + "\nexploratoryForce: " + exploratoryForce;
	}

	public void setUpForIteration() {
		super.setUpForIteration();
		isNearObstacle = false;
		exploratoryForce = new Vector2D();
		selfOrganizingForce = new Vector2D();
	}

	public void calculateForce() {
		for (Robot robot : getConnectedRobots()) {
			selfOrganizingForce = selfOrganizingForce.add(getVirtualForceFrom(robot));
		}
		applyForce(selfOrganizingForce);
		if (isEdge) {
			// exploratoryForce = getExploratoryForce();
			applyForce(exploratoryForce);
		}
		// appliedForce = appliedForce.add(getForceFromVertex());
		applyForce(exploratoryForce);
	}

	public Boolean canSense(Robot robot) {
		return super.canSense(robot) && acuteAngleTest(robot);
	}

	public CircularSector getSweepCircularSector() {
		if (connectedNodes.size() < 2) {
			return new CircularSector(this, null, -Math.PI / 2, Math.PI / 2);
		} else {
			CircularSector sweepCircularSector = new CircularSector(this, null, 0.0, 0.0);
			for (int i = 0; i < connectedNodes.size(); i++) {
				Double startAngle = getAngle(((EvsmRobot) connectedNodes.get(i)));
				Double endAngle;
				if (i == connectedNodes.size() - 1) {
					endAngle = getAngle(((EvsmRobot) connectedNodes.get(0)));
				} else {
					endAngle = getAngle(((EvsmRobot) connectedNodes.get(i + 1)));
				}
				CircularSector circularSector = new CircularSector(this, null, startAngle, endAngle);
				if (sweepCircularSector == null || sweepCircularSector.getAngle() < circularSector.getAngle()) {
					sweepCircularSector = circularSector;
				}
			}
			return sweepCircularSector;
		}
	}

	public ArrayList<LineSegment2D> getVisibleWalls() {
		ArrayList<LineSegment2D> walls = new ArrayList<LineSegment2D>();
		CircularSector sweepCircularSector = getSweepCircularSector();
		for (Obstacle2D obstacle : getSensorNetwork().obstacles) {
			for (LineSegment2D wall : obstacle.getEdges()) {
				if ((obstacle.preknown || canSense(wall)) && sweepCircularSector.contains(wall)) {
					walls.add(wall);
				}
			}
		}
		ArrayList<LineSegment2D> hiddenWalls = new ArrayList<LineSegment2D>();
		for (LineSegment2D wallA : walls) {
			for (LineSegment2D wallB : walls) {
				if (wallA != wallB && !wallA.isVisibleFrom(this, wallB)) {
					hiddenWalls.add(wallA);
				}
			}
		}
		walls.removeAll(hiddenWalls);
		return walls;
	}

	public ArrayList<LineSegment2D> getNearWalls() {
		ArrayList<LineSegment2D> walls = new ArrayList<LineSegment2D>();
		for (LineSegment2D wall : getVisibleWalls()) {
			Boolean addFlag = true;
			for (Robot robot : getConnectedRobots()) {
				if (getDistanceFrom(wall) > robot.getDistanceFrom(wall)) {
					addFlag = false;
					break;
				}
			}
			if (addFlag) {
				walls.add(wall);
			}
		}
		return walls;
	}

	public Double getWeightOfWall(LineSegment2D wall) {
		Double sumOfDistance = 0.0;
		for (LineSegment2D visibleWall : getVisibleWalls()) {
			sumOfDistance += Math.pow(getDistanceFrom(visibleWall), 2);
		}
		if (sumOfDistance == 0) {
			return 0.0;
		} else {
			return getDistanceFrom(wall) / Math.sqrt(sumOfDistance);
		}
	}

	public Double getAverageDistanceFromConnectedRobots() {
		if (connectedNodes.size() > 0) {
			Double averageDistance = 0.0;
			for (Robot robot : getConnectedRobots()) {
				averageDistance += getDistanceFrom(robot);
			}
			return averageDistance / connectedNodes.size();
		} else {
			return 0.0;
		}
	}

	public Vector2D getExploratoryForceFrom(LineSegment2D wall) {
		return getVector2DTo(wall).normalize().multiply(springConstant * Math.min(wirelessRange - getAverageDistanceFromConnectedRobots(), getDistanceFrom(wall)) * getWeightOfWall(wall)).add(speed.multiply(dampingCoefficient).reverse());
	}

	public Vector2D getExploratoryForce() {
		Vector2D force = new Vector2D();
		for (LineSegment2D wall : getNearWalls()) {
			force = force.add(getExploratoryForceFrom(wall));
		}
		return force;
	}

	public Boolean isNearThanConnectedRobots(Point2D point) {
		for (Robot robot : getConnectedRobots()) {
			if (getDistanceFrom(point) > robot.getDistanceFrom(point)) {
				return false;
			}
		}
		return true;
	}

	public Vector2D getForceFromVertex() {
		Vector2D force = new Vector2D();
		if (connectedNodes.size() > 1) {
			for (Obstacle2D obstacle : getSensorNetwork().obstacles) {
				if (!obstacle.innerBlank) {
					for (LineSegment2D wall : obstacle.expandBy(5.0).getEdges()) {
						if (getDistanceFrom(wall.getStart()) < 10 && isNearThanConnectedRobots(wall.getStart())) {
							force = force.add(Spring.getForce(getVector2DTo(wall.getStart()), 0.0, springConstant / 2));
						} else if (getDistanceFrom(wall.getEnd()) < 10 && isNearThanConnectedRobots(wall.getEnd())) {
							force = force.add(Spring.getForce(getVector2DTo(wall.getEnd()), 0.0, springConstant / 2));
						}
					}
				}
			}
		}
		return force;
	}
}

@SuppressWarnings("serial")
class EvsmSensorNetworkCanvas extends SensorNetworkCanvas {
	public EvsmSensorNetworkCanvas(SensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void debug(Graphics g) {
		// EvsmRobot robot = (EvsmRobot) sensorNetwork.get(129);
		// for (LineSegment2D wall : robot.getNearWalls()) {
		// drawLineSegment2D(wall, g, Color.blue);
		// }
		// System.out.println(robot.getSweepCircularSector());
		// for (EvsmRobot evsmRobot : robot.getConnectedEvsmRobots()) {
		// drawRobot(evsmRobot, g, Color.blue);
		// System.out.println(evsmRobot.id + " : " + robot.getAngle(evsmRobot));
		// }
		// System.out.println(robot.getSweepCircularSector().contains(new
		// Point2D(0, 0)));
		// System.out.println(robot.getSweepCircularSector().contains(new
		// Point2D(400, 0)));
		// System.out.println(robot.getSweepCircularSector().contains(new
		// Point2D(400, 400)));
		// System.out.println(robot.getSweepCircularSector().contains(new
		// Point2D(0, 400)));
		// // System.out.println("----------------");
		// System.out.println(robot.toString());
		// System.out.println(sensorNetwork.getCoverage());
	}

	public void drawRobot(Robot robot, Graphics g) {
		super.drawRobot(robot, g);
		EvsmRobot evsmRobot = (EvsmRobot) robot;
		if (evsmRobot.selfOrganizingForce.getNorm() > 1) {
			drawVector(evsmRobot, evsmRobot.selfOrganizingForce.expandTo(Math.log(evsmRobot.selfOrganizingForce.getNorm())), g, Color.magenta);
		}
		if (evsmRobot.exploratoryForce.getNorm() > 1) {
			drawVector(evsmRobot, evsmRobot.exploratoryForce.expandTo(Math.log(evsmRobot.exploratoryForce.getNorm())), g, Color.blue);
		}
		if (evsmRobot.isEdge) {
			drawCircle(new Circle(robot, Math.max(robot.size, minRobotSize)), g, Color.red, true);
		}
	}
}
