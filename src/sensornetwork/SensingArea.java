package sensornetwork;

import geom.Obstacle2D;
import geom.Point2D;
import geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import utils.MathUtils;

public class SensingArea {
	private Random random = new Random();
	private Integer precision = SensorNetwork.precision;
	private Double eventsPerSeconds = 0.0001;
	private Integer maxEventsPerPoint = 5;
	private List<Obstacle2D> obstacles = new ArrayList<Obstacle2D>();
	private Collection<Point2D> eventPoints = new ArrayList<Point2D>();
	private Set<Point2D> allPoints;

	public static SensingArea getType1(Double width, Double height) {
		ArrayList<Obstacle2D> obstacles = new ArrayList<Obstacle2D>();
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(), width, height), true, true));
		return new SensingArea(obstacles);
	}

	public static SensingArea getType2(Double width, Double height) {
		ArrayList<Obstacle2D> obstacles = new ArrayList<Obstacle2D>();
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(), width, height), true, true));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 1 / 10, height * 0 / 10), width * 1 / 10, height * 3.5 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 3 / 10, height * 0 / 10), width * 1 / 10, height * 3.5 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 6 / 10, height * 0 / 10), width * 1 / 10, height * 3.5 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 8 / 10, height * 0 / 10), width * 1 / 10, height * 3.5 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 1 / 10, height * 6.5 / 10), width * 1 / 10, height * 3.5 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 3 / 10, height * 6.5 / 10), width * 1 / 10, height * 3.5 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 6 / 10, height * 6.5 / 10), width * 1 / 10, height * 3.5 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 8 / 10, height * 6.5 / 10), width * 1 / 10, height * 3.5 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 0 / 10, height * 4.5 / 10), width * 3 / 10, height * 1 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 7 / 10, height * 4.5 / 10), width * 3 / 10, height * 1 / 10)));
		return new SensingArea(obstacles);
	}

	public static SensingArea getType3(Double width, Double height) {
		ArrayList<Obstacle2D> obstacles = new ArrayList<Obstacle2D>();
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(), width, height), true, true));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 2 / 10, height * 0 / 10), width * 2 / 10, height * 3 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 6 / 10, height * 0 / 10), width * 2 / 10, height * 3 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 2 / 10, height * 7 / 10), width * 2 / 10, height * 3 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 6 / 10, height * 7 / 10), width * 2 / 10, height * 3 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 0 / 10, height * 4.5 / 10), width * 3 / 10, height * 1 / 10)));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 7 / 10, height * 4.5 / 10), width * 3 / 10, height * 1 / 10)));
		return new SensingArea(obstacles);
	}

	public static SensingArea getType4(Double width, Double height) {
		ArrayList<Obstacle2D> obstacles = new ArrayList<Obstacle2D>();
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(), width, height), true, true));
		obstacles.add(new Obstacle2D(new Rectangle2D(new Point2D(width * 6 / 10, height * 6 / 10), width * 2 / 10, height * 3 / 10)));
		return new SensingArea(obstacles);
	}

	public SensingArea() {
	}

	public SensingArea(List<Obstacle2D> obstacles) {
		super();
		addObstacles(obstacles);
	}

	public void setRandomSeed(long seed) {
		random = new Random(seed);
	}

	public Double getWidth() {
		return obstacles.get(0).getSurroundedRectangle().getWidth();
	}

	public Double getHeight() {
		return obstacles.get(0).getSurroundedRectangle().getHeight();
	}

	public void addObstacle(Obstacle2D obstacle) {
		obstacles.add(obstacle);
	}

	public void addObstacles(List<Obstacle2D> obstacles) {
		this.obstacles.addAll(obstacles);
	}

	public List<Obstacle2D> getObstacles() {
		return obstacles;
	}

	public Collection<Point2D> getEventPoints() {
		return eventPoints;
	}

	public void resetEventPoints(Double seconds) {
		eventPoints.clear();
		eventPoints.addAll(calculateEventPoints(seconds));
	}

	public Collection<Point2D> calculateEventPoints(Double seconds) {
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		Double eventsAverage = precision * precision * seconds * eventsPerSeconds;
		for (Point2D point : getAllPoints()) {
			for (int i = 1; i <= maxEventsPerPoint; i++) {
				Double poisson = MathUtils.getPoisson(eventsAverage, i);
				if (random.nextDouble() < poisson) {
					for (int j = 1; j <= i; j++) {
						points.add(point.clone());
					}
				}
			}
		}
		return points;
	}

	protected Set<Point2D> getAllPoints() {
		if (allPoints == null) {
			allPoints = calculateAllPoints(precision);
		}
		return allPoints;
	}

	protected Set<Point2D> calculateAllPoints(Integer precision) {
		HashSet<Point2D> allPoints = new HashSet<Point2D>();
		for (Obstacle2D obstacle : getObstacles()) {
			if (obstacle.innerBlank) {
				allPoints.addAll(obstacle.getPoints(precision));
			} else {
				allPoints.removeAll(obstacle.getPoints(precision));
			}
		}
		return allPoints;
	}
}
