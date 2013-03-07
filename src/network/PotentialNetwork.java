package network;

public class PotentialNetwork extends Network {
	public void updatePotential() {
		for (NetworkNode networkNode : getNetworkNodes()) {
			if (networkNode.isRunning()) {
				((PotentialNode) networkNode).updatePotential();
			}
		}
	}
}
