import geom.Circle;
import geom.CircularSector;
import geom.Line2D;
import geom.LineSegment2D;
import geom.Obstacle2D;
import geom.Point2D;
import geom.Polygon2D;
import geom.Vector2D;

import java.util.Random;

public class MobileSensorNetworkSimulation {
	static Double iterateInterval = 2.0;
	static Integer robotCount = 3;
	static RobotParameters robotParameters;
	static private Random random = new Random(0);

	public static void main(String[] args) {
		robotParameters = new RobotParameters();
		robotParameters.size = 0.5;
		robotParameters.weight = 1.0;
		robotParameters.wirelessRange = 20.0;
		robotParameters.sensorRange = 8.0;
		robotParameters.maxSpeed = 1.0;
		robotParameters.minSpeed = 0.02;
		robotParameters.maxAcceleration = 0.5;

		// test();
		// getYAVFSimulator(20, 0.25, 1.0).start();
		getSpringVFSimulator(40, 0.1, 0.5).start();
		// getSmartSpringVFSimulator(40, 0.1, 0.5, iterateInterval / 10,
		// 10).start();
		// getSpringConstantAndDampingCoefficientRelation(0.25);
		// getConvergedRatioOfSpringVFandSmartSpringVF(25, 0.25, 1.0, 5);
	}

