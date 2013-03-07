package graph;

import geom.Circle;
import geom.LineSegment2D;
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
			// for (int i = 0; i < connectedNodes.size(); i++) {
			// if (getAngle(connectedNodes.get(i)) > getAngle(node)) {
			// connectedNodes.add(i, node);
			// if (!graph.isDirected()) {
			// node.connectedNodes.add(this);
			// }
			// return;
			// }
			// }
			connectedNodes.add(node);
			if (!graph.isDirected()) {
				node.connectedNodes.add(this);
			}
		}
	}

	public Node getMostFarthestConnectedNode() {
		Node farthestNode = null;
		for (Node node : connectedNodes) {
			if (farthestNode == null || getDistanceFrom(node) > getDistanceFrom(farthestNode)) {
				farthestNode = node;
			}
		}
		return farthestNode;
	}

	public Boolean isDelaunayTriangle(Node node2, Node node3) {
		if (this.equals(node2) || this.equals(node3) || node2.equals(node3)) {
			return false;
		} else {
			Circle circle = new Circle(this, node2, node3);
			for (Node node : getConnectedNodes()) {
				if (!node.equals(this) && !node.equals(node2) && !node.equals(node3)) {
					if (circle.contains(node)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Boolean ggTest(Node node) {
		if (node == null || equals(node)) {
			return false;
		} else {
			Circle circle = new Circle(new LineSegment2D(this, node));
			for (Node another : getConnectedNodes()) {
				if (!another.equals(this) && !another.equals(node)) {
					if (circle.containsExcludeEdge(another)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Boolean rngTest(Node node) {
		if (node.equals(this)) {
			return false;
		} else {
			Double distance = getDistanceFrom(node);
			for (Node another : getConnectedNodes()) {
				if (!another.equals(this) && !another.equals(node)) {
					if (Math.max(getDistanceFrom(another), node.getDistanceFrom(another)) < distance) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
