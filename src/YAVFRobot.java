import geom.LineSegment2D;
import geom.Spring;
import geom.Vector2D;

import java.awt.Graphics;
import java.util.ArrayList;

public class YAVFRobot extends VFRobot {
	Double springConstant = 0.0;

	public YAVFRobot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval) {
		super(sensorNetwork, id, wirelessRange, sensorRange, weight, size, iterateInterval);
	}

	public void setUpForIteration() {
		super.setUpForIteration();
	}

	public Vector2D getVirtualForce() {
		Vector2D force = new Vector2D();
		// for (LineSegment2D wall : getSensibleWalls()) {
		// force = force.add(getVirtualForceFrom(wall));
		// }
		return super.getVirtualForce().add(force);
	}

	public void createConnections() {
		ArrayList<Robot> sensibleRobots = getSensibleRobots();
		// for (Robot robot : sensibleRobots) {
		// if (acuteAngleTest(robot)) {
		// connect(robot);
		// }
		// }
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
