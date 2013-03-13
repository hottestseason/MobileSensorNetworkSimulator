package simulator;

import java.util.Random;

import mobilesensornetwork.MobileSensorNetwork;
import mobilesensornetwork.SensorRobotParameters;
import sensornetwork.SensingArea;

public abstract class MobileSensorNetworkSimulator {
	public long seed = 0;
	public SensingArea sensingArea;
	public Integer maxIteration = 144000;
	public Double iterationInterval;
	public SensorRobotParameters sensorRobotParameters;
	public Integer robotCount;

	protected Random random;

	private MobileSensorNetwork mobileSensorNetwork;

	protected void setMobileSensorNetwork(MobileSensorNetwork mobileSensorNetwork) {
		this.mobileSensorNetwork = mobileSensorNetwork;
	}

	protected MobileSensorNetwork getMobileSensorNetwork() {
		return mobileSensorNetwork;
	}

	public abstract void setup();

	public abstract void start();

	public void iterate() {
		getMobileSensorNetwork().incrementIterationNo();
		getMobileSensorNetwork().resetState();
		getMobileSensorNetwork().sendBeacon();
		getMobileSensorNetwork().resetConnections();
	}
}
