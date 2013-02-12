package mobilesensornetwork;

import geom.Circle;
import geom.Vector2D;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

public class SmartSpringVFRobot extends SpringVFRobot {
	Integer sensingInterval;
	MobileSensorNetwork lastSensedNeighbors = new MobileSensorNetwork();
	HashMap<Integer, Integer> neighborConnectedRobots = new HashMap<Integer, Integer>();

	public SmartSpringVFRobot(RobotParameters parameters) {
		super(parameters);
	}

	public SpringVFRobot cloneToSpringVFRobot() {
		SpringVFRobot cloned = new SpringVFRobot(getParameters());
		cloned.setDampingCoefficient(dampingCoefficient);
		cloned.setSpringConstant(springConstant);
		cloned.setIterateInterval(getIterateInterval());
		cloned.setPoint(this);
		cloned.setSpeed(getSpeed());
		return cloned;
	}

	public void iterate() {
		if (getSensorNetwork().getIterationNo() % sensingInterval == 1) {
			super.iterate();
			lastSensedNeighbors = new MobileSensorNetwork();
			lastSensedNeighbors.add(cloneToSpringVFRobot());
			neighborConnectedRobots.put(0, getConnectedRobots().size());
			for (Robot robot : getConnectedRobots()) {
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
			cloneOfThis.setSpeed(getSpeed().clone());
			iterateLasteSensedNeighbors();
			virutalForce = cloneOfThis.virutalForce.clone();
			dampingForce = cloneOfThis.dampingForce.clone();
		}
	}

	public void iterateLasteSensedNeighbors() {
		lastSensedNeighbors.iterate();
		for (Robot robot : lastSensedNeighbors.getRobots()) {
			VFRobot vfRobot = (VFRobot) robot;
			vfRobot.virutalForce = vfRobot.virutalForce.divide(Math.max(neighborConnectedRobots.get(robot.getId()), 1.0));
		}
	}

	public Vector2D move(Double seconds) {
		lastSensedNeighbors.move(seconds);
		return super.move(seconds);
	}
}

@SuppressWarnings("serial")
class SmartSpringVFMobileSensorNetworkCanvas extends SpringVFMobileSensorNetworkCanvas {
	public SmartSpringVFMobileSensorNetworkCanvas(MobileSensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawRobot(Robot robot, Graphics g) {
		super.drawRobot(robot, g);
		SmartSpringVFRobot smartSpringVFRobot = (SmartSpringVFRobot) robot;
		synchronized (smartSpringVFRobot.lastSensedNeighbors) {
			for (Robot neighborRobot : smartSpringVFRobot.lastSensedNeighbors.getRobots()) {
				drawCircle(new Circle(neighborRobot, Math.max(neighborRobot.getSize(), minRobotSize) / 2), g, Color.red, true);
			}
		}
	}
}