package simulator;

import java.util.Random;

import mobilesensornetwork.SpringVfMobileSensorNetwork;
import mobilesensornetwork.gui.MobileSensorNetworkChartPanel;
import mobilesensornetwork.gui.MobileSensorNetworkInfoPanel;
import mobilesensornetwork.gui.MobileSensorNetworkTable;
import mobilesensornetwork.gui.SpringVFMobileSensorNetworkCanvas;

public class SpringVfMobileSensorNetworkGuiSimulator extends MobileSensorNetworkGuiSimulator {
	public Double dampingCoefficient, springConstant;

	protected SpringVfMobileSensorNetwork getSpringVfMobileSensorNetwork() {
		return (SpringVfMobileSensorNetwork) getMobileSensorNetwork();
	}

	public void setup() {
		random = new Random(seed);
		setMobileSensorNetwork(new SpringVfMobileSensorNetwork());
		getSpringVfMobileSensorNetwork().setIterateInterval(iterationInterval);
		getSpringVfMobileSensorNetwork().setSensingArea(sensingArea);
		getSpringVfMobileSensorNetwork().prepareNodes(robotCount, sensorRobotParameters, dampingCoefficient, springConstant);
		getSpringVfMobileSensorNetwork().scatter(25.0, 25.0, random);

		mobileSensorNetworkCanvas = new SpringVFMobileSensorNetworkCanvas(getSpringVfMobileSensorNetwork());
		mobileSensorNetworkTable = new MobileSensorNetworkTable(getSpringVfMobileSensorNetwork());
		mobileSensorNetworkInfoPanel = new MobileSensorNetworkInfoPanel(getSpringVfMobileSensorNetwork(), this);
		mobileSensorNetworkChartPanel = new MobileSensorNetworkChartPanel(getSpringVfMobileSensorNetwork());
	}

	public void update() {
		if (!getSpringVfMobileSensorNetwork().isRunning() || getSpringVfMobileSensorNetwork().getIterationNo() > maxIteration) {
			stop();
			return;
		}
		getSpringVfMobileSensorNetwork().incrementIterationNo();
		getSpringVfMobileSensorNetwork().resetState();
		getSpringVfMobileSensorNetwork().sendBeacon();
		getSpringVfMobileSensorNetwork().resetConnections();
		getSpringVfMobileSensorNetwork().updatePotential();
		getSpringVfMobileSensorNetwork().createSpringConnections();
		getSpringVfMobileSensorNetwork().calculateVirtualForce();
		getSpringVfMobileSensorNetwork().updateAreaCoverageCalculator();
		getSpringVfMobileSensorNetwork().getSensingData();
		getSpringVfMobileSensorNetwork().updateEventCoverageCalculator(iterationInterval);
		getSpringVfMobileSensorNetwork().getEventsData();
		getSpringVfMobileSensorNetwork().transferMessages();
		getSpringVfMobileSensorNetwork().move(getSpringVfMobileSensorNetwork().getIterateInterval());
		updateGui();
	}
}
