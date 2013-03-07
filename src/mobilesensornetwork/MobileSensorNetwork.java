package mobilesensornetwork;

import geom.Vector2D;

import java.util.ArrayList;
import java.util.Random;

import sensornetwork.SensorNetwork;

public class MobileSensorNetwork extends SensorNetwork {
	private Double sumMovedDistance = 0.0;

	public void scatter(Double width, Double height, Random random) {
		for (SensorRobot robot : getSensorRobots()) {
			robot.setPoint(Vector2D.random(width, height, random).add(sensingArea.getWidth() / 2, sensingArea.getWidth() / 2).toPoint2D());
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
		for (SensorRobot sensorRobot : getSensorRobots()) {
			if (sensorRobot.getId() == 0) {
				continue;
			}
			movedDistance += sensorRobot.move(seconds).getNorm();
		}
		sumMovedDistance += movedDistance;
		return movedDistance;
	}
}
