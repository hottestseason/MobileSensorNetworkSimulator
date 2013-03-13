package mobilesensornetwork;

import network.NetworkNode;

public class VfMobileSensorNetwork extends MobileSensorNetwork {
	public void calculateVirtualForce() {
		long before = System.nanoTime();
		for (NetworkNode node : getRunningNodes()) {
			((VFRobot) node).calculateVirtualForce();
		}
		System.out.print("virtualForce " + (System.nanoTime() - before) / 1000L + "us ");
	}
}
