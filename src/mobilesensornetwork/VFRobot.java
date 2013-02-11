package mobilesensornetwork;

import geom.Vector2D;

public class VFRobot extends Robot {
	protected Double dampingCoefficient = 0.0;
	Vector2D virutalForce = new Vector2D();
	Vector2D dampingForce = new Vector2D();

	public VFRobot(RobotParameters parameters) {
		super(parameters);
	}

	public Double getDampingCoefficient() {
		return dampingCoefficient;
	}

	public void setDampingCoefficient(Double dampingCoefficient) {
		this.dampingCoefficient = dampingCoefficient;
	}

	public void iterate() {
		super.iterate();
		calculateForce();
	}

	public Vector2D getAppliedForce() {
		return virutalForce.add(dampingForce);
	}

	public void resetState() {
		virutalForce = new Vector2D();
		dampingForce = new Vector2D();
		super.resetState();
	}

	public void calculateForce() {
		virutalForce = getVirtualForce();
		dampingForce = getDampingForce();
		setAcceleration(getAppliedForce().multiply(1 / getWeight()));
	}

	public Vector2D getVirtualForce() {
		Vector2D force = new Vector2D();
		for (Robot robot : getConnectedRobots()) {
			force = force.add(getVirtualForceFrom(robot));
		}
		return force;
	}

	public Vector2D getVirtualForceFrom(Robot robot) {
		return new Vector2D();
	}

	public Vector2D getDampingForce() {
		return getSpeed().multiply(getDampingCoefficient()).reverse();
	}
}
