package sensornetwork;

import geom.Circle;
import geom.Point2D;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CoverageCalculator {
	private SensorNetwork sensorNetwork;
	private Integer precision = 25;
	private HashMap<Integer, HashSet<Point2D>> sensedPointsHistory = new HashMap<Integer, HashSet<Point2D>>();
	private HashMap<Integer, HashSet<Point2D>> allPointsHistory = new HashMap<Integer, HashSet<Point2D>>();
	private Integer finishedNo = -1;

	public CoverageCalculator(SensorNetwork sensorNetwork) {
		this.sensorNetwork = sensorNetwork;
	}

	public Double getCoverage(Integer iterationNo) {
		if (iterationNo > finishedNo) {
			HashSet<Point2D> sensedPoints = sensedPointsHistory.get(iterationNo);
			HashSet<Point2D> allPoints = allPointsHistory.get(iterationNo);
			if (allPoints == null || allPoints.size() == 0) {
				return null;
			} else if (sensedPoints == null) {
				return 0.0;
			} else {
				return (double) sensedPoints.size() / (double) allPoints.size();
			}
		} else {
			return null;
		}
	}

	public Set<Point2D> getSensedPoints(Integer iterationNo) {
		return sensedPointsHistory.get(iterationNo);
	}

	public void sense(Integer iterationNo, Point2D point) {
		HashSet<Point2D> sensedPoints = sensedPointsHistory.get(iterationNo);
		if (sensedPoints == null) {
			sensedPoints = new HashSet<Point2D>();
			sensedPointsHistory.put(iterationNo, sensedPoints);
		}
		if (getAllPoints(iterationNo).contains(point)) {
			sensedPoints.add(point);
		}
	}

	public void sense(Integer iterationNo, Set<Point2D> points) {
		HashSet<Point2D> sensedPoints = sensedPointsHistory.get(iterationNo);
		if (sensedPoints == null) {
			sensedPoints = new HashSet<Point2D>();
			sensedPointsHistory.put(iterationNo, sensedPoints);
		}
		sensedPoints.addAll(points);
		sensedPoints.retainAll(getAllPoints(iterationNo));
	}

	public void sense(Integer iterationNo, Circle circle) {
		sense(iterationNo, circle.getPoints(precision));
	}

	public void sensingFinishied(Integer iterationNo) {
		finishedNo = iterationNo;
		sensedPointsHistory.put(iterationNo, null);
		allPointsHistory.put(iterationNo, null);
	}

	protected Set<Point2D> getAllPoints(Integer iterationNo) {
		HashSet<Point2D> allPoints = allPointsHistory.get(iterationNo);
		if (allPoints == null) {
			allPoints = new HashSet<Point2D>();
		}
		return allPoints;
	}

	public void setAllPoints(Integer iterationNo, Set<Point2D> allPoints) {
		allPointsHistory.put(iterationNo, (HashSet<Point2D>) allPoints);
	}
}
