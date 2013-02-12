package mobilesensornetwork;

import geom.Circle;
import geom.Spring;
import geom.Vector2D;

import java.awt.Color;
import java.awt.Graphics;

public class SpringVFRobot extends VFRobot {
	Double idealDistance = 0.0;
	protected Double springConstant = 0.0;

	public static Double calculateIdealDistance(Double wirelessRange, Double sensorRange) {
		return sensorRange * Math.sqrt(3);
	}

	public SpringVFRobot(RobotParameters parameters) {
		super(parameters);
		idealDistance = calculateIdealDistance(getWirelessRange(), getSensorRange());
	}

	public Double getSpringConstant() {
		return springConstant;
	}

	public void setSpringConstant(Double springConstant) {
		this.springConstant = springConstant;
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		if (isAtSamePoint(robot) || !ggTest(robot)) {
			return new Vector2D();
		} else {
			return Spring.getForce(getVector2DTo(robot), idealDistance, getSpringConstant());
		}
	}
}

@SuppressWarnings("serial")
class SpringVFMobileSensorNetworkCanvas extends SensorNetworkCanvas {
	public SpringVFMobileSensorNetworkCanvas(MobileSensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawRobot(Robot robot, Graphics g) {
		super.drawRobot(robot, g);
		drawCircle(new Circle(robot, ((SpringVFRobot) robot).idealDistance), g, new Color(0, 0, 0, 32), false);
	}
}