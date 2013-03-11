package network;

import graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkNode extends Node {
	private Boolean running = true;
	private Double transmissionConsumedEnergy = 0.0;
	private HashMap<Integer, ArrayList<Message>> buffers;

	public Network getNetwork() {
		return (Network) getGraph();
	}

	public Integer getIterationNo() {
		return getNetwork().getIterationNo();
	}

	public Boolean isRunning() {
		return running;
	}

	public void stop() {
		running = false;
		resetState();
		buffers = null;
		clearEdges();
	}

	public void start() {
		running = true;
	}

	public Double getTransmissionConsumedEnergy() {
		return transmissionConsumedEnergy;
	}

	public Double getConsumedEnergy() {
		return getTransmissionConsumedEnergy();
	}

	public Boolean consumeTransmissionEnergy(Double energy) {
		transmissionConsumedEnergy += energy;
		return true;
	}

	public Boolean canConnect(NetworkNode networkNode) {
		return true;
	}

	public List<NetworkNode> getConnectedNetworkNodes() {
		return (List<NetworkNode>) (List<?>) getConnectedNodes();
	}

	public void resetState() {
		clearOldBuffers(3);
	}

	public void transferMessages() {
		for (Message message : getBuffer(getIterationNo())) {
			if (!equals(message.getTo())) {
				send(message);
			}
		}
	}

	protected ArrayList<Message> getBuffer(Integer iterationNo) {
		ArrayList<Message> buffer = buffers.get(iterationNo);
		if (buffer == null) {
			buffer = new ArrayList<Message>();
		}
		return buffer;
	}

	protected void addToBuffer(Integer iterationNo, Message message) {
		if (buffers == null) {
			buffers = new HashMap<Integer, ArrayList<Message>>();
		}
		ArrayList<Message> buffer = buffers.get(iterationNo);
		if (buffer == null) {
			buffer = new ArrayList<Message>(500);
			buffers.put(iterationNo, buffer);
		}
		buffer.add(message);
	}

	public void clearOldBuffers(Integer clearCount) {
		if (buffers == null) {
			return;
		}
		ArrayList<Integer> removeBufferKeys = new ArrayList<Integer>();
		for (Map.Entry<Integer, ArrayList<Message>> entry : buffers.entrySet()) {
			if (entry.getKey() < getIterationNo() - clearCount) {
				removeBufferKeys.add(entry.getKey());
			}
		}
		for (Integer key : removeBufferKeys) {
			if (buffers.get(key) != null) {
				buffers.remove(key);
			}
		}
	}

	public void send(Integer bit, Double distance) {
		consumeTransmissionEnergy((bit * 50 + 0.1 * bit * distance * distance) * Math.pow(10, -9));
	}

	public void send(Message message, Double distance) {
		send(message.getBit(), distance);
	}

	public void send(Message message, NetworkNode to) {
		if (!equals(to)) {
			send(message.getBit(), getDistanceFrom(to));
			to.receive(message);
		}
	}

	public void send(Message message) {
		NetworkNode nextNetworkNode = getNextHop(message);
		if (nextNetworkNode != null) {
			send(message, nextNetworkNode);
		}
	}

	public void receive(Integer bit) {
		consumeTransmissionEnergy(bit * 50 * Math.pow(10, -9));
	}

	public void receive(Message message) {
		receive(message.getBit());
		 if (equals(message.getTo())) {
		 message.clearHoppedNodes();
		 } else {
		 message.addHoppedNetworkNode(this);
		 }
		 addToBuffer(getIterationNo() + 1, message);
	}

	protected NetworkNode getNextHop(Message message) {
		return getUnhoppedConnectedNodes(message).get(0);
	}

	protected ArrayList<NetworkNode> getUnhoppedConnectedNodes(Message message) {
		ArrayList<NetworkNode> connectedNodes = new ArrayList<NetworkNode>();
		for (NetworkNode networkNode : getConnectedNetworkNodes()) {
			connectedNodes.add(networkNode);
		}
		connectedNodes.removeAll(message.getHoppedNetworkNodes());
		return connectedNodes;
	}
}