package mobilesensornetwork;

import sensornetwork.SensorParameters;

public class SensorRobotParameters extends SensorParameters {
	private Double maxSpeed;
	private Double minSpeed = 0.0;
	private Double maxAcceleration = Double.MAX_VALUE;

	public Double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Double getMinSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(Double minSpeed) {
		this.minSpeed = minSpeed;
	}

	public Double getMaxAcceleration() {
		return maxAcceleration;
	}

	public void setMaxAcceleration(Double maxAcceleration) {
		this.maxAcceleration = maxAcceleration;
	}
}
