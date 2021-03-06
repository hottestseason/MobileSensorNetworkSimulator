package network;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import utils.MapUtils;

public class PotentialNode extends NetworkNode {
	protected Double potentialAlpha = 0.9;

	private TreeMap<Integer, Double> potentials = new TreeMap<Integer, Double>();

	public void resetState() {
		super.resetState();
		MapUtils.clearOld(potentials, 5);
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
		return getMinPotentialNode((List<PotentialNode>) (List<?>) getConnectedNodes());
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

	protected List<NetworkNode> getNextHops(Message message) {
		List<NetworkNode> nodes = new ArrayList<NetworkNode>();
		NetworkNode nextNode = getMinPotentialNode((List<PotentialNode>) (List<?>) getUnhoppedConnectedNodes(message));
		if (nextNode != null) {
			nodes.add(nextNode);
		}
		return nodes;
	}
}
