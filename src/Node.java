import geom.Circle;
import geom.LineSegment2D;
import geom.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node extends Point2D {
	Graph graph;
	Integer id;
	List<Node> connectedNodes = Collections.synchronizedList(new ArrayList<Node>());

	public void setPoint(Point2D point) {
		this.x = point.x;
		this.y = point.y;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node node = (Node) obj;
			return id == node.id;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return id;
	}

	public Boolean atSamePoint(Point2D point) {
		return super.equals(point);
	}

	public void resetConnections() {
		connectedNodes.clear();
	}

	public Boolean isConnectedTo(Node node) {
		return connectedNodes.indexOf(node) != -1;
	}

	public void connect(Node node) {
		if (!isConnectedTo(node)) {
			for (int i = 0; i < connectedNodes.size(); i++) {
				if (getAngle(connectedNodes.get(i)) > getAngle(node)) {
					connectedNodes.add(i, node);
					if (!graph.directed) {
						node.connectedNodes.add(this);
					}
					return;
				}
			}
			connectedNodes.add(node);
			if (!graph.directed) {
				node.connectedNodes.add(this);
			}
		}
	}

	public Boolean acuteAngleTest(Node node) {
		Circle circle = new Circle(new LineSegment2D(this, node));
		if (node == this) {
			return false;
		} else {
			for (Node another : graph) {
				if (!another.equals(this) && !another.equals(node)) {
					if (circle.containsExcludeEdge(another)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Boolean isDelaunayTriangle(Node node2, Node node3) {
		if (this.equals(node2) || this.equals(node3) || node2.equals(node3)) {
			return false;
		} else {
			Circle circle = new Circle(this, node2, node3);
			for (Node node : graph) {
				if (!node.equals(this) && !node.equals(node2) && !node.equals(node3)) {
					if (circle.contains(node)) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
