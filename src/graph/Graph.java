package graph;

import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings("serial")
public class Graph extends ArrayList<Node> {
	private Boolean directed = false;

	public Boolean isDirected() {
		return directed;
	}

	public void resetEdges() {
		for (Node node : this) {
			node.clearEdges();
		}
	}

	public Boolean isConnected() {
		HashSet<Node> visitedNodes = new HashSet<Node>();
		visitForConnectionTest(get(0), visitedNodes);
		return visitedNodes.containsAll(this);
	}

	private void visitForConnectionTest(Node node, HashSet<Node> visitedNodes) {
		visitedNodes.add(node);
		for (Node connectedNode : node.getConnectedNodes()) {
			if (!visitedNodes.contains(connectedNode)) {
				visitForConnectionTest(connectedNode, visitedNodes);
			}
		}
	}
}
