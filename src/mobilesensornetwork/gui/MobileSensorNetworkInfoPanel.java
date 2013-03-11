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
	private JLabel iterationNoLabel = new JLabel();
	private JLabel runningRobotsLabel = new JLabel();
	private JLabel sensedPointsLabel = new JLabel();
	private JLabel otherLabel = new JLabel();
	private ArrayList<JButton> controlButton = new ArrayList<JButton>();

	public MobileSensorNetworkInfoPanel(MobileSensorNetwork sensorNetwork, ActionListener actionListener) {
		this.sensorNetwork = sensorNetwork;

		setLayout(new BorderLayout());

		JPanel pageStartPanel = new JPanel();
		pageStartPanel.setLayout(new FlowLayout());
		pageStartPanel.add(iterationNoLabel);
		pageStartPanel.add(runningRobotsLabel);
		pageStartPanel.add(sensedPointsLabel);
		pageStartPanel.add(otherLabel);
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
		runningRobotsLabel.setText("RunningRobots: " + sensorNetwork.getRunningNodes().size());
		sensedPointsLabel.setText("SensedPoints: " + sensorNetwork.sensedAreas);
		otherLabel.setText(sensorNetwork.toString());
	}
}