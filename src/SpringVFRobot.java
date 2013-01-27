import geom.Spring;
import geom.Vector2D;

import java.util.ArrayList;

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

	public void createConnections() {
		ArrayList<Robot> sensibleRobots = getSensibleRobots();
		for (Robot robot : sensibleRobots) {
			if (ggTest(robot)) {
				connect(robot);
			}
		}
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		if (atSamePoint(robot)) {
			return new Vector2D();
		} else {
			return Spring.getForce(getVector2DTo(robot), idealDistance, springConstant);
		}
	}
}
