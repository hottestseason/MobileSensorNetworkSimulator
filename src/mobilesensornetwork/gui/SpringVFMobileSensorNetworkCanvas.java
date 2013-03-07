package mobilesensornetwork.gui;

import java.awt.Color;
import java.awt.Graphics;

import mobilesensornetwork.MobileSensorNetwork;
import mobilesensornetwork.SensorRobot;
import mobilesensornetwork.SpringVFRobot;

public class SpringVFMobileSensorNetworkCanvas extends MobileSensorNetworkCanvas {
	public SpringVFMobileSensorNetworkCanvas(MobileSensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawConnectionBetween(SensorRobot sensorRobotA, SensorRobot sensorRobotB, Graphics g) {
		if (((SpringVFRobot) sensorRobotA).getSpringConnectedRobots().contains(sensorRobotB)) {
			drawLineSegment2D(sensorRobotA, sensorRobotB, g, new Color(32, 32, 32, 128));
		} else {
			drawLineSegment2D(sensorRobotA, sensorRobotB, g, new Color(64, 64, 64, 4));
		}
	}
}
