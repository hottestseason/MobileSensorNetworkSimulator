package simulator;

import java.util.Random;

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
		random = new Random(seed);
		setMobileSensorNetwork(new YaVfMobileSensorNetwork());
		getYavfMobileSensorNetwork().setIterateInterval(iterationInterval);
		getYavfMobileSensorNetwork().setSensingArea(sensingArea);
		getYavfMobileSensorNetwork().prepareNodes(robotCount, sensorRobotParameters, dampingCoefficient, springConstant);
		getYavfMobileSensorNetwork().scatter(25.0, 25.0, random);

		mobileSensorNetworkCanvas = new YaVfMobileSensorNetworkCanvas(getYavfMobileSensorNetwork());
		mobileSensorNetworkTable = new MobileSensorNetworkTable(getYavfMobileSensorNetwork());
		mobileSensorNetworkInfoPanel = new MobileSensorNetworkInfoPanel(getYavfMobileSensorNetwork(), this);
		mobileSensorNetworkChartPanel = new MobileSensorNetworkChartPanel(getYavfMobileSensorNetwork());
	}

	public void update() {
		if (getYavfMobileSensorNetwork().getIterationNo() > maxIteration) {
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

		before = System.nanoTime();
		getYavfMobileSensorNetwork().updateAreaCoverageCalculator();
		getYavfMobileSensorNetwork().getSensingData();
		System.out.print("areaData " + (System.nanoTime() - before) / 1000L + "us ");

		before = System.nanoTime();
		getYavfMobileSensorNetwork().updateEventCoverageCalculator(iterationInterval);
		getYavfMobileSensorNetwork().getEventsData();
		System.out.print("eventsData " + (System.nanoTime() - before) / 1000L + "us ");

		getYavfMobileSensorNetwork().transferMessages();
		getYavfMobileSensorNetwork().adjustNodeSize();

		getYavfMobileSensorNetwork().move(getYavfMobileSensorNetwork().getIterationInterval());

		updateGui();
	}
}
