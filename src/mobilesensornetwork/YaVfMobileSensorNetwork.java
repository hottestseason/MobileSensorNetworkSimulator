package mobilesensornetwork;

public class YaVfMobileSensorNetwork extends SpringVfMobileSensorNetwork {
	public String toString() {
		YaVfRobot node = (YaVfRobot) getSinkNode();
		return "Damping: " + node.getDampingCoefficient() + ", Spring: " + node.getSpringConstant();
	}

	public void prepareNodes(Integer size, SensorRobotParameters parameters, Double dampingCoefficient, Double springConstant) {
		for (int i = 0; i < size; i++) {
			YaVfRobot robot = new YaVfRobot(parameters);
			robot.setDampingCoefficient(dampingCoefficient);
			robot.setSpringConstant(springConstant);
			add(robot);
			if (i >= 7) {
				robot.stop();
			}
		}
	}

	public void adjustNodeSize() {
		SpringVFRobot springVFSinkRobot = (SpringVFRobot) getSinkNode();
		for (SpringVFRobot springVFRobot : springVFSinkRobot.getSpringConnectedRobots()) {
			if (springVFSinkRobot.getDistanceFrom(springVFRobot) >= springVFSinkRobot.getSpringLengthFor(springVFRobot) * 0.99) {
				for (SensorRobot sensorRobot : getSensorRobots()) {
					if (!sensorRobot.isRunning() && sensorRobot.getRemainedBatteryRatio() > 0.9) {
						sensorRobot.setPoint(springVFSinkRobot.multiply(0.5).add(springVFRobot.multiply(0.5)).toPoint2D());
						sensorRobot.start();
						break;
					}
				}
			}
		}
	}
}
