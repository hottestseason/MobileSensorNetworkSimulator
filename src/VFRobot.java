import geom.Circle;
import geom.Vector2D;

import java.awt.Color;
import java.awt.Graphics;

public class VFRobot extends Robot {
	Double idealDistance = 0.0;
	Double attractiveCoefficient = 0.0;
	Double repulsiveCoefficient = 0.0;
	Double dampingCoefficient = 0.0;
	Vector2D virutalForce = new Vector2D();
	Vector2D dampingForce = new Vector2D();

	public VFRobot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval) {
		super(sensorNetwork, id, wirelessRange, sensorRange, weight, size, iterateInterval);
	}

	public VFRobot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval, Double idealDistance, Double attractiveCoefficient, Double repulsiveCoefficient, Double dampingCoefficient) {
		this(sensorNetwork, id, wirelessRange, sensorRange, weight, size, iterateInterval);
		this.idealDistance = idealDistance;
		this.attractiveCoefficient = attractiveCoefficient;
		this.repulsiveCoefficient = repulsiveCoefficient;
		this.dampingCoefficient = dampingCoefficient;
	}

	public String toString() {
		return super.toString() + "\nvirtualForce: " + virutalForce + "\ndampingForce: " + dampingForce;
	}

	public void setUpForIteration() {
		super.setUpForIteration();
		virutalForce = new Vector2D();
		dampingForce = new Vector2D();
	}

	public void calculateForce() {
		virutalForce = getVirtualForce();
		dampingForce = getDampingForce();
		applyForce(virutalForce.add(dampingForce));
	}

	public Vector2D getVirtualForce() {
		Vector2D force = new Vector2D();
		for (Robot robot : getConnectedRobots()) {
			force = force.add(getVirtualForceFrom(robot));
		}
		return force;
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		if (atSamePoint(robot)) {
			return new Vector2D();
		} else {
			Vector2D vector = getVector2DTo(robot);
			if (vector.getNorm() >= idealDistance) {
				return vector.normalize().multiply(attractiveCoefficient * (vector.getNorm() - idealDistance));
			} else {
				return vector.normalize().multiply(repulsiveCoefficient / vector.getNorm()).reverse();
			}
		}
	}

	public Vector2D getDampingForce() {
		return speed.multiply(dampingCoefficient).reverse();
	}
}

@SuppressWarnings("serial")
class VFMobileSensorNetworkCanvas extends SensorNetworkCanvas {
	public VFMobileSensorNetworkCanvas(SensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawRobot(Robot robot, Graphics g) {
		super.drawRobot(robot, g);
		drawCircle(new Circle(robot, ((VFRobot) robot).idealDistance), g, new Color(0, 0, 0, 32), false);
		VFRobot vfRobot = (VFRobot) robot;
		drawVector(vfRobot, fixForce(vfRobot.virutalForce), g, Color.magenta);
		drawVector(vfRobot, fixForce(vfRobot.dampingForce), g, Color.blue);
	}
}
