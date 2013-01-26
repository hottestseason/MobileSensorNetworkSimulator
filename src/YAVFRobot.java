import geom.LineSegment2D;
import geom.Spring;
import geom.Vector2D;

import java.awt.Graphics;

public class YAVFRobot extends SpringVFRobot {
	Double springConstant = 0.0;

	public YAVFRobot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval) {
		super(sensorNetwork, id, wirelessRange, sensorRange, weight, size, iterateInterval);
	}

	public Vector2D getVirtualForce() {
		Vector2D force = new Vector2D();
		// for (LineSegment2D wall : getSensibleWalls()) {
		// force = force.add(getVirtualForceFrom(wall));
		// }
		return super.getVirtualForce().add(force);
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		if (atSamePoint(robot)) {
			return new Vector2D();
		} else {
			Vector2D vector = getVector2DTo(robot);
			Vector2D force = Spring.getForce(vector, idealDistance, springConstant);
			return force;
		}
	}

	public Vector2D getVirtualForceFrom(LineSegment2D wall) {
		if (getDistanceFrom(wall) > 0 && getDistanceFrom(wall) < sensorRange / 2) {
			return Spring.getForce(getVector2DTo(wall), sensorRange / 2, 0.4);
		} else {
			return new Vector2D();
		}
	}
}

@SuppressWarnings("serial")
class YAVFMobileSensorNetworkCanvas extends VFMobileSensorNetworkCanvas {
	public YAVFMobileSensorNetworkCanvas(SensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawRobot(Robot robot, Graphics g) {
		super.drawRobot(robot, g);
	}

	public void debug(Graphics g) {
		// System.out.println(sensorNetwork.get(5).point.getDistanceFrom(sensorNetwork.get(11).point));
	}
}
