package mobilesensornetwork;

public class VfMobileSensorNetwork extends MobileSensorNetwork {
	public void calculateVirtualForce() {
		long before = System.nanoTime();
		for (SensorRobot sensorRobot : getSensorRobots()) {
			if (sensorRobot.isRunning()) {
				((VFRobot) sensorRobot).calculateVirtualForce();
			}
		}
		System.out.print("virtualForce " + (System.nanoTime() - before) / 1000L + "us ");
	}
}
