package sensornetwork;

import geom.Circle;
import geom.LineSegment2D;
import geom.Obstacle2D;
import geom.Point2D;
import geom.Vector2D;

import java.util.ArrayList;
import java.util.List;

import network.Message;
import network.NetworkNode;
import network.PotentialNode;

public class SensorNode extends PotentialNode {
	private SensorParameters parameters;
	private Double sensingConsumedEnergy = 0.0;

	public SensorNode() {
	}

	public SensorNode(SensorParameters parameters) {
		this.parameters = parameters;
	}

	public SensorParameters getParameters() {
		return parameters;
	}

	public Double getSize() {
		return getParameters().getSize();
	}

	public Double getWeight() {
		return getParameters().getWeight();
	}

	public Double getWirelessRange() {
		return getParameters().getWirelessRange();
	}

	public Double getSensorRange() {
		return getParameters().getSensorRange();
	}

	public Double getMaxEnergy() {
		return getParameters().getMaxEnergy();
	}

	public Circle getCircle() {
		return new Circle(this, getSize());
	}

	public Circle getWirelessCircle() {
		return new Circle(this, getWirelessRange());
	}

	public Circle getSensorCircle() {
		return new Circle(this, getSensorRange());
	}

	public Boolean consumeTransmissionEnergy(Double energy) {
		if (!isSinkNode()) {
			if (canConsumeEnergy(energy)) {
				return super.consumeTransmissionEnergy(energy);
			} else {
				stop();
				return false;
			}
		} else {
			return true;
		}
	}

	public Double getSensingConsumedEnergy() {
		return sensingConsumedEnergy;
	}

	public Boolean consumeSensingEnergy(Double energy) {
		if (!isSinkNode()) {
			if (canConsumeEnergy(energy)) {
				this.sensingConsumedEnergy += energy;
				return true;
			} else {
				stop();
				return false;
			}
		} else {
			return true;
		}
	}

	public Double getConsumedEnergy() {
		return super.getConsumedEnergy() + getSensingConsumedEnergy();
	}

	public Boolean canConsumeEnergy(Double energy) {
		if (getConsumedEnergy() + energy < getMaxEnergy()) {
			return true;
		} else {
			return false;
		}
	}

	public Double getRemainedEnergy() {
		return getMaxEnergy() - getConsumedEnergy();
	}

	public Double getRemainedBatteryRatio() {
		return getRemainedEnergy() / getMaxEnergy();
	}

	protected Double calculateOptimizeTerm() {
		return potentialAlpha * getAverageConnectedEnergy() / getRemainedEnergy();
	}

	protected Double getAverageConnectedEnergy() {
		if (getConnectedSensorNodes().size() == 0) {
			return 0.0;
		} else {
			Double sumConnectedEnergy = 0.0;
			for (SensorNode sensorNode : getConnectedSensorNodes()) {
				sumConnectedEnergy += sensorNode.getRemainedEnergy();
			}
			return sumConnectedEnergy / (double) getConnectedSensorNodes().size();
		}
	}

	public void getSensingData() {
		addToBuffer(getIterationNo(), new SensedData(this, getSinkNode(), getIterationNo(), getSensorCircle()));
	}

	public void getEventData(Point2D point) {
		if (getSensorCircle().contains(point)) {
			addToBuffer(getIterationNo(), new EventData(this, getSinkNode(), getIterationNo(), point));
		}
	}

	public void getEventsData() {
		for (Point2D point : getSensorNetwork().getEventPoints()) {
			getEventData(point);
		}
	}

	public List<SensorNode> getConnectedSensorNodes() {
		return (List<SensorNode>) (List<?>) getConnectedNodes();
	}

	public Boolean canSense(Point2D point) {
		if (!getWirelessCircle().contains(point)) {
			return false;
		} else {
			for (Obstacle2D obstacle : getSensorNetwork().getObstacles()) {
				for (LineSegment2D wall : obstacle.getEdges()) {
					if (wall.contains(new LineSegment2D(this, point))) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public Boolean canSense(SensorNode sensorNode) {
		return !sensorNode.equals(this) && canSense(sensorNode.toPoint2D());
	}

	public Boolean canSense(LineSegment2D lineSegment) {
		return getWirelessCircle().contains(lineSegment);
	}

	public List<LineSegment2D> getSensibleWalls() {
		ArrayList<LineSegment2D> walls = new ArrayList<LineSegment2D>();
		for (Obstacle2D obstacle : getSensorNetwork().getObstacles()) {
			for (LineSegment2D wall : obstacle.getEdges()) {
				if ((obstacle.preknown || canSense(wall))) {
					walls.add(wall);
				}
			}
		}
		return walls;
	}

	public List<LineSegment2D> getVisibleWalls() {
		List<LineSegment2D> sensibleWalls = getSensibleWalls();
		ArrayList<LineSegment2D> hiddenWalls = new ArrayList<LineSegment2D>();
		for (LineSegment2D wallA : sensibleWalls) {
			for (LineSegment2D wallB : sensibleWalls) {
				if (wallA != wallB && !wallA.isVisibleFrom(this, wallB)) {
					hiddenWalls.add(wallA);
				}
			}
		}
		sensibleWalls.removeAll(hiddenWalls);
		return sensibleWalls;
	}

	public void receive(Message message) {
		super.receive(message);
		if (isSinkNode()) {
			if (message instanceof SensedData) {
				getSensorNetwork().sensedAreaDataDelivered((SensedData) message);
			} else if (message instanceof EventData) {
				getSensorNetwork().eventDataDelivered((EventData) message);
			}
		}
	}

	public SensorNetwork getSensorNetwork() {
		return (SensorNetwork) getGraph();
	}

	protected SensorNode getSinkNode() {
		return getSensorNetwork().get(0);
	}

	protected Boolean isSinkNode() {
		if (getId() == 0) {
			return true;
		} else {
			return false;
		}
	}

	protected void sendBeacon() {
		send(1 * 1000 * 8, getSensorRange());
		for (SensorNode sensorNode : getConnectedSensorNodes()) {
			sensorNode.receive(new Message(this, sensorNode, 1 * 1000 * 8));
		}
	}

	public Boolean canConnect(NetworkNode node) {
		return canSense(node);
	}

	public Boolean isEdgeNode() {
		int splitSize = 360;
		double radius = getWirelessRange() * 1.01;
		for (int i = 0; i < splitSize; i++) {
			Double angle = 2 * Math.PI * i / splitSize;
			Point2D aroundPoint = add(new Vector2D(Math.cos(angle), Math.sin(angle)).multiply(radius));
			Boolean flag = true;
			for (SensorNode node : getConnectedSensorNodes()) {
				if (new Circle(node, radius).containsExcludeEdge(aroundPoint)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		return false;
	}
}
