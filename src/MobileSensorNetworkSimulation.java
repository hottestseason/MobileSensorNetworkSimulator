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
	static Double iterateInterval = 1.0;
	static Integer robotCount = 3;
	static Double wirelessRange = 20.0;
	static Double sensorRange = 8.0;
	static Double robotWeight = 1.0;
	static Double robotSize = 0.5;
	static Double idealDistance = sensorRange * Math.sqrt(3);
	static private Random random = new Random(0);

	public static void main(String[] args) {
		// test();

		// getYAVFSimulator(20, 0.25, 0.8).start();
		// getSpringVFSimulator(20, 0.25, 0.7).start();
		getSpringConstantAndDampingCoefficientRelation(0.25);
	}

	public static void getSpringConstantAndDampingCoefficientRelation(double springConstant) {
		SensorNetworkSimulator.gui = false;
		int[] robotCounts = { 2, 4, 8, 16, 32 };
		for (int robotCount : robotCounts) {
			for (int seed = 0; seed < 10; seed++) {
				random = new Random(seed);
				for (Double dampingCoefficient = 0.2; dampingCoefficient < 3.0; dampingCoefficient += 0.01) {
					System.out.print(seed + " ");

					SensorNetwork sensorNetwork = new SensorNetwork();

					for (int i = 0; i < robotCount; i++) {
						Robot robot = new SpringVFRobot(sensorNetwork, i, wirelessRange, sensorRange, robotWeight, robotSize, iterateInterval, idealDistance, springConstant, dampingCoefficient);
						sensorNetwork.add(robot);
						robot.setPoint(Vector2D.random(10.0, 10.0, random).add(50.0, 50.0).toPoint2D());
					}
					sensorNetwork.obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(), 100.0, 100.0), true, true));

					SensorNetworkSimulator sensorNetworkSimulator = new SensorNetworkSimulator(sensorNetwork);
					sensorNetworkSimulator.start();
				}
			}
		}
	}

	public static SensorNetworkSimulator getSpringVFSimulator(Integer robotCount, Double springConstant, Double dampingCoefficient) {
		SensorNetwork sensorNetwork = new SensorNetwork();

		for (int i = 0; i < robotCount; i++) {
			Robot robot = new SpringVFRobot(sensorNetwork, i, wirelessRange, sensorRange, robotWeight, robotSize, iterateInterval, idealDistance, springConstant, dampingCoefficient);
			sensorNetwork.add(robot);
			// robot.setPoint(Vector2D.random(10.0, 10.0, random).add(50.0,
			// 50.0).toPoint2D());
			robot.setPoint(new Point2D((i / 10) + 1, (i % 10) + 1));
		}
		sensorNetwork.obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(), 100.0, 100.0), true, true));
		sensorNetwork.obstacles.addAll(Obstacle2D.getType1());

		SensorNetworkSimulator sensorNetworkSimulator = new SensorNetworkSimulator(sensorNetwork);

		return sensorNetworkSimulator;
	}

	public static SensorNetworkSimulator getYAVFSimulator(Integer robotCount, Double springConstant, Double dampingCoefficient) {
		SensorNetwork sensorNetwork = new SensorNetwork();

		for (int i = 0; i < robotCount; i++) {
			YAVFRobot robot = new YAVFRobot(sensorNetwork, i, wirelessRange, sensorRange, robotWeight, robotSize, iterateInterval);
			robot.idealDistance = idealDistance;
			robot.springConstant = springConstant;
			robot.dampingCoefficient = dampingCoefficient;
			robot.setPoint(Vector2D.random(25.0, 25.0).add(50.0, 50.0).toPoint2D());
			// robot.setPoint(new Point2D((i / 10) + 1, (i % 10) + 1));
			sensorNetwork.add(robot);
		}
		sensorNetwork.obstacles.add(new Obstacle2D(Polygon2D.rectangle(new Point2D(), 100.0, 100.0), true, true));
		// sensorNetwork.obstacles.addAll(Obstacle2D.getType1());

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
