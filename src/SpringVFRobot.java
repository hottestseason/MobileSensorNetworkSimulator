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
		if (sensibleRobots.size() == 1) {
			connect(sensibleRobots.get(0));
			sensibleRobots.get(0).connect(this);
		} else {
			for (Robot robot1 : sensibleRobots) {
				for (Robot robot2 : sensibleRobots) {
					if (!robot1.equals(robot2) && robot1.canSense(robot2)) {
						if (!robot1.canSense(robot2) || isDelaunayTriangle(robot1, robot2)) {
							connect(robot1);
							connect(robot2);
							robot1.connect(robot2);
						}
					}
				}
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
