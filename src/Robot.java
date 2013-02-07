import geom.Circle;
import geom.LineSegment2D;
import geom.Obstacle2D;
import geom.Point2D;
import geom.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Robot extends Node {
	RobotParameters parameters;
	Double iterateInterval;
	Double dampingCoefficient = 0.0;
	Vector2D speed = new Vector2D();
	Vector2D virutalForce = new Vector2D();
	Vector2D dampingForce = new Vector2D();

	Double consumedEnergy = 0.0;
	Double movedDistance = 0.0;
	private HashMap<String, Object> memory = new HashMap<String, Object>();

	public Robot(RobotParameters parameters) {
		this.parameters = parameters;
	}

	public Double getSize() {
		return parameters.size;
	}

	public Double getWeight() {
		return parameters.weight;
	}

	public Double getWirelessRange() {
		return parameters.wirelessRange;
	}

	public Double getSensorRange() {
		return parameters.sensorRange;
	}

	public Double getMaxSpeed() {
		return parameters.maxSpeed;
	}

	public Double getMinSpeed() {
		return parameters.minSpeed;
	}

	public Double getMaxAcceleration() {
		return parameters.maxAcceleration;
	}

	public Circle getCircle() {
		return new Circle(this, getSize());
	}

	public Circle getWirelessCircle() {
		return new Circle(this, getWirelessRange());
	}

	public Circle getSensorCircle() {
		return new Circle(this, getSensorRange());
	}

	public Vector2D getAppliedForce() {
		return virutalForce.add(dampingForce);
	}

	public Object getMemory(String key) {
		return memory.get(key);
	}

	public void setMemory(String key, Object value) {
		memory.put(key, value);
	}

	public String toString() {
		String ret = id + " : ";
		ret += toPoint2D() + " -> " + getNextPoint() + "\n";
		ret += "speed : " + speed + "\n";
		ret += "virtualForce: " + virutalForce + "\ndampingForce: " + dampingForce + "\n";
		ret += "appliedForce : " + getAppliedForce() + "\n";
		ret += "acceleration : " + getAcceleration();
		return ret;
	}

	public void iterate() {
		resetState();
		setUpForIteration();
		calculateForce();
	}

	public void resetState() {
		virutalForce = new Vector2D();
		dampingForce = new Vector2D();
		setMemory("sensibleRobots", null);
		clearConnections();
	}

	public void setUpForIteration() {
		for (Robot robot : getSensibleRobots()) {
			sendTo(robot, 1 * 1000.0 * 8);
		}
		createConnections();
	}

	public void calculateForce() {
		virutalForce = getVirtualForce();
		dampingForce = getDampingForce();
	}

	public Vector2D getVirtualForce() {
		Vector2D force = new Vector2D();
		for (Robot robot : getConnectedRobots()) {
			force = force.add(getVirtualForceFrom(robot));
		}
		return force;
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		return new Vector2D();
	}

	public Vector2D getDampingForce() {
		return speed.multiply(dampingCoefficient).reverse();
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
		if (!new Circle(this, getWirelessRange()).contains(point)) {
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
		return new Circle(this, getWirelessRange()).contains(lineSegment);
	}

	public ArrayList<Robot> getSensibleRobots() {
		ArrayList<Robot> sensibleRobots = (ArrayList<Robot>) getMemory("sensibleRobots");
		if (sensibleRobots == null) {
			sensibleRobots = new ArrayList<Robot>();
			for (Robot robot : ((SensorNetwork) graph).getRobots()) {
				if (canSense(robot)) {
					sensibleRobots.add(robot);
				}
			}
			setMemory("sensibleRobots", sensibleRobots);
		}
		return sensibleRobots;
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

	public Vector2D getAcceleration() {
		Vector2D acceleration = getAppliedForce().multiply(1 / getWeight());
		if (acceleration.getNorm() > getMaxAcceleration()) {
			return acceleration.expandTo(getMaxAcceleration());
		} else {
			return acceleration;
		}
	}

	public Double getAccelerateTime(Double seconds) {
		if (speed.add(getAcceleration().multiply(seconds)).getNorm() > getMaxSpeed()) {
			return (getMaxSpeed() - speed.getNorm()) / getAcceleration().getNorm();
		} else {
			return seconds;
		}
	}

	public Vector2D getDisplacement(Double seconds) {
		Vector2D displacement = speed.multiply(seconds).add(getAcceleration().multiply(Math.pow(seconds, 2.0) / 2)).add(getAcceleration().multiply(Math.pow(seconds - getAccelerateTime(seconds), 2) / 2));
		if (displacement.getNorm() / seconds < getMinSpeed()) {
			return new Vector2D();
		}
		for (Robot robot : getConnectedRobots()) {
			if (robot != this) {
				displacement = getCircle().getDisplacementToAvoidCollisionFrom(displacement, robot.getCircle());
			}
		}
		for (Obstacle2D obstacle : getSensorNetwork().obstacles) {
			displacement = obstacle.getDisplacementToAvoidCollision(getCircle(), displacement);
		}
		return displacement;
	}

	public Point2D getNextPoint() {
		return add(getDisplacement(iterateInterval));
	}

	public Vector2D move(Double seconds) {
		Vector2D displacement = getDisplacement(seconds);
		Double distance = displacement.getNorm();
		setPoint(add(displacement));
		speed = displacement.expandTo(speed.add(getAcceleration().multiply(getAccelerateTime(seconds))).innerProduct(displacement.normalize()));
		movedDistance += distance;
		consumedEnergy += distance * 0.8 * getWeight();
		return displacement;
	}

	public void send(Double distance, Double bit) {
		consumedEnergy += (bit * 50 + 0.1 * bit * distance * distance) * Math.pow(10, -9);
	}

	public void sendTo(Robot to, Double bit) {
		send(getDistanceFrom(to), bit);
		to.receive(bit);
	}

	public void receive(Double bit) {
		consumedEnergy += bit * 50 * Math.pow(10, -9);
	}

	protected SensorNetwork getSensorNetwork() {
		return (SensorNetwork) graph;
	}

	public Boolean isDelaunayTriangle(Node node2, Node node3) {
		if (this.equals(node2) || this.equals(node3) || node2.equals(node3)) {
			return false;
		} else {
			Circle circle = new Circle(this, node2, node3);
			for (Node node : getSensibleRobots()) {
				if (!node.equals(this) && !node.equals(node2) && !node.equals(node3)) {
					if (circle.contains(node)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Boolean ggTest(Robot robot) {
		if (robot.equals(this)) {
			return false;
		} else {
			Circle circle = new Circle(new LineSegment2D(this, robot));
			for (Robot another : getSensibleRobots()) {
				if (!another.equals(this) && !another.equals(robot)) {
					if (circle.containsExcludeEdge(another)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Boolean rngTest(Robot robot) {
		if (robot.equals(this)) {
			return false;
		} else {
			Double distance = getDistanceFrom(robot);
			for (Robot another : getSensibleRobots()) {
				if (!another.equals(this) && !another.equals(robot)) {
					if (Math.max(getDistanceFrom(another), robot.getDistanceFrom(another)) < distance) {
						return false;
					}
				}
			}
			return true;
		}
	}
}

class RobotParameters {
	Double size;
	Double weight;
	Double wirelessRange;
	Double sensorRange;
	Double maxSpeed;
	Double minSpeed;
	Double maxAcceleration;
}