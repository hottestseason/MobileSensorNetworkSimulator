package simulator;

import geom.Point2D;
import mobilesensornetwork.YaVfMobileSensorNetwork;
import mobilesensornetwork.gui.MobileSensorNetworkChartPanel;
import mobilesensornetwork.gui.MobileSensorNetworkInfoPanel;
import mobilesensornetwork.gui.MobileSensorNetworkTable;
import mobilesensornetwork.gui.YaVfMobileSensorNetworkCanvas;

public class YaVfMobileSensorNetworkGuiSimulator extends SpringVfMobileSensorNetworkGuiSimulator {
	protected YaVfMobileSensorNetwork getYavfMobileSensorNetwork() {
		return (YaVfMobileSensorNetwork) getMobileSensorNetwork();
	}

	public void setup() {
		setMobileSensorNetwork(new YaVfMobileSensorNetwork());
		getYavfMobileSensorNetwork().setIterateInterval(iterationInterval);
		getYavfMobileSensorNetwork().setSensingInterval(sensingInterval);
		getYavfMobileSensorNetwork().setSensingArea(sensingArea);
		getYavfMobileSensorNetwork().prepareNodes(robotCount, sensorRobotParameters, dampingCoefficient, springConstant);
		getYavfMobileSensorNetwork().scatter(25.0, 25.0);
		getYavfMobileSensorNetwork().scatter(new Point2D(60.0, 60.0), 50.0, 50.0);
		getYavfMobileSensorNetwork().getSinkNode().setPoint(new Point2D(10.0, 10.0));

		mobileSensorNetworkCanvas = new YaVfMobileSensorNetworkCanvas(getYavfMobileSensorNetwork());
		mobileSensorNetworkTable = new MobileSensorNetworkTable(getYavfMobileSensorNetwork());
		mobileSensorNetworkInfoPanel = new MobileSensorNetworkInfoPanel(getYavfMobileSensorNetwork(), this);
		mobileSensorNetworkChartPanel = new MobileSensorNetworkChartPanel(getYavfMobileSensorNetwork());
	}

	public void update() {
		if (getYavfMobileSensorNetwork().getIterationNo() >= maxIteration) {
			stop();
			return;
		}
		long start = System.nanoTime();
		getYavfMobileSensorNetwork().startIteration();
		System.out.print(getYavfMobileSensorNetwork().getIterationNo() + ": ");

		long before = System.nanoTime();
		getYavfMobileSensorNetwork().resetState();
		getYavfMobileSensorNetwork().sendBeacon();
		getYavfMobileSensorNetwork().resetConnections();
		getYavfMobileSensorNetwork().updatePotential();
		getYavfMobileSensorNetwork().createSpringConnections();
		getYavfMobileSensorNetwork().calculateVirtualForce();

		if (getYavfMobileSensorNetwork().getIterationNo() % sensingInterval == 0) {
			before = System.nanoTime();
			getYavfMobileSensorNetwork().updateAreaCoverageCalculator();
			getYavfMobileSensorNetwork().getSensingData();
			System.out.print("areaData " + (System.nanoTime() - before) / 1000L + "us ");
		}

		before = System.nanoTime();
		getYavfMobileSensorNetwork().updateEventCoverageCalculator(iterationInterval);
		getYavfMobileSensorNetwork().getEventsData();
		System.out.print("eventsData " + (System.nanoTime() - before) / 1000L + "us ");

		getYavfMobileSensorNetwork().transferMessages();
		getYavfMobileSensorNetwork().adjustNodeSize();

		getYavfMobileSensorNetwork().move(getYavfMobileSensorNetwork().getIterationInterval());

		getYavfMobileSensorNetwork().finishIteration();

		updateGui();
	}
}
