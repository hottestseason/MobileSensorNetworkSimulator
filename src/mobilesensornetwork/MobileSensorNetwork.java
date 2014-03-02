package mobilesensornetwork;

import geom.Point2D;

import java.util.ArrayList;

import network.NetworkNode;
import sensornetwork.SensorNetwork;
import utils.RandomUtils;

public class MobileSensorNetwork extends SensorNetwork {
	private Double sumMovedDistance = 0.0;

	public void scatter(Double width, Double height) {
		for (SensorRobot robot : getSensorRobots()) {
			robot.setPoint(RandomUtils.nextVector(width, height).add(sensingArea.getWidth() / 2, sensingArea.getWidth() / 2).toPoint2D());
		}
	}

	public void scatter(Point2D point, Double width, Double height) {
		for (SensorRobot robot : getSensorRobots()) {
			robot.setPoint(RandomUtils.nextVector(width, height).add(point).toPoint2D());
		}
	}

	public Double getSumMovedDistance() {
		return sumMovedDistance;
	}

	public SensorRobot get(int index) {
		return (SensorRobot) super.get(index);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<SensorRobot> getSensorRobots() {
		return (ArrayList<SensorRobot>) (ArrayList<?>) this;
	}

	public Double move(Double seconds) {
		Double movedDistance = 0.0;
		for (NetworkNode node : getRunningNodes()) {
			if (node.getId() != 0) {
				SensorRobot sensorRobot = (SensorRobot) node;
				movedDistance += sensorRobot.move(seconds).getNorm();
			}
		}
		sumMovedDistance += movedDistance;
		return movedDistance;
	}
}
