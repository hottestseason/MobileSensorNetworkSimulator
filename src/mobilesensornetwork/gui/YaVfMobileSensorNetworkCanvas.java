package mobilesensornetwork.gui;

import java.awt.Color;
import java.awt.Graphics;

import mobilesensornetwork.MobileSensorNetwork;
import mobilesensornetwork.SensorRobot;
import mobilesensornetwork.YaVfRobot;

public class YaVfMobileSensorNetworkCanvas extends SpringVFMobileSensorNetworkCanvas {
	public YaVfMobileSensorNetworkCanvas(MobileSensorNetwork sensorNetwork) {
		super(sensorNetwork);
	}

	public void drawRobot(SensorRobot sensorRobot, Graphics g) {
		if (!sensorRobot.isRunning()) {
			return;
		}
		super.drawRobot(sensorRobot, g);
		YaVfRobot yavfRobot = (YaVfRobot) sensorRobot;
		drawVector(yavfRobot, fixForce(yavfRobot.getForceFromNeighborRobots()), g, Color.blue);
		drawVector(yavfRobot, fixForce(yavfRobot.getAttractiveForceFromWall()), g, Color.red);
	}
}
