import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings("serial")
public class Graph extends ArrayList<Node> {
	Boolean directed = false;

	public void resetEdges() {
		for (Node node : this) {
			node.connectedNodes.clear();
		}
	}

	public Boolean isConnected() {
		HashSet<Node> visitedNodes = new HashSet<Node>();
		visit(get(0), visitedNodes);
		return visitedNodes.containsAll(this);
	}

	public void visit(Node node, HashSet<Node> visitedNodes) {
		visitedNodes.add(node);
		for (Node connectedNode : node.connectedNodes) {
			if (!visitedNodes.contains(connectedNode)) {
				visit(connectedNode, visitedNodes);
			}
		}
	}
}
