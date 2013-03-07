package mobilesensornetwork;

import geom.Vector2D;

import java.util.HashMap;

public class SmartSpringVFRobot extends SpringVFRobot {
	Integer sensingInterval;
	MobileSensorNetwork lastSensedNeighbors = new MobileSensorNetwork();
	HashMap<Integer, Integer> neighborConnectedRobots = new HashMap<Integer, Integer>();

	public SmartSpringVFRobot(SensorRobotParameters parameters) {
		super(parameters);
	}

	public SpringVFRobot cloneToSpringVFRobot() {
		SpringVFRobot cloned = new SpringVFRobot(getParameters());
		cloned.setDampingCoefficient(dampingCoefficient);
		cloned.setSpringConstant(springConstant);
		cloned.setPoint(this);
		cloned.setSpeed(getSpeed());
		return cloned;
	}

	// public void iterate() {
	// if (getMobileSensorNetwork().getIterationNo() % sensingInterval == 1) {
	// super.iterate();
	// lastSensedNeighbors = new MobileSensorNetwork();
	// lastSensedNeighbors.add(cloneToSpringVFRobot());
	// neighborConnectedRobots.put(0, getConnectedSensorRobots().size());
	// for (SensorRobot sensorRobot : getConnectedSensorRobots()) {
	// SmartSpringVFRobot smartSpringVFRobot = (SmartSpringVFRobot) sensorRobot;
	// lastSensedNeighbors.add(smartSpringVFRobot.cloneToSpringVFRobot());
	// neighborConnectedRobots.put(lastSensedNeighbors.size() - 1,
	// sensorRobot.getConnectedSensorRobots().size());
	// }
	// iterateLasteSensedNeighbors();
	// } else {
	// resetState();
	// setUpForIteration();
	// SpringVFRobot cloneOfThis = (SpringVFRobot) lastSensedNeighbors.get(0);
	// cloneOfThis.setPoint(this);
	// cloneOfThis.setSpeed(getSpeed().clone());
	// iterateLasteSensedNeighbors();
	// virutalForce = cloneOfThis.virutalForce.clone();
	// dampingForce = cloneOfThis.dampingForce.clone();
	// }
	// }
	//
	// public void iterateLasteSensedNeighbors() {
	// lastSensedNeighbors.iterate();
	// for (SensorRobot sensorRobot : lastSensedNeighbors.getSensorRobots()) {
	// VFRobot vfRobot = (VFRobot) sensorRobot;
	// vfRobot.virutalForce =
	// vfRobot.virutalForce.divide(Math.max(neighborConnectedRobots.get(sensorRobot.getId()),
	// 1.0));
	// }
	// }

	public Vector2D move(Double seconds) {
		lastSensedNeighbors.move(seconds);
		return super.move(seconds);
	}
}

// @SuppressWarnings("serial")
// class SmartSpringVFMobileSensorNetworkCanvas extends
// SpringVFMobileSensorNetworkCanvas {
// public SmartSpringVFMobileSensorNetworkCanvas(MobileSensorNetwork
// sensorNetwork) {
// super(sensorNetwork);
// }
//
// public void drawRobot(SensorRobot sensorRobot, Graphics g) {
// super.drawRobot(sensorRobot, g);
// SmartSpringVFRobot smartSpringVFRobot = (SmartSpringVFRobot) sensorRobot;
// synchronized (smartSpringVFRobot.lastSensedNeighbors) {
// for (SensorRobot neighborRobot :
// smartSpringVFRobot.lastSensedNeighbors.getSensorRobots()) {
// drawCircle(new Circle(neighborRobot, Math.max(neighborRobot.getSize(),
// minRobotSize) / 2), g, Color.red, true);
// }
// }
// }
// }