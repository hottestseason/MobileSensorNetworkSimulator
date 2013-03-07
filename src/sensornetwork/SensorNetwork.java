package sensornetwork;

import geom.Obstacle2D;
import geom.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import network.PotentialNetwork;

public class SensorNetwork extends PotentialNetwork {
	protected SensingArea sensingArea;

	public Integer sensedAreas = 0;
	public Integer sensedEvents = 0;

	protected CoverageCalculator areaCoverageCalculator = new CoverageCalculator(this);
	protected CoverageCalculator eventCoverageCalculator = new CoverageCalculator(this);

	public void setSensingArea(SensingArea sensingArea) {
		this.sensingArea = sensingArea;
	}

	public List<Obstacle2D> getObstacles() {
		return sensingArea.getObstacles();
	}

	public Double getAreaCoverage(Integer iterationNo) {
		return areaCoverageCalculator.getCoverage(iterationNo);
	}

	public Double getEventCoverage(Integer iterationNo) {
		return eventCoverageCalculator.getCoverage(iterationNo);
	}

	public Integer getStartedNodeSize() {
		Integer size = 0;
		for (SensorNode sensorNode : getSensorNodes()) {
			if (sensorNode.getConsumedEnergy() > 0) {
				size++;
			}
		}
		return size;
	}

	public SensorNode get(int index) {
		return (SensorNode) super.get(index);
	}

	public SensorNode getSinkNode() {
		return get(0);
	}

	public ArrayList<SensorNode> getSensorNodes() {
		return (ArrayList<SensorNode>) (ArrayList<?>) this;
	}

	public Integer getMaxMessageHop() {
		return (int) (Math.sqrt(size()) * Math.sqrt(2));
	}

	public Collection<Point2D> getEventPoints() {
		return sensingArea.getEventPoints();
	}

	public void updateAreaCoverageCalculator() {
		areaCoverageCalculator.setAllPoints(getIterationNo(), sensingArea.getAllPoints());
		if (getIterationNo() > getMaxMessageHop()) {
			Set<Point2D> sensedPoints = areaCoverageCalculator.getSensedPoints(getIterationNo() - getMaxMessageHop());
			if (sensedPoints != null) {
				sensedAreas += sensedPoints.size();
			}
			areaCoverageCalculator.sensingFinishied(getIterationNo() - getMaxMessageHop());
		}
	}

	public void updateEventCoverageCalculator(Double iterationInterval) {
		sensingArea.resetEventPoints(iterationInterval);
		eventCoverageCalculator.setAllPoints(getIterationNo(), new HashSet<Point2D>(getEventPoints()));
		if (getIterationNo() > getMaxMessageHop()) {
			eventCoverageCalculator.sensingFinishied(getIterationNo() - getMaxMessageHop());
		}
	}

	public void sensedAreaDataDelivered(SensedData sensedData) {
		areaCoverageCalculator.sense(sensedData.getIterationNo(), sensedData.getSensedCircle());
	}

	public void eventDataDelivered(EventData eventData) {
		eventCoverageCalculator.sense(eventData.getIterationNo(), eventData.getPoint());
	}

	public void sendBeacon() {
		for (SensorNode sensorNode : getSensorNodes()) {
			if (sensorNode.isRunning()) {
				sensorNode.sendBeacon();
			}
		}
	}

	public void getSensingData() {
		for (SensorNode sensorNode : getSensorNodes()) {
			if (sensorNode.isRunning()) {
				sensorNode.getSensingData();
			}
		}
	}

	public void getEventsData() {
		for (SensorNode sensorNode : getSensorNodes()) {
			if (sensorNode.isRunning()) {
				sensorNode.getEventsData();
			}
		}
	}
}
