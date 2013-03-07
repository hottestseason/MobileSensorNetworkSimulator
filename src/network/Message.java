package network;

import java.util.ArrayList;

public class Message {
	private NetworkNode from;
	private NetworkNode to;
	private Integer bit;
	private ArrayList<NetworkNode> hoppedNetworkNodes = new ArrayList<NetworkNode>();

	public Message(NetworkNode from, NetworkNode to, Integer bit) {
		this.from = from;
		this.to = to;
		this.bit = bit;
	}

	public NetworkNode getFrom() {
		return from;
	}

	public NetworkNode getTo() {
		return to;
	}

	public Integer getBit() {
		return bit;
	}

	public ArrayList<NetworkNode> getHoppedNetworkNodes() {
		return hoppedNetworkNodes;
	}

	public void addHoppedNetworkNode(NetworkNode networkNode) {
		hoppedNetworkNodes.add(networkNode);
	}

	public Integer getHopCount() {
		return hoppedNetworkNodes.size();
	}

	public void clearHoppedNodes() {
		hoppedNetworkNodes.clear();
	}
}
