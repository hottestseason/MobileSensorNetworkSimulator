package simulator;

import geom.Point2D;
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
		setMobileSensorNetwork(new SpringVfMobileSensorNetwork());
		getSpringVfMobileSensorNetwork().setIterateInterval(iterationInterval);
		getSpringVfMobileSensorNetwork().setSensingInterval(sensingInterval);
		getSpringVfMobileSensorNetwork().setSensingArea(sensingArea);
		getSpringVfMobileSensorNetwork().prepareNodes(robotCount, sensorRobotParameters, dampingCoefficient, springConstant);
		getSpringVfMobileSensorNetwork().scatter(25.0, 25.0);
		getSpringVfMobileSensorNetwork().scatter(new Point2D(60.0, 60.0), 50.0, 50.0);
		getSpringVfMobileSensorNetwork().getSinkNode().setPoint(new Point2D(10.0, 10.0));

		mobileSensorNetworkCanvas = new SpringVFMobileSensorNetworkCanvas(getSpringVfMobileSensorNetwork());
		mobileSensorNetworkTable = new MobileSensorNetworkTable(getSpringVfMobileSensorNetwork());
		mobileSensorNetworkInfoPanel = new MobileSensorNetworkInfoPanel(getSpringVfMobileSensorNetwork(), this);
		mobileSensorNetworkChartPanel = new MobileSensorNetworkChartPanel(getSpringVfMobileSensorNetwork());
	}

	public void update() {
		if (getSpringVfMobileSensorNetwork().getIterationNo() >= maxIteration) {
			stop();
			return;
		}
		getSpringVfMobileSensorNetwork().startIteration();
		getSpringVfMobileSensorNetwork().resetState();
		getSpringVfMobileSensorNetwork().sendBeacon();
		getSpringVfMobileSensorNetwork().resetConnections();
		getSpringVfMobileSensorNetwork().updatePotential();
		getSpringVfMobileSensorNetwork().createSpringConnections();
		getSpringVfMobileSensorNetwork().calculateVirtualForce();

		if (getSpringVfMobileSensorNetwork().getIterationNo() % sensingInterval == 0) {
			getSpringVfMobileSensorNetwork().updateAreaCoverageCalculator();
			getSpringVfMobileSensorNetwork().getSensingData();
		}

		getSpringVfMobileSensorNetwork().updateEventCoverageCalculator(iterationInterval);
		getSpringVfMobileSensorNetwork().getEventsData();

		getSpringVfMobileSensorNetwork().transferMessages();
		getSpringVfMobileSensorNetwork().move(getSpringVfMobileSensorNetwork().getIterationInterval());
		getSpringVfMobileSensorNetwork().finishIteration();
		updateGui();
	}
}
