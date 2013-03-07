package network;

import java.util.HashMap;
import java.util.List;

public class PotentialNode extends NetworkNode {
	protected Double potentialAlpha = 0.9;
	private HashMap<Integer, Double> potentials = new HashMap<Integer, Double>();

	public List<PotentialNode> getConnectedPotentialNodes() {
		return (List<PotentialNode>) (List<?>) getConnectedNodes();
	}

	public Double getPotential() {
		return getPotential(getIterationNo() - 1);
	}

	public Double getPotential(Integer iterateNo) {
		Double potential = potentials.get(iterateNo);
		if (potential == null) {
			return 0.0;
		} else {
			return potential;
		}
	}

	protected void updatePotential() {
		potentials.put(getIterationNo(), calculatePotential());
	}

	public Double calculatePotential() {
		if (getId() == 0) {
			return 0.0;
		} else {
			Double potential = getPotential();
			PotentialNode minPotentialNode = getMinPotentialNode();
			if (minPotentialNode != null) {
				potential += (minPotentialNode.getPotential() - getPotential()) * potentialAlpha;
			}
			return potential + calculateOptimizeTerm();
		}
	}

	protected Double calculateOptimizeTerm() {
		return potentialAlpha;
	}

	protected PotentialNode getMinPotentialNode() {
		return getMinPotentialNode(getConnectedPotentialNodes());
	}

	protected PotentialNode getMinPotentialNode(List<PotentialNode> from) {
		PotentialNode minPotentialNode = null;
		for (PotentialNode potentialNode : from) {
			if (minPotentialNode == null || potentialNode.getPotential() < minPotentialNode.getPotential()) {
				minPotentialNode = potentialNode;
			}
		}
		return minPotentialNode;
	}

	protected NetworkNode getNextHop(Message message) {
		return getMinPotentialNode((List<PotentialNode>) (List<?>) getUnhoppedConnectedNodes(message));
	}
}