	public static void getSpringConstantAndDampingCoefficientRelation(double springConstant) {
		SensorNetworkSimulator.gui = false;
		int[] robotCounts = { 32, 64 };
		Double dampingCoefficientFrom = 0.1, dampingCoefficientTo = 5.0, dampingCoefficientStep = 0.05;
		int maxSeed = 20;
		for (int robotCount : robotCounts) {
			for (int seed = 0; seed < maxSeed; seed++) {
				random = new Random(seed);
				for (Double dampingCoefficient = dampingCoefficientFrom; dampingCoefficient < dampingCoefficientTo; dampingCoefficient += dampingCoefficientStep) {
					System.out.print(seed + ",");

					SensorNetwork sensorNetwork = new SensorNetwork();
					sensorNetwork.calculatesCavarege = false;
					sensorNetwork.maxIteration = 3200;
					sensorNetwork.mustAlwaysConnected = true;

					for (int i = 0; i < robotCount; i++) {
						SpringVFRobot robot = new SpringVFRobot(robotParameters);
						robot.iterateInterval = iterateInterval;
						robot.dampingCoefficient = dampingCoefficient;
						robot.springConstant = springConstant;
						sensorNetwork.add(robot);
						robot.setPoint(Vector2D.random(10.0, 10.0, random).add(100.0, 100.0).toPoint2D());
					}
					sensorNetwork.obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(), 200.0, 200.0), true, true));

					SensorNetworkSimulator sensorNetworkSimulator = new SensorNetworkSimulator(sensorNetwork);
					sensorNetworkSimulator.start();
				}
			}
		}
	}

	public static void getConvergedRatioOfSpringVFandSmartSpringVF(Integer robotCount, Double springConstant, Double dampingCoefficient, Integer sensingInterval) {
		SensorNetworkSimulator.gui = false;

		int maxSeed = 20;
		for (double iterateInterval = 0.5; iterateInterval < 7.5; iterateInterval += 0.2) {
			for (int seed = 0; seed < maxSeed; seed++) {
				System.out.print(seed + ",");
				random = new Random(seed);
				SensorNetwork sensorNetwork = new SensorNetwork();
				sensorNetwork.calculatesCavarege = false;
				sensorNetwork.maxIteration = 1600 * sensingInterval;
				sensorNetwork.mustAlwaysConnected = true;
				for (int i = 0; i < robotCount; i++) {
					SpringVFRobot robot = new SpringVFRobot(robotParameters);
					robot.iterateInterval = iterateInterval;
					robot.dampingCoefficient = dampingCoefficient;
					robot.springConstant = springConstant;
					sensorNetwork.add(robot);
					robot.setPoint(Vector2D.random(10.0, 10.0, random).add(100.0, 100.0).toPoint2D());
				}
				sensorNetwork.obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(), 200.0, 200.0), true, true));
				new SensorNetworkSimulator(sensorNetwork).start();
			}
		}
	}

	public static SensorNetworkSimulator getSpringVFSimulator(Integer robotCount, Double springConstant, Double dampingCoefficient) {
		SensorNetwork sensorNetwork = new SensorNetwork();

		for (int i = 0; i < robotCount; i++) {
			SpringVFRobot robot = new SpringVFRobot(robotParameters);
			robot.iterateInterval = iterateInterval;
			robot.dampingCoefficient = dampingCoefficient;
			robot.springConstant = springConstant;
			sensorNetwork.add(robot);
			robot.setPoint(Vector2D.random(10.0, 10.0, random).add(50.0, 50.0).toPoint2D());
			// robot.setPoint(new Point2D((i / 10) + 1, (i % 10) + 1));
		}
		sensorNetwork.obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(), 100.0, 100.0), true, true));
		// sensorNetwork.obstacles.addAll(Obstacle2D.getType1());

		SensorNetworkSimulator sensorNetworkSimulator = new SensorNetworkSimulator(sensorNetwork);

		return sensorNetworkSimulator;
	}

	public static SensorNetworkSimulator getSmartSpringVFSimulator(Integer robotCount, Double springConstant, Double dampingCoefficient, Double iterateInterval, Integer sensingInterval) {
		SensorNetwork sensorNetwork = new SensorNetwork();

		for (int i = 0; i < robotCount; i++) {
			SmartSpringVFRobot robot = new SmartSpringVFRobot(robotParameters);
			robot.iterateInterval = iterateInterval;
			robot.dampingCoefficient = dampingCoefficient;
			robot.springConstant = springConstant;
			robot.sensingInterval = sensingInterval;
			sensorNetwork.add(robot);
			robot.setPoint(Vector2D.random(10.0, 10.0, random).add(50.0, 50.0).toPoint2D());
			// robot.setPoint(new Point2D((i / 10) + 1, (i % 10) + 1));
		}
		sensorNetwork.obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(), 100.0, 100.0), true, true));
		// sensorNetwork.obstacles.addAll(Obstacle2D.getType1());

		SensorNetworkSimulator sensorNetworkSimulator = new SensorNetworkSimulator(sensorNetwork, new SmartSpringVFMobileSensorNetworkCanvas(sensorNetwork));

		return sensorNetworkSimulator;
	}

	public static SensorNetworkSimulator getYAVFSimulator(Integer robotCount, Double springConstant, Double dampingCoefficient) {
		SensorNetwork sensorNetwork = new SensorNetwork();

		Double size = 100.0;

		robotCount = (int) (Math.pow(Math.ceil(size / SpringVFRobot.calculateIdealDistance(dampingCoefficient, size)) + 1, 2) * 1.2);
		robotCount = 100;

		for (int i = 0; i < robotCount; i++) {
			YAVFRobot robot = new YAVFRobot(robotParameters);
			robot.iterateInterval = iterateInterval;
			robot.dampingCoefficient = dampingCoefficient;
			robot.springConstant = springConstant;
			// robot.setPoint(Vector2D.random(10.0, 10.0).add(50.0,
			// 50.0).toPoint2D());
			robot.setPoint(new Point2D((i / 10) + 1, (i % 10) + 1).multiply(2.0).toPoint2D());
			sensorNetwork.add(robot);
		}
		sensorNetwork.obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(), size, size), true, true));
		sensorNetwork.obstacles.addAll(Obstacle2D.getType1());

		SensorNetworkSimulator sensorNetworkSimulator = new SensorNetworkSimulator(sensorNetwork, new YAVFMobileSensorNetworkCanvas(sensorNetwork));

		return sensorNetworkSimulator;
	}

	public static void test() {
		System.out.println(!new Circle(new Point2D(), 0.0).containsExcludeEdge(new Point2D()));
		System.out.println(new Point2D(100, 100).onTheLeftSideOf(new Line2D(new Point2D(), new Vector2D(100, 0))));
		Obstacle2D o = new Obstacle2D(Polygon2D.rectangle(new Point2D(), 100.0, 100.0));
		System.out.println(o.getAreaSize() == 10000);
		// System.out.println(o.getPoints().get(new Point2D(50, 50)));
		// System.out.println(o.getPoints().get(new Point2D()));
		// System.out.println(o.getPoints().get(new Point2D(-10, -10)) == null);
		System.out.println(o.contains(new Point2D(50, 0)));
		System.out.println(!o.contains(new Point2D(50, 0), false));
		System.out.println(o.contains(new Point2D(50, 50)));
		System.out.println(!o.contains(new Point2D(50, 150)));
		System.out.println(o.getDisplacementToAvoidCollisionWithEdge(new Point2D(100, 100), new Vector2D(10, -10), o.getEdges().get(2)).y == 0);
		o = new Obstacle2D(Polygon2D.rectangle(new Point2D(), 100.0, 100.0), true);
		System.out.println(o.contains(new Point2D(50, 0)));
		System.out.println(!o.contains(new Point2D(50, 0), false));
		System.out.println(!o.contains(new Point2D(50, 50)));
		System.out.println(o.contains(new Point2D(50, 150)));
		o = new Obstacle2D(Polygon2D.rectangle(new Point2D(), 400.0, 400.0), true);
		Point2D p = new Point2D(396.49833858633343, 400.01435785119656);
		for (LineSegment2D edge : o.getEdges()) {
			Point2D nearPoint = p.getNearestPointOn(edge);
			System.out.println(nearPoint.y == 0.0 || nearPoint.y == 400.0);
		}
		p = new Point2D();
		Vector2D displacement = new Vector2D(1, -2);
		System.out.println(p.add(displacement).onTheLeftSideOf(o.getEdges().get(3)));
		Vector2D nextDisplacement = o.getDisplacementToAvoidCollision(p, displacement);
		System.out.println(nextDisplacement.x == 1.0);
		System.out.println(nextDisplacement.y == 0.0);
		System.out.println(new Point2D(0.0, 0.1).getDisplacementToAvoidCollisionFrom(new Vector2D(-1, -1), new LineSegment2D(new Point2D(0, 100), new Point2D())).y == -0.1);
		System.out.println(o.getDisplacementToAvoidCollision(new Point2D(21, 1), new Vector2D(0.3, -1.8)).y == -1);
		System.out.println(o.getDisplacementToAvoidCollision(new Point2D(), new Vector2D(-1, 0)).x == 0);
		CircularSector circularSector = new CircularSector(0.0, Math.PI);
		System.out.println(circularSector.contains(Math.PI / 2));
		System.out.println(!circularSector.contains(-Math.PI / 2));
		circularSector = new CircularSector(Math.PI, 0.0);
		System.out.println(!circularSector.contains(Math.PI / 2));
		System.out.println(circularSector.contains(-Math.PI / 2));
		circularSector = new CircularSector(-Math.PI / 2, Math.PI / 2);
		System.out.println(circularSector.contains(0.0));
		System.out.println(!circularSector.contains(-Math.PI));
		circularSector = new CircularSector(Math.PI / 2, -Math.PI / 2);
		System.out.println(!circularSector.contains(0.0));
		System.out.println(circularSector.contains(-Math.PI));
		System.out.println(new Circle(new Point2D(), 10.0).getDisplacementToAvoidCollisionFrom(new Vector2D(10, 0), new Circle(new Point2D(40, 0), 10.0)).equals(new Vector2D(10, 0)));
		System.out.println(new Circle(new Point2D(), 10.0).getDisplacementToAvoidCollisionFrom(new Vector2D(10, 0), new Circle(new Point2D(10, 0), 10.0)).equals(new Vector2D(-10, 0)));
		System.out.println(new Circle(new Point2D(), 10.0).getDisplacementToAvoidCollisionFrom(new Vector2D(60, 0), new Circle(new Point2D(60, 0), 10.0)).equals(new Vector2D(40, 0)));
		System.out.println(!new LineSegment2D(new Point2D(), new Point2D(0, 200)).isVisibleFrom(new Point2D(-51, 100), new LineSegment2D(new Point2D(-50, 50), new Point2D(-50, 150))));
		System.out.println(!new LineSegment2D(new Point2D(), new Point2D(0, 200)).isVisibleFrom(new Point2D(51, 100), new LineSegment2D(new Point2D(50, 50), new Point2D(50, 150))));
		Circle.test();
	}
}
