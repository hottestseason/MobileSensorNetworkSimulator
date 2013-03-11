package network;

import graph.Graph;

import java.util.ArrayList;

public class Network extends Graph {
	private Double iterateInterval = 0.1;
	private Integer iterationNo = 0;

	public Double getIterateInterval() {
		return iterateInterval;
	}

	public void setIterateInterval(Double iterateInterval) {
		this.iterateInterval = iterateInterval;
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
		long before = System.nanoTime();
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
		System.out.print("connections " + (System.nanoTime() - before) / 1000L + "us ");
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
		ArrayList<NetworkNode> nodes = new ArrayList<NetworkNode>();
		for (NetworkNode node : getNetworkNodes()) {
			if (node.isRunning()) {
				nodes.add(node);
			}
		}
		return nodes;
	}
}
