package mobilesensornetwork;

import geom.LineSegment2D;
import geom.Spring;
import geom.Vector2D;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class YAVFRobot extends SpringVFRobot {
	Vector2D forceFromNeighborRobots = new Vector2D();
	Vector2D expulsiveForceFromWall = new Vector2D();

	public YAVFRobot(RobotParameters parameters) {
		super(parameters);
	}

	public Vector2D getVirtualForce() {
		forceFromNeighborRobots = new Vector2D();
		for (Robot robot : getConnectedRobots()) {
			forceFromNeighborRobots = forceFromNeighborRobots.add(getVirtualForceFrom(robot));
		}
		expulsiveForceFromWall = new Vector2D();
		ArrayList<LineSegment2D> sensibleWalls = getSensibleWalls();
		for (LineSegment2D wall : sensibleWalls) {
			expulsiveForceFromWall = expulsiveForceFromWall.add(getVirtualForceFrom(wall).divide((double) sensibleWalls.size()));
		}
		return forceFromNeighborRobots.add(expulsiveForceFromWall);
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		if (isAtSamePoint(robot)) {
			return new Vector2D();
		} else {
			return Spring.getForce(getVector2DTo(robot), idealDistance, getSpringConstant());
		}
	}

	public Vector2D getVirtualForceFrom(LineSegment2D wall) {
		Double distanceFromWall = getDistanceFrom(wall);
		if (distanceFromWall > 0 && distanceFromWall < getSensorRange() / 2) {
			return Spring.getForce(getVector2DTo(wall), getSensorRange() / 2, getSpringConstant() * 2);
		} else {
			return new Vector2D();
		}
	}
}

@SuppressWarnings("serial")
class YAVFMobileSensorNetworkCanvas extends SpringVFMobileSensorNetworkCanvas {
	public YAVFMobileSensorNetworkCanvas(MobileSensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawRobot(Robot robot, Graphics g) {
		super.drawRobot(robot, g);
		YAVFRobot yavfRobot = (YAVFRobot) robot;
		drawVector(yavfRobot, fixForce(yavfRobot.expulsiveForceFromWall), g, Color.green);
	}

	public void debug(Graphics g) {
		// System.out.println(sensorNetwork.get(5).point.getDistanceFrom(sensorNetwork.get(11).point));
	}
}
