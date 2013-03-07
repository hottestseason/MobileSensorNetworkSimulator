package simulator;

import mobilesensornetwork.SensorRobotParameters;
import sensornetwork.SensingArea;

public class CompareGuiSimulator {
	SpringVfMobileSensorNetworkGuiSimulator springSimulator;
	YaVfMobileSensorNetworkGuiSimulator yaVfSimulator;

	public CompareGuiSimulator(SensorRobotParameters sensorRobotParameters) {
		springSimulator = new SpringVfMobileSensorNetworkGuiSimulator();
		springSimulator.seed = 0;
		springSimulator.sensingArea = SensingArea.getType3(500.0, 500.0);
		springSimulator.iterateInterval = 0.2;
		springSimulator.sensorRobotParameters = sensorRobotParameters;
		springSimulator.robotCount = 150;
		springSimulator.springConstant = 5.0;
		springSimulator.dampingCoefficient = 4.0;
		springSimulator.setup();

		yaVfSimulator = new YaVfMobileSensorNetworkGuiSimulator();
		yaVfSimulator.seed = 0;
		yaVfSimulator.sensingArea = SensingArea.getType3(500.0, 500.0);
		yaVfSimulator.iterateInterval = 0.2;
		yaVfSimulator.sensorRobotParameters = sensorRobotParameters;
		yaVfSimulator.robotCount = 150;
		yaVfSimulator.springConstant = 5.0;
		yaVfSimulator.dampingCoefficient = 4.0;
		yaVfSimulator.setup();
	}
}
