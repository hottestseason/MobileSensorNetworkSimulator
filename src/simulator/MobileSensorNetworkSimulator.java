package simulator;

import mobilesensornetwork.MobileSensorNetwork;
import mobilesensornetwork.SensorRobotParameters;
import sensornetwork.SensingArea;

public abstract class MobileSensorNetworkSimulator {
	public SensingArea sensingArea;
	public Integer maxIteration = 144000;
	public Double iterationInterval;
	public Integer sensingInterval;
	public SensorRobotParameters sensorRobotParameters;
	public Integer robotCount;

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
