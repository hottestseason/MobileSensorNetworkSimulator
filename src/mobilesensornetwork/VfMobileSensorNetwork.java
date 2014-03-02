package mobilesensornetwork;

import network.NetworkNode;

public class VfMobileSensorNetwork extends MobileSensorNetwork {
	public void calculateVirtualForce() {
		for (NetworkNode node : getRunningNodes()) {
			((VFRobot) node).calculateVirtualForce();
		}
	}
}
