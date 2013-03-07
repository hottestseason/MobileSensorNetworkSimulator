package mobilesensornetwork.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mobilesensornetwork.MobileSensorNetwork;

public class MobileSensorNetworkInfoPanel extends JPanel {
	private MobileSensorNetwork sensorNetwork;
	private JLabel iterationNoLabel;
	private JLabel runningRobotsLabel;
	private JLabel sensedPointsLabel;
	private ArrayList<JButton> controlButton = new ArrayList<JButton>();

	public MobileSensorNetworkInfoPanel(MobileSensorNetwork sensorNetwork, ActionListener actionListener) {
		this.sensorNetwork = sensorNetwork;

		setLayout(new BorderLayout());

		JPanel pageStartPanel = new JPanel();
		pageStartPanel.setLayout(new FlowLayout());
		iterationNoLabel = new JLabel();
		pageStartPanel.add(iterationNoLabel);
		runningRobotsLabel = new JLabel();
		pageStartPanel.add(runningRobotsLabel);
		sensedPointsLabel = new JLabel();
		pageStartPanel.add(sensedPointsLabel);
		add(pageStartPanel, BorderLayout.PAGE_START);

		JPanel centerPanel = new JPanel();
		add(centerPanel, BorderLayout.CENTER);

		JPanel pageEndPanel = new JPanel();
		String[] controls = { "start", "stop", "resume", "restart" };
		for (String control : controls) {
			JButton controlButton = new JButton(control.substring(0, 1).toUpperCase() + control.substring(1));
			controlButton.setActionCommand(control);
			controlButton.addActionListener(actionListener);
			pageEndPanel.add(controlButton);
		}
		add(pageEndPanel, BorderLayout.PAGE_END);

		update();
	}

	public void update() {
		iterationNoLabel.setText("IterationNo: " + sensorNetwork.getIterationNo());
		runningRobotsLabel.setText("RunningRobots: " + sensorNetwork.getRunningNodeSize());
		sensedPointsLabel.setText("SensedPoints: " + sensorNetwork.sensedAreas);
	}
}
