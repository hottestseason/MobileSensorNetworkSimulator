package mobilesensornetwork;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MobileSensorNetworkInfoPanel extends JPanel implements TimerListener {
	static int refreshRate = 10;
	private MobileSensorNetwork sensorNetwork;
	private JLabel iterationNoLabel;
	private JLabel sumMovedDistanceLabel;
	private JLabel connectivityLabel;

	private Timer timer;

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

	public void start() {
		timer = new Timer(this, 1.0 / refreshRate);
		timer.start();
	}

	public void iterate() {
		iterationNoLabel.setText("IterationNo: " + sensorNetwork.getIterationNo());
		sumMovedDistanceLabel.setText("SumMovedDistance: " + sensorNetwork.getSumMovedDistance().intValue());
		connectivityLabel.setText("Connectivity: " + sensorNetwork.getConnectivity());
	}
}
