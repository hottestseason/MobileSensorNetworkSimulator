package network;

public class PotentialNetwork extends Network {
	public void updatePotential() {
		for (NetworkNode networkNode : getRunningNodes()) {
			((PotentialNode) networkNode).updatePotential();
		}
	}
}
