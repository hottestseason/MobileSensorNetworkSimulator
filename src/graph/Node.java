package graph;

import geom.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node extends Point2D {
	private Graph graph;
	private Integer id;
	private List<Node> connectedNodes = Collections.synchronizedList(new ArrayList<Node>());

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public List<Node> getConnectedNodes() {
		return connectedNodes;
	}

	public void setPoint(Point2D point) {
		this.x = point.x;
		this.y = point.y;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node node = (Node) obj;
			return getId() == node.getId();
		} else {
			return false;
		}
	}

	public int hashCode() {
		return getId();
	}

	public Node clone() {
		return (Node) super.clone();
	}

	public Boolean isAtSamePoint(Point2D point) {
		return super.equals(point);
	}

	public void clearEdges() {
		connectedNodes.clear();
	}

	public Boolean isConnectedWith(Node node) {
		return connectedNodes.indexOf(node) != -1;
	}

	public void connect(Node node) {
		if (!isConnectedWith(node)) {
			for (int i = 0; i < connectedNodes.size(); i++) {
				if (getAngle(connectedNodes.get(i)) > getAngle(node)) {
					connectedNodes.add(i, node);
					if (!graph.isDirected()) {
						node.connectedNodes.add(this);
					}
					return;
				}
			}
			connectedNodes.add(node);
			if (!graph.isDirected()) {
				node.connectedNodes.add(this);
			}
		}
	}

	protected Double getMaxEdgeDistance() {
		Double maxDistance = 0.0;
		for (Node node : connectedNodes) {
			maxDistance = Math.max(maxDistance, getDistanceFrom(node));
		}
		return maxDistance;
	}
}
