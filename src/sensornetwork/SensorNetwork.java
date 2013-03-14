package sensornetwork;

import geom.Circle;
import geom.Obstacle2D;
import geom.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import network.PotentialNetwork;

public class SensorNetwork extends PotentialNetwork {
	static public Integer precision = 50;

	protected SensingArea sensingArea;

	public Integer sensedAreas = 0;
	public Integer sensedEvents = 0;

	protected CoverageCalculator areaCoverageCalculator = new CoverageCalculator(this);
	protected CoverageCalculator eventCoverageCalculator = new CoverageCalculator(this);

	private Integer sensingInterval;

	public TreeMap<Integer, Double> areaCoverageHistory = new TreeMap<Integer, Double>();
	public TreeMap<Integer, Integer> startedNodesHistory = new TreeMap<Integer, Integer>();

	public void setSensingArea(SensingArea sensingArea) {
		this.sensingArea = sensingArea;
	}

	public List<Obstacle2D> getObstacles() {
		return sensingArea.getObstacles();
	}

	public Double getAreaCoverage(Integer iterationNo) {
		Double coverage = areaCoverageHistory.get(iterationNo);
		if (coverage != null) {
			return coverage;
		} else {
			return areaCoverageCalculator.getCoverage(iterationNo);
		}
	}

	public Double getEventCoverage(Integer iterationNo) {
		return eventCoverageCalculator.getCoverage(iterationNo);
	}

	public Integer getStartedNodeSize(Integer iterationNo) {
		return startedNodesHistory.get(iterationNo);
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

	public Integer getLastSensingFinishedNo() {
		return getIterationNo() - getMaxMessageHop() * getSensingInterval();
	}

	public Collection<Point2D> getEventPoints() {
		return sensingArea.getEventPoints();
	}

	public void updateAreaCoverageCalculator() {
		areaCoverageCalculator.setAllPoints(getIterationNo(), sensingArea.getAllPoints());
	}

	public void updateEventCoverageCalculator(Double iterationInterval) {
		sensingArea.resetEventPoints(iterationInterval);
		eventCoverageCalculator.setAllPoints(getIterationNo(), new HashSet<Point2D>(getEventPoints()));
		if (getIterationNo() > getMaxMessageHop()) {
			eventCoverageCalculator.sensingFinishied(getIterationNo() - getMaxMessageHop());
		}
	}

	public void sensed(Integer iterationNo, Circle circle) {
		areaCoverageCalculator.sense(iterationNo, circle);
	}

	public void sensed(Circle circle) {
		sensed(getIterationNo(), circle);
	}

	public void sensedAreaDataDelivered(SensedData sensedData) {
		sensed(sensedData.getIterationNo(), sensedData.getSensedCircle());
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

	public Integer getSensingInterval() {
		return sensingInterval;
	}

	public void setSensingInterval(Integer sensingInterval) {
		this.sensingInterval = sensingInterval;
	}

	public void finishIteration() {
		startedNodesHistory.put(getIterationNo(), getStartedNodeSize());
		if (getIterationNo() > getMaxMessageHop()) {
			Integer finishedNo = getIterationNo() - getMaxMessageHop();
			Set<Point2D> sensedPoints = areaCoverageCalculator.getSensedPoints(finishedNo);
			if (sensedPoints != null) {
				sensedAreas += sensedPoints.size();
			}
			areaCoverageHistory.put(finishedNo, getAreaCoverage(finishedNo));
			areaCoverageCalculator.sensingFinishied(finishedNo);
		}
	}

	public void clearOldHistory() {
		if (getIterationNo() > dateSavedPeriods) {
			startedNodesHistory.headMap(getIterationNo() - dateSavedPeriods).clear();
			areaCoverageHistory.headMap(getIterationNo() - dateSavedPeriods).clear();
		}
	}
}
