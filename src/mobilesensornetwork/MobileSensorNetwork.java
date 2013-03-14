package mobilesensornetwork;

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
		long before = System.nanoTime();
		Double movedDistance = 0.0;
		for (NetworkNode node : getRunningNodes()) {
			if (node.getId() != 0) {
				SensorRobot sensorRobot = (SensorRobot) node;
				movedDistance += sensorRobot.move(seconds).getNorm();
			}
		}
		sumMovedDistance += movedDistance;
		System.out.print("move " + (System.nanoTime() - before) / 1000L + "us ");
		return movedDistance;
	}
}
