package mobilesensornetwork;

import geom.LineSegment2D;
import geom.Spring;
import geom.Vector2D;
import graph.Node;

import java.util.List;

public class YaVfRobot extends SpringVFRobot {
	private Vector2D forceFromNeighborRobots = new Vector2D();
	private Vector2D attractiveForceFromWall = new Vector2D();

	public Vector2D getForceFromNeighborRobots() {
		return forceFromNeighborRobots;
	}

	public Vector2D getAttractiveForceFromWall() {
		return attractiveForceFromWall;
	}

	public YaVfRobot(SensorRobotParameters parameters) {
		super(parameters);
	}

	public void resetState() {
		super.resetState();
		forceFromNeighborRobots = new Vector2D();
		attractiveForceFromWall = new Vector2D();
	}

	public Vector2D getVirtualForce() {
		for (SensorRobot sensorRobot : getSpringConnectedRobots()) {
			forceFromNeighborRobots = forceFromNeighborRobots.add(getVirtualForceFrom(sensorRobot));
		}
		if (isEdgeNode()) {
			attractiveForceFromWall = calculateAttractiveForceFromWall();
		}
		return forceFromNeighborRobots.add(attractiveForceFromWall);
	}

	public Vector2D getVirtualForceFrom(LineSegment2D wall) {
		Double distanceFromWall = getDistanceFrom(wall);
		if (distanceFromWall > 0 && distanceFromWall < getSensorRange() / 2) {
			return Spring.getForce(getVector2DTo(wall), getSensorRange() / 2, getSpringConstant() * 2);
		} else {
			return new Vector2D();
		}
	}

	public Double getIdealSpringLength() {
		return super.getSpringLengthFor(null);
	}

	public Double getSpringLengthFor(SensorRobot sensorRobot) {
		if (getRemainedBatteryRatio() < 0.25) {
			Double idealDistance = getWirelessRange() / 2;
			idealDistance += (getRemainedBatteryRatio() - 0.1) * (calculateIdealDistance(getWirelessRange(), getSensorRange()) - getWirelessRange() / 2) / 0.15;
			return idealDistance;
		} else {
			return super.getSpringLengthFor(sensorRobot);
		}
	}

	public Vector2D calculateAttractiveForceFromWall() {
		Vector2D force = new Vector2D();
		Double sumCoefficient = 0.0;
		List<LineSegment2D> visibleWalls = getVisibleWalls();
		Double maxNorm = 0.0;
		for (Node node : getConnectedNodes()) {
			maxNorm += getWirelessRange() - getDistanceFrom(node);
		}
		for (LineSegment2D wall : visibleWalls) {
			Vector2D vector = getVector2DTo(wall);
			if (vector.getNorm() > maxNorm) {
				vector = vector.expandTo(maxNorm);
			}
			Double coefficient = getNearCoefficientFor(wall);
			force = force.add(Spring.getForce(vector, 0.0, getSpringConstant()).multiply(coefficient));
			sumCoefficient += coefficient;
		}
		if (sumCoefficient > 0) {
			return force.divide(sumCoefficient).multiply(getRemainedBatteryRatio());
		} else {
			return new Vector2D();
		}
	}

	public Double getNearCoefficientFor(LineSegment2D wall) {
		Double coefficient = 0.0;
		for (Node node : getConnectedNodes()) {
			coefficient += node.getDistanceFrom(wall) - getDistanceFrom(wall) + getIdealSpringLength();
		}
		return Math.pow(coefficient / getConnectedNodes().size(), 4);
	}
}