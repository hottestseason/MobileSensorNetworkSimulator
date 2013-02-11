package mobilesensornetwork;

import geom.Circle;
import geom.Obstacle2D;
import geom.Point2D;
import graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("serial")
public class MobileSensorNetwork extends Graph {
	private Integer maxIteration = 6400;
	private Boolean mustAlwaysConnected = false;
	private Boolean calculatesCavarege = true;

	private Integer iterationNo = 0;
	List<Obstacle2D> obstacles = Collections.synchronizedList(new ArrayList<Obstacle2D>());
	private Double sumMovedDistance = 0.0;
	private Boolean connectivity = true;
	Boolean alwaysConnected = true;

	Calculator calculator;
	protected Boolean stopFlag = false;

	public Integer getIterationNo() {
		return iterationNo;
	}

	public Boolean getCalculatesCavarege() {
		return calculatesCavarege;
	}

	public void setCalculatesCavarege(Boolean calculatesCavarege) {
		this.calculatesCavarege = calculatesCavarege;
	}

	public Integer getMaxIteration() {
		return maxIteration;
	}

	public void setMaxIteration(Integer maxIteration) {
		this.maxIteration = maxIteration;
	}

	public Boolean getMustAlwaysConnected() {
		return mustAlwaysConnected;
	}

	public void setMustAlwaysConnected(Boolean mustAlwaysConnected) {
		this.mustAlwaysConnected = mustAlwaysConnected;
	}

	public Boolean getConnectivity() {
		return connectivity;
	}

	public void setConnectivity(Boolean connectivity) {
		this.connectivity = connectivity;
	}

	public Double getSumMovedDistance() {
		return sumMovedDistance;
	}

	public void setSumMovedDistance(Double sumMovedDistance) {
		this.sumMovedDistance = sumMovedDistance;
	}

	public Double getCoverage(Integer iterationNo) {
		return calculator.getCoverage(iterationNo);
	}

	public boolean add(Robot robot) {
		robot.setGraph(this);
		robot.setId(size());
		return super.add(robot);
	}

	public Robot get(int index) {
		return (Robot) super.get(index);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Robot> getRobots() {
		return (ArrayList<Robot>) (ArrayList<?>) this;
	}

	public void addObstacle(Obstacle2D obstacle) {
		obstacles.add(obstacle);
	}

	public void addObstacles(ArrayList<Obstacle2D> obstacles) {
		this.obstacles.addAll(obstacles);
	}

	public void iterate() {
		iterationNo++;
		System.out.println(iterationNo);
		for (Robot robot : getRobots()) {
			calculator.sense(iterationNo, robot.getSensorCircle());
			robot.iterate();
			connectivity = isConnected();
			if (!connectivity) {
				alwaysConnected = false;
				if (getMustAlwaysConnected()) {
					stop();
				}
			}
		}
		calculator.sensingFinishied(iterationNo);
		if (getIterationNo() > getMaxIteration()) {
			stop();
		}
	}

	public void move(Double seconds) {
		Double movedDistance = 0.0;
		for (Robot robot : getRobots()) {
			if (robot.getId() == 0) {
				continue;
			}
			movedDistance += robot.move(seconds).getNorm();
		}
		if (movedDistance > 0) {
			setSumMovedDistance(getSumMovedDistance() + movedDistance);
		} else {
			stop();
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
		if (getCalculatesCavarege()) {
			calculator = new Calculator();
			calculator.start();
		}
	}

	public void stop() {
		// stopFlag = true;
	}

	public Boolean isConnected() {
		HashSet<Robot> visitedRobots = new HashSet<Robot>();
		visitForConnectionTest(get(0), visitedRobots);
		return visitedRobots.containsAll(this);
	}

	private void visitForConnectionTest(Robot robot, HashSet<Robot> visitedRobots) {
		visitedRobots.add(robot);
		for (Robot connectedRobot : robot.getSensibleRobots()) {
			if (!visitedRobots.contains(connectedRobot)) {
				visitForConnectionTest(connectedRobot, visitedRobots);
			}
		}
	}

	class Calculator implements TimerListener {
		private Double calculateInterval = 0.01;
		private Integer precision = 10;
		private HashSet<Point2D> allPoints;
		private ArrayList<Integer> sensingFinishiedIterationNoQueue = new ArrayList<Integer>();
		private HashMap<Integer, ArrayList<Circle>> sensedAreasHistory = new HashMap<Integer, ArrayList<Circle>>();
		private HashMap<Integer, Double> coverageHistroy = new HashMap<Integer, Double>();
		private Timer timer;

		public Double getCoverage(Integer iterationNo) {
			return coverageHistroy.get(iterationNo);
		}

		public void start() {
			timer = new Timer(this, calculateInterval);
			timer.start();
		}

		public void iterate() {
			if (sensingFinishiedIterationNoQueue.size() > 0) {
				Integer iterationNo = sensingFinishiedIterationNoQueue.get(0);
				coverageHistroy.put(iterationNo, calculateCoverage(sensedAreasHistory.get(iterationNo)));
				sensingFinishiedIterationNoQueue.remove(0);
			}
		}

		public void sense(Integer iterationNo, Circle circle) {
			if (sensedAreasHistory.get(iterationNo) == null) {
				sensedAreasHistory.put(iterationNo, new ArrayList<Circle>());
			}
			sensedAreasHistory.get(iterationNo).add(circle.clone());
		}

		public void sensingFinishied(Integer iterationNo) {
			sensingFinishiedIterationNoQueue.add(iterationNo);
		}

		private HashSet<Point2D> getAllPoints() {
			if (allPoints == null) {
				allPoints = calculateAllPoints(precision);
			}
			return allPoints;
		}

		private HashSet<Point2D> calculateAllPoints(Integer precision) {
			HashSet<Point2D> allPoints = new HashSet<Point2D>();
			for (Obstacle2D obstacle : obstacles) {
				if (obstacle.innerBlank) {
					allPoints.addAll(obstacle.getPoints(precision));
				} else {
					allPoints.removeAll(obstacle.getPoints(precision));
				}
			}
			return allPoints;
		}

		private Double calculateCoverage(ArrayList<Circle> sensedAreas) {
			HashSet<Point2D> coveredPoints = new HashSet<Point2D>();
			for (Circle sensedArea : sensedAreas) {
				coveredPoints.addAll(sensedArea.getPoints(precision));
			}
			HashSet<Point2D> allPoints = getAllPoints();
			coveredPoints.retainAll(allPoints);
			return (double) coveredPoints.size() / (double) allPoints.size();
		}
	}
}
