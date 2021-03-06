package network;

import graph.Graph;

import java.util.ArrayList;

public class Network extends Graph {
	static public Integer dateSavedPeriods = 50000;

	private Double iterationInterval = 0.1;
	private Integer iterationNo = 0;

	private ArrayList<NetworkNode> runningNodes = new ArrayList<NetworkNode>();

	public Double getIterationInterval() {
		return iterationInterval;
	}

	public void setIterateInterval(Double iterateInterval) {
		this.iterationInterval = iterateInterval;
	}

	public Integer getIterationNo() {
		return iterationNo;
	}

	public void incrementIterationNo() {
		iterationNo++;
	}

	public Boolean isRunning() {
		for (NetworkNode node : getNetworkNodes()) {
			if (node.isRunning()) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<NetworkNode> getNetworkNodes() {
		return (ArrayList<NetworkNode>) (ArrayList<?>) this;
	}

	public NetworkNode get(int index) {
		return (NetworkNode) super.get(index);
	}

	public Double getsumConsumedEnergy() {
		Double sumConsumedEnergy = 0.0;
		for (NetworkNode networkNode : getNetworkNodes()) {
			sumConsumedEnergy += networkNode.getConsumedEnergy();
		}
		return sumConsumedEnergy;
	}

	public Double getMaxConsumedEnergy() {
		Double maxConsumedEnergy = 0.0;
		for (NetworkNode networkNode : getNetworkNodes()) {
			if (networkNode.getConsumedEnergy() > maxConsumedEnergy) {
				maxConsumedEnergy = networkNode.getConsumedEnergy();
			}
		}
		return maxConsumedEnergy;
	}

	public void resetConnections() {
		ArrayList<NetworkNode> runningNodes = getRunningNodes();
		for (NetworkNode networkNode : runningNodes) {
			networkNode.clearEdges();
		}
		for (int i = 0; i < runningNodes.size(); i++) {
			NetworkNode networkNodeA = runningNodes.get(i);
			for (int j = i + 1; j < runningNodes.size(); j++) {
				NetworkNode networkNodeB = runningNodes.get(j);
				if (networkNodeA.canConnect(networkNodeB)) {
					networkNodeA.connect(networkNodeB);
				}
			}
		}
	}

	public void resetState() {
		for (NetworkNode networkNode : getNetworkNodes()) {
			if (networkNode.isRunning()) {
				networkNode.resetState();
			}
		}
	}

	public void transferMessages() {
		for (NetworkNode networkNode : getNetworkNodes()) {
			if (networkNode.isRunning()) {
				networkNode.transferMessages();
			}
		}
	}

	public ArrayList<NetworkNode> getRunningNodes() {
		return runningNodes;
	}

	public void startIteration() {
		incrementIterationNo();
		runningNodes = new ArrayList<NetworkNode>();
		for (NetworkNode node : getNetworkNodes()) {
			if (node.isRunning()) {
				runningNodes.add(node);
			}
		}
	}
}
