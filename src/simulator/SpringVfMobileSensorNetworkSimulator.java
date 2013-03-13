package simulator;

import java.util.Random;

import mobilesensornetwork.SpringVFRobot;
import mobilesensornetwork.SpringVfMobileSensorNetwork;

public class SpringVfMobileSensorNetworkSimulator extends MobileSensorNetworkSimulator {
	public Double dampingCoefficient, springConstant;

	protected SpringVfMobileSensorNetwork getSpringVfMobileSensorNetwork() {
		return (SpringVfMobileSensorNetwork) getMobileSensorNetwork();
	}

	public void setup() {
		random = new Random(seed);
		setMobileSensorNetwork(new SpringVfMobileSensorNetwork());
		getSpringVfMobileSensorNetwork().setIterateInterval(iterationInterval);
		getSpringVfMobileSensorNetwork().setSensingArea(sensingArea);
		getSpringVfMobileSensorNetwork().prepareNodes(robotCount, sensorRobotParameters, dampingCoefficient, springConstant);
		getSpringVfMobileSensorNetwork().scatter(25.0, 25.0, random);
	}

	public void start() {
		Double movedDistance = 0.0;
		do {
			iterate();
			movedDistance = getSpringVfMobileSensorNetwork().move(getSpringVfMobileSensorNetwork().getIterationInterval());
		} while (movedDistance > 0.0 && getSpringVfMobileSensorNetwork().isConnected() && getSpringVfMobileSensorNetwork().getIterationNo() < maxIteration);
		SpringVFRobot robot = (SpringVFRobot) getSpringVfMobileSensorNetwork().getSinkNode();
		Boolean converged = getMobileSensorNetwork().getIterationNo() < maxIteration;
		System.out.println(getMobileSensorNetwork().size() + "," + getMobileSensorNetwork().getIterationInterval() + "," + robot.getSpringConstant() + "," + robot.getDampingCoefficient() + "," + getMobileSensorNetwork().getIterationNo() + "," + getSpringVfMobileSensorNetwork().isConnected() + "," + converged + "," + getMobileSensorNetwork().getSumMovedDistance() + "," + getMobileSensorNetwork().getsumConsumedEnergy() + "," + getMobileSensorNetwork().getMaxConsumedEnergy());
	}

	public void iterate() {
		super.iterate();
		getSpringVfMobileSensorNetwork().createSpringConnections();
		getSpringVfMobileSensorNetwork().calculateVirtualForce();
	}
}
