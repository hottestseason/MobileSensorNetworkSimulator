package network;

public class PotentialNetwork extends Network {
	public void updatePotential() {
		long before = System.nanoTime();
		for (NetworkNode networkNode : getRunningNodes()) {
			((PotentialNode) networkNode).updatePotential();
		}
		System.out.print("potential " + (System.nanoTime() - before) / 1000L + "us ");
	}
}
