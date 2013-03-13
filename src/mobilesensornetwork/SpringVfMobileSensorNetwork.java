package mobilesensornetwork;

public class SpringVfMobileSensorNetwork extends VfMobileSensorNetwork {
	public void prepareNodes(Integer size, SensorRobotParameters parameters, Double dampingCoefficient, Double springConstant) {
		for (int i = 0; i < size; i++) {
			SpringVFRobot robot = new SpringVFRobot(parameters);
			robot.setDampingCoefficient(dampingCoefficient);
			robot.setSpringConstant(springConstant);
			add(robot);
		}
	}

	public void createSpringConnections() {
		long before = System.nanoTime();
		for (SensorRobot sensorRobot : getSensorRobots()) {
			if (sensorRobot.isRunning()) {
				((SpringVFRobot) sensorRobot).createSpringConnections();
			}
		}
		System.out.print("springs " + (System.nanoTime() - before) / 1000L + "us ");
	}
}
