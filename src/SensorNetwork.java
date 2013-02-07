import geom.Circle;
import geom.Obstacle2D;
import geom.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("serial")
public class SensorNetwork extends Graph implements Runnable {
	Integer iterateNo = 0;
	List<Obstacle2D> obstacles = Collections.synchronizedList(new ArrayList<Obstacle2D>());
	Double sumMovedDistance = 0.0;
	Boolean isConnected = true;
	Boolean alwaysConnected = true;
	Integer maxIteration = 6400;
	Boolean mustAlwaysConnected = false;
	Boolean calculatesCavarege = true;
	Boolean stopsOnNotConnectedBoolean = true;
	protected HashSet<Point2D> allPoints;
	protected ArrayList<ArrayList<Circle>> sensedAreasHistory = new ArrayList<ArrayList<Circle>>();
	protected ArrayList<Double> coverageHistroy = new ArrayList<Double>();
	protected Boolean stopFlag = false;
	protected Thread thread = new Thread(this);

	public boolean add(Robot robot) {
		robot.graph = this;
		robot.id = size();
		return super.add(robot);
	}

	public Robot get(int index) {
		return (Robot) super.get(index);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Robot> getRobots() {
		return (ArrayList<Robot>) (ArrayList<?>) this;
	}

	public void iterate() {
		iterateNo++;
		ArrayList<Circle> sensedAreas = new ArrayList<Circle>();
		sensedAreasHistory.add(sensedAreas);
		for (Robot robot : getRobots()) {
			sensedAreas.add(robot.getSensorCircle());
			robot.iterate();
			isConnected = isConnected();
			if (!isConnected) {
				alwaysConnected = false;
				if (mustAlwaysConnected) {
					stopFlag = true;
				}
			}
		}
		if (iterateNo > maxIteration) {
			stopFlag = true;
		}
	}

	public void move(Double seconds) {
		Double movedDistance = 0.0;
		for (Robot robot : getRobots()) {
			if (robot.id == 0) {
				continue;
			}
			movedDistance += robot.move(seconds).getNorm();
		}
		if (movedDistance > 0) {
			sumMovedDistance += movedDistance;
		} else {
			stopFlag = true;
		}
	}

	public Double getsumConsumedEnergy() {
		Double sumConsumedEnergy = 0.0;
		for (Robot robot : getRobots()) {
			sumConsumedEnergy += robot.consumedEnergy;
		}
		return sumConsumedEnergy;
	}

	public Double getMaxConsumedEnergy() {
		Double maxConsumedEnergy = 0.0;
		for (Robot robot : getRobots()) {
			if (robot.consumedEnergy > maxConsumedEnergy) {
				maxConsumedEnergy = robot.consumedEnergy;
			}
		}
		return maxConsumedEnergy;
	}

	public Double getMaxMovedDistance() {
		Double maxMovedDistance = 0.0;
		for (Robot robot : getRobots()) {
			if (robot.movedDistance > maxMovedDistance) {
				maxMovedDistance = robot.movedDistance;
			}
		}
		return maxMovedDistance;
	}

	public void start() {
		if (calculatesCavarege) {
			getAllPoints();
			thread.start();
		}
	}

	public void run() {
		while (!stopFlag) {
			if (sensedAreasHistory.size() - 1 > coverageHistroy.size()) {
				coverageHistroy.add(calcurateCoverage(sensedAreasHistory.get(coverageHistroy.size())));
				System.out.println(coverageHistroy.size() + " : " + coverageHistroy.get(coverageHistroy.size() - 1));
			}
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public HashSet<Point2D> getAllPoints() {
		if (allPoints == null) {
			allPoints = new HashSet<Point2D>();
			for (Obstacle2D obstacle : obstacles) {
				if (obstacle.innerBlank) {
					allPoints.addAll(obstacle.getPoints());
				} else {
					allPoints.removeAll(obstacle.getPoints());
				}
			}
		}
		return allPoints;
	}

	public Double calcurateCoverage() {
		return calcurateCoverage(sensedAreasHistory.get(sensedAreasHistory.size() - 1));
	}

	public Double calcurateCoverage(ArrayList<Circle> sensedAreas) {
		HashSet<Point2D> coveredPoints = new HashSet<Point2D>();
		for (Circle sensedArea : sensedAreas) {
			coveredPoints.addAll(sensedArea.getPoints());
		}
		HashSet<Point2D> allPoints = getAllPoints();
		coveredPoints.retainAll(allPoints);
		return (double) coveredPoints.size() / (double) allPoints.size();
	}

	public Boolean isConnected() {
		HashSet<Robot> visitedRobots = new HashSet<Robot>();
		visitForConnectionTest(get(0), visitedRobots);
		return visitedRobots.containsAll(this);
	}

	public void visitForConnectionTest(Robot robot, HashSet<Robot> visitedRobots) {
		visitedRobots.add(robot);
		for (Robot connectedRobot : robot.getSensibleRobots()) {
			if (!visitedRobots.contains(connectedRobot)) {
				visitForConnectionTest(connectedRobot, visitedRobots);
			}
		}
	}
}
