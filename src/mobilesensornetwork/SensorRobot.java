package mobilesensornetwork;

import geom.Obstacle2D;
import geom.Vector2D;

import java.util.List;

import sensornetwork.SensorNode;

public class SensorRobot extends SensorNode {
	private SensorRobotParameters parameters;
	private Double movementConsumedEnergy = 0.0;
	private Vector2D speed = new Vector2D();
	private Vector2D acceleration = new Vector2D();

	public SensorRobot(SensorRobotParameters parameters) {
		super();
		this.parameters = parameters;
	}

	public SensorRobotParameters getParameters() {
		return parameters;
	}

	public Double getMaxSpeed() {
		return getParameters().getMaxSpeed();
	}

	public Double getMinSpeed() {
		return getParameters().getMinSpeed();
	}

	public Double getMaxAcceleration() {
		return getParameters().getMaxAcceleration();
	}

	public Vector2D getSpeed() {
		return speed;
	}

	public void setSpeed(Vector2D speed) {
		this.speed = speed;
	}

	public Vector2D getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2D acceleration) {
		if (acceleration.getNorm() > getMaxAcceleration()) {
			this.acceleration = acceleration.expandTo(getMaxAcceleration());
		} else {
			this.acceleration = acceleration;
		}
	}

	public List<SensorRobot> getConnectedSensorRobots() {
		return (List<SensorRobot>) (List<?>) getConnectedSensorNodes();
	}

	public Double getMovementConsumedEnergy() {
		return movementConsumedEnergy;
	}

	public Boolean consumeMovementEnergy(Double energy) {
		if (!isSinkNode()) {
			if (canConsumeEnergy(energy)) {
				this.movementConsumedEnergy += energy;
				return true;
			} else {
				stop();
				return false;
			}
		} else {
			return true;
		}
	}

	public Double getConsumedEnergy() {
		return getTransmissionConsumedEnergy() + getSensingConsumedEnergy() + getMovementConsumedEnergy();
	}

	public Double getAccelerateTime(Double seconds) {
		if (speed.add(getAcceleration().multiply(seconds)).getNorm() > getMaxSpeed()) {
			return (getMaxSpeed() - speed.getNorm()) / getAcceleration().getNorm();
		} else {
			return seconds;
		}
	}

	public Vector2D getDisplacement(Double seconds) {
		Vector2D displacement = getSpeed().multiply(seconds).add(getAcceleration().multiply(Math.pow(seconds, 2.0) / 2)).add(getAcceleration().multiply(Math.pow(seconds - getAccelerateTime(seconds), 2) / 2));
		if (displacement.getNorm() / seconds < getMinSpeed()) {
			return new Vector2D();
		}
		for (SensorRobot sensorRobot : getConnectedSensorRobots()) {
			if (sensorRobot != this) {
				displacement = getCircle().getDisplacementToAvoidCollisionFrom(displacement, sensorRobot.getCircle());
			}
		}
		for (Obstacle2D obstacle : getMobileSensorNetwork().getObstacles()) {
			displacement = obstacle.getDisplacementToAvoidCollision(getCircle(), displacement);
		}
		return displacement;
	}

	public Vector2D move(Double seconds) {
		Vector2D displacement = getDisplacement(seconds);
		Double distance = displacement.getNorm();
		setPoint(add(displacement));
		setSpeed(displacement.expandTo(speed.add(getAcceleration().multiply(getAccelerateTime(seconds))).innerProduct(displacement.normalize())));
		consumeMovementEnergy(calculateMoveEnergy(distance));
		return displacement;
	}

	public void stop() {
		super.stop();
		speed = new Vector2D();
		acceleration = new Vector2D();
	}

	protected MobileSensorNetwork getMobileSensorNetwork() {
		return (MobileSensorNetwork) getSensorNetwork();
	}

	private Double calculateMoveEnergy(Double distance) {
		return distance * 0.6 * getWeight();
	}
}
