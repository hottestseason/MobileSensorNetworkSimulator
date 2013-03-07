package mobilesensornetwork;

import geom.Spring;
import geom.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class SpringVFRobot extends VFRobot {
	protected Double springConstant = 0.0;
	protected ArrayList<SpringVFRobot> springConnectedRobots = new ArrayList<SpringVFRobot>();

	public static Double calculateIdealDistance(Double wirelessRange, Double sensorRange) {
		return sensorRange * Math.sqrt(3);
	}

	public SpringVFRobot(SensorRobotParameters parameters) {
		super(parameters);
	}

	public Double getSpringConstant() {
		return springConstant;
	}

	public void setSpringConstant(Double springConstant) {
		this.springConstant = springConstant;
	}

	public List<SpringVFRobot> getSpringConnectedRobots() {
		return springConnectedRobots;
	}

	public void resetState() {
		springConnectedRobots.clear();
		super.resetState();
	}

	public void createSpringConnections() {
		for (SensorRobot robot : getConnectedSensorRobots()) {
			if (canCreateSpringWith(robot)) {
				springConnectedRobots.add((SpringVFRobot) robot);
			}
		}
	}

	public Vector2D getVirtualForceFrom(SensorRobot sensorRobot) {
		if (springConnectedRobots.contains(sensorRobot)) {
			return Spring.getForce(getVector2DTo(sensorRobot), getSpringLengthFor(sensorRobot), getSpringConstant());
		} else {
			return new Vector2D();
		}
	}

	public Boolean canCreateSpringWith(SensorRobot sensorRobot) {
		return !isAtSamePoint(sensorRobot) && ggTest(sensorRobot);
	}

	public Double getSpringLengthFor(SensorRobot sensorRobot) {
		return calculateIdealDistance(getWirelessRange(), getSensorRange());
	}
}