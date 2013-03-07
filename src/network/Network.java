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
		for (NetworkNode networkNode : getNetworkNodes()) {
			if (networkNode.isRunning()) {
				networkNode.clearEdges();
			}
		}
		for (int i = 0; i < size(); i++) {
			NetworkNode networkNodeA = get(i);
			if (networkNodeA.isRunning()) {
				for (int j = i + 1; j < size(); j++) {
					NetworkNode networkNodeB = get(j);
					if (networkNodeB.isRunning() && networkNodeA.canConnect(networkNodeB)) {
						networkNodeA.connect(networkNodeB);
					}
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

	public Integer getRunningNodeSize() {
		Integer size = 0;
		for (NetworkNode networkNode : getNetworkNodes()) {
			if (networkNode.isRunning()) {
				size++;
			}
		}
		return size;
	}
}
