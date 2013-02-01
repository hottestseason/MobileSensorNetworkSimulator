import geom.Circle;
import geom.Spring;
import geom.Vector2D;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class SpringVFRobot extends Robot {
	Double idealDistance = 0.0;
	Double springConstant = 0.0;

	public static Double calculateIdealDistance(Double wirelessRange, Double sensorRange) {
		return sensorRange * Math.sqrt(3);
	}

	public SpringVFRobot(RobotParameters parameters) {
		super(parameters);
		idealDistance = calculateIdealDistance(getWirelessRange(), getSensorRange());
	}

	public SpringVFRobot clone() {
		SpringVFRobot cloned = (SpringVFRobot) super.clone();
		cloned.idealDistance = idealDistance;
		cloned.springConstant = springConstant;
		cloned.dampingCoefficient = dampingCoefficient;
		return cloned;
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

@SuppressWarnings("serial")
class SpringVFMobileSensorNetworkCanvas extends SensorNetworkCanvas {
	public SpringVFMobileSensorNetworkCanvas(SensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawRobot(Robot robot, Graphics g) {
		super.drawRobot(robot, g);
		drawCircle(new Circle(robot, ((SpringVFRobot) robot).idealDistance), g, new Color(0, 0, 0, 32), false);
	}
}