import geom.Circle;
import geom.Vector2D;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

public class SmartSpringVFRobot extends SpringVFRobot {
	Integer sensingInterval;
	SensorNetwork lastSensedNeighbors = new SensorNetwork();
	HashMap<Integer, Integer> neighborConnectedRobots = new HashMap<Integer, Integer>();

	public SmartSpringVFRobot(RobotParameters parameters) {
		super(parameters);
	}

	public SpringVFRobot cloneToSpringVFRobot() {
		SpringVFRobot cloned = new SpringVFRobot(parameters);
		cloned.dampingCoefficient = dampingCoefficient;
		cloned.springConstant = springConstant;
		cloned.iterateInterval = iterateInterval;
		cloned.setPoint(this);
		cloned.speed = speed;
		return cloned;
	}

	public void iterate() {
		if (getSensorNetwork().iterateNo % sensingInterval == 1) {
			super.iterate();
			lastSensedNeighbors = new SensorNetwork();
			lastSensedNeighbors.add(cloneToSpringVFRobot());
			neighborConnectedRobots.put(0, getConnectedRobots().size());
			for (Robot robot : getSensibleRobots()) {
				SmartSpringVFRobot smartSpringVFRobot = (SmartSpringVFRobot) robot;
				lastSensedNeighbors.add(smartSpringVFRobot.cloneToSpringVFRobot());
				neighborConnectedRobots.put(lastSensedNeighbors.size() - 1, robot.getConnectedRobots().size());
			}
			iterateLasteSensedNeighbors();
		} else {
			resetState();
			setUpForIteration();
			SpringVFRobot cloneOfThis = (SpringVFRobot) lastSensedNeighbors.get(0);
			cloneOfThis.setPoint(this);
			cloneOfThis.speed = speed.clone();
			iterateLasteSensedNeighbors();
			appliedForce = cloneOfThis.appliedForce.clone();
		}
	}

	public void iterateLasteSensedNeighbors() {
		lastSensedNeighbors.iterate();
		for (Robot robot : lastSensedNeighbors.getRobots()) {
			robot.virutalForce = robot.virutalForce.divide(Math.max(neighborConnectedRobots.get(robot.id), 1.0));
			robot.appliedForce = robot.virutalForce.add(robot.dampingForce);
		}
	}

	public Vector2D move(Double seconds) {
		lastSensedNeighbors.move(seconds);
		return super.move(seconds);
	}
}

@SuppressWarnings("serial")
class SmartSpringVFMobileSensorNetworkCanvas extends SpringVFMobileSensorNetworkCanvas {
	public SmartSpringVFMobileSensorNetworkCanvas(SensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawRobot(Robot robot, Graphics g) {
		super.drawRobot(robot, g);
		SmartSpringVFRobot smartSpringVFRobot = (SmartSpringVFRobot) robot;
		synchronized (sensorNetwork) {
			for (Robot neighborRobot : smartSpringVFRobot.lastSensedNeighbors.getRobots()) {
				drawCircle(new Circle(neighborRobot, Math.max(neighborRobot.getSize(), minRobotSize) / 2), g, Color.red, true);
			}
		}
	}
}