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
}
