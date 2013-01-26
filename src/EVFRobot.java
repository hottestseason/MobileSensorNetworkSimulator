import geom.Vector2D;

public class EVFRobot extends VFRobot {
	Double alpha = 0.0;
	Double beta = 1.0;

	public EVFRobot(SensorNetwork sensorNetwork, Integer id, Double wirelessRange, Double sensorRange, Double weight, Double size, Double iterateInterval) {
		super(sensorNetwork, id, wirelessRange, sensorRange, weight, size, iterateInterval);
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		if (atSamePoint(robot)) {
			return new Vector2D();
		} else {
			Vector2D vector = getVector2DTo(robot);
			if (vector.getNorm() > idealDistance) {
				return vector.normalize().multiply(Math.abs(alpha * (Math.pow(vector.getNorm(), -beta) - Math.pow(idealDistance, -beta))));
			} else if (vector.getNorm() == idealDistance) {
				return new Vector2D();
			} else if (vector.getNorm() > idealDistance / 2) {
				return vector.normalize().reverse().multiply(Math.abs(alpha * (Math.pow(vector.getNorm(), -beta) - Math.pow(idealDistance, -beta))));
			} else {
				return vector.normalize().reverse();
			}
		}
	}

	public Vector2D getOrientationForceFrom(Robot another) {
		Vector2D force = new Vector2D();
		// for (int i = 0; i < connectedNodes.size(); i++) {
		//
		// }
		//
		// for (Robot neighborAroundAnotherRobot : another.getConnectedRobots())
		// {
		//
		// }
		return force;
	}
}
