package network;

import graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import utils.MapUtils;

public class NetworkNode extends Node {
	private Boolean running = true;
	private Double transmissionConsumedEnergy = 0.0;
	private TreeMap<Integer, ArrayList<Message>> buffers;

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
		MapUtils.clearOld(buffers, 3);
	}

	public void transferMessages() {
		List<Message> messages = getBuffer(getIterationNo());
		if (messages != null) {
			for (Message message : getBuffer(getIterationNo())) {
				if (!equals(message.getTo())) {
					send(message);
				}
			}
		}
	}

	protected List<Message> getBuffer(Integer iterationNo) {
		if (buffers == null) {
			return null;
		} else {
			return buffers.get(iterationNo);
		}
	}

	protected void addToBuffer(Integer iterationNo, Message message) {
		if (buffers == null) {
			buffers = new TreeMap<Integer, ArrayList<Message>>();
		}
		ArrayList<Message> buffer = buffers.get(iterationNo);
		if (buffer == null) {
			buffer = new ArrayList<Message>(500);
			buffers.put(iterationNo, buffer);
		}
		buffer.add(message);
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
