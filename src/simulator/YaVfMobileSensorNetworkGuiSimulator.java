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
		getYavfMobileSensorNetwork().setIterateInterval(iterateInterval);
		getYavfMobileSensorNetwork().setSensingArea(sensingArea);
		getYavfMobileSensorNetwork().prepareNodes(robotCount, sensorRobotParameters, dampingCoefficient, springConstant);
		getYavfMobileSensorNetwork().scatter(25.0, 25.0, random);

		mobileSensorNetworkCanvas = new YaVfMobileSensorNetworkCanvas(getYavfMobileSensorNetwork());
		mobileSensorNetworkTable = new MobileSensorNetworkTable(getYavfMobileSensorNetwork());
		mobileSensorNetworkInfoPanel = new MobileSensorNetworkInfoPanel(getYavfMobileSensorNetwork(), this);
		mobileSensorNetworkChartPanel = new MobileSensorNetworkChartPanel(getYavfMobileSensorNetwork());
	}

	public void update() {
		if (!getYavfMobileSensorNetwork().isRunning() || getYavfMobileSensorNetwork().getIterationNo() > maxIteration) {
			stop();
			return;
		}
		long start = System.nanoTime();
		getYavfMobileSensorNetwork().incrementIterationNo();
		System.out.print(getYavfMobileSensorNetwork().getIterationNo() + ":");

		long before = System.nanoTime();
		getYavfMobileSensorNetwork().resetState();
		System.out.print(" state " + (System.nanoTime() - before) / 1000L + "us");
		getYavfMobileSensorNetwork().sendBeacon();

		before = System.nanoTime();
		getYavfMobileSensorNetwork().resetConnections();
		System.out.print(", connections " + (System.nanoTime() - before) / 1000L + "us");

		before = System.nanoTime();
		getYavfMobileSensorNetwork().updatePotential();
		System.out.print(", potential " + (System.nanoTime() - before) / 1000L + "us");

		before = System.nanoTime();
		getYavfMobileSensorNetwork().createSpringConnections();
		System.out.print(", springs " + (System.nanoTime() - before) / 1000L + "us");

		before = System.nanoTime();
		getYavfMobileSensorNetwork().calculateVirtualForce();
		System.out.print(", virtualForce " + (System.nanoTime() - before) / 1000L + "us");

		before = System.nanoTime();
		getYavfMobileSensorNetwork().updateAreaCoverageCalculator();
		getYavfMobileSensorNetwork().getSensingData();
		System.out.print(", areaData " + (System.nanoTime() - before) / 1000L + "us");

		before = System.nanoTime();
		getYavfMobileSensorNetwork().updateEventCoverageCalculator(iterateInterval);
		getYavfMobileSensorNetwork().getEventsData();
		System.out.print(", eventsData " + (System.nanoTime() - before) / 1000L + "us");

		before = System.nanoTime();
		getYavfMobileSensorNetwork().transferMessages();
		System.out.print(", transfer " + (System.nanoTime() - before) / 1000L + "us");
		getYavfMobileSensorNetwork().adjustNodeSize();
		getYavfMobileSensorNetwork().move(getYavfMobileSensorNetwork().getIterateInterval());

		before = System.nanoTime();
		updateGui();
		System.out.print(", updateGui " + (System.nanoTime() - before) / 1000L + "us");
		System.out.println(", " + (System.nanoTime() - start) / 1000L + "us");
	}
}
