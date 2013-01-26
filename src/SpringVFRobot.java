import geom.Spring;
import geom.Vector2D;

public class SpringVFRobot extends VFRobot {
	Double springConstant = 0.0;

	public SpringVFRobot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval) {
		super(sensorNetwork, id, wirelessRange, sensorRange, weight, size, iterateInterval);
	}

	public SpringVFRobot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval, Double idealDistance, Double springConstant, Double dampingCoefficient) {
		this(sensorNetwork, id, wirelessRange, sensorRange, weight, size, iterateInterval);
		this.idealDistance = idealDistance;
		this.springConstant = springConstant;
		this.dampingCoefficient = dampingCoefficient;
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		if (atSamePoint(robot) || !acuteAngleTest(robot)) {
			return new Vector2D();
		} else {
			Vector2D springVector = getVector2DTo(robot);
			return Spring.getForce(springVector, idealDistance, springConstant);
		}
	}
}
