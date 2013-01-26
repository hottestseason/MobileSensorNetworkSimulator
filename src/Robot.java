import geom.Circle;
import geom.LineSegment2D;
import geom.Obstacle2D;
import geom.Point2D;
import geom.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class Robot extends Node {
	Vector2D appliedForce = new Vector2D();
	Vector2D speed = new Vector2D();
	Double weight;
	Double wirelessRange;
	Double sensorRange;
	Double iterateInterval;
	Double size;
	Double maxSpeed = 5.0;
	Double minSpeed = 0.01;
	Double maxAcceleration = 1.0;
	Boolean isEdge = false;

	public Robot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval) {
		graph = sensorNetwork;
		this.id = id;
		this.wirelessRange = wirelessRange;
		this.sensorRange = sensorRange;
		this.weight = weight;
		this.size = size;
		this.iterateInterval = iterateInterval;
	}

	public String toString() {
		String ret = id + " : ";
		ret += toPoint2D() + " -> " + getNextPoint() + "\n";
		ret += "speed : " + speed + "\n";
		ret += "appliedForce : " + appliedForce + "\n";
		ret += "acceleration : " + getAcceleration();
		return ret;
	}

	public void iterate() {
		setUpForIteration();
		calculateForce();
	}

	public void setUpForIteration() {
		appliedForce = new Vector2D();
		resetConnections();
		createConnections();
		if (isEdgeNode()) {
			isEdge = true;
		} else {
			isEdge = false;
		}
	}

	public void calculateForce() {
	}

	public void createConnections() {
		for (Robot robot : getSensibleRobots()) {
			connect(robot);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Robot> getConnectedRobots() {
		return (List<Robot>) (List<?>) connectedNodes;
	}

	public Boolean canSense(Point2D point) {
		if (!new Circle(this, wirelessRange).contains(point)) {
			return false;
		} else {
			for (Obstacle2D obstacle : getSensorNetwork().obstacles) {
				for (LineSegment2D wall : obstacle.getEdges()) {
					if (wall.contains(new LineSegment2D(this, point))) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Boolean canSense(Robot robot) {
		return !robot.equals(this) && canSense(robot.toPoint2D());
	}

	public Boolean canSense(LineSegment2D lineSegment) {
		return new Circle(this, wirelessRange).contains(lineSegment);
	}

	public ArrayList<Robot> getSensibleRobots() {
		ArrayList<Robot> robots = new ArrayList<Robot>();
		for (Robot robot : ((SensorNetwork) graph).getRobots()) {
			if (canSense(robot)) {
				robots.add(robot);
			}
		}
		return robots;
	}

	public ArrayList<LineSegment2D> getSensibleWalls() {
		ArrayList<LineSegment2D> walls = new ArrayList<LineSegment2D>();
		for (Obstacle2D obstacle : getSensorNetwork().obstacles) {
			for (LineSegment2D wall : obstacle.getEdges()) {
				if ((obstacle.preknown || canSense(wall))) {
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

	public void applyForce(Vector2D force) {
		appliedForce = appliedForce.add(force);
	}

	public Vector2D getAcceleration() {
		Vector2D acceleration = appliedForce.multiply(1 / weight);
		if (acceleration.getNorm() > maxAcceleration) {
			return acceleration.expandTo(maxAcceleration);
		} else {
			return acceleration;
		}
	}

	public Double getAccelerateTime(Double seconds) {
		if (speed.add(getAcceleration().multiply(seconds)).getNorm() > maxSpeed) {
			return (maxSpeed - speed.getNorm()) / getAcceleration().getNorm();
		} else {
			return seconds;
		}
	}

	public Vector2D getDisplacement(Double seconds) {
		Vector2D displacement = speed.multiply(seconds).add(getAcceleration().multiply(Math.pow(seconds, 2.0) / 2)).add(getAcceleration().multiply(Math.pow(seconds - getAccelerateTime(seconds), 2) / 2));
		if (displacement.getNorm() / seconds < minSpeed) {
			return new Vector2D();
		}
		for (Robot robot : getConnectedRobots()) {
			if (robot != this) {
				displacement = new Circle(this, size).getDisplacementToAvoidCollisionFrom(displacement, new Circle(robot, robot.size));
			}
		}
		for (Obstacle2D obstacle : getSensorNetwork().obstacles) {
			displacement = obstacle.getDisplacementToAvoidCollision(new Circle(this, size), displacement);
		}
		return displacement;
	}

	public Point2D getNextPoint() {
		return add(getDisplacement(iterateInterval));
	}

	public Vector2D move(Double seconds) {
		Vector2D displacement = getDisplacement(seconds);
		setPoint(add(displacement));
		speed = displacement.expandTo(speed.add(getAcceleration().multiply(getAccelerateTime(seconds))).innerProduct(displacement.normalize()));
		return displacement;
	}

	public Boolean isEdgeNode() {
		int splitSize = 36;
		Double maxEdgeDistance = getMaxEdgeDistance();
		for (int i = 0; i < splitSize; i++) {
			Double angle = 2 * Math.PI * i / splitSize;
			Point2D aroundPoint = add(new Vector2D(Math.cos(angle), Math.sin(angle)).multiply(wirelessRange));
			Boolean flag = true;
			for (Robot robot : getConnectedRobots()) {
				if (new Circle(robot, maxEdgeDistance * 1.05).contains(aroundPoint)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		return false;
	}

	protected SensorNetwork getSensorNetwork() {
		return (SensorNetwork) graph;
	}

	protected Double getMaxEdgeDistance() {
		Double maxDistance = 0.0;
		for (Robot robot : getConnectedRobots()) {
			maxDistance = Math.max(maxDistance, getDistanceFrom(robot));
		}
		return maxDistance;
	}
}
