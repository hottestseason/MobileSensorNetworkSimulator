package mobilesensornetwork;

public class VfMobileSensorNetwork extends MobileSensorNetwork {
	public void calculateVirtualForce() {
		for (SensorRobot sensorRobot : getSensorRobots()) {
			if (sensorRobot.isRunning()) {
				((VFRobot) sensorRobot).calculateVirtualForce();
			}
		}
	}
}
