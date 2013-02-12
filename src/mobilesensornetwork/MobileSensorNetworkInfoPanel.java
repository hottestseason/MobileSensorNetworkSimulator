package mobilesensornetwork;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MobileSensorNetworkInfoPanel extends JPanel {
	private MobileSensorNetwork sensorNetwork;
	private JLabel iterationNoLabel;
	private JLabel sumMovedDistanceLabel;
	private JLabel connectivityLabel;

	public MobileSensorNetworkInfoPanel(MobileSensorNetwork sensorNetwork) {
		this.sensorNetwork = sensorNetwork;
		iterationNoLabel = new JLabel();
		sumMovedDistanceLabel = new JLabel();
		connectivityLabel = new JLabel();

		setLayout(new FlowLayout());
		add(iterationNoLabel);
		add(sumMovedDistanceLabel);
		add(connectivityLabel);
	}

	public void update() {
		iterationNoLabel.setText("IterationNo: " + sensorNetwork.getIterationNo());
		sumMovedDistanceLabel.setText("SumMovedDistance: " + sensorNetwork.getSumMovedDistance().intValue());
		connectivityLabel.setText("Connectivity: " + sensorNetwork.getConnectivity());
	}
}
