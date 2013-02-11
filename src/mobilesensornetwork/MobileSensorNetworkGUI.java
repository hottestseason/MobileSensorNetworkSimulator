package mobilesensornetwork;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MobileSensorNetworkGUI extends JFrame implements TimerListener {
	static int width = 1366, height = 768;
	private MobileSensorNetwork sensorNetwork;
	private SensorNetworkCanvas sensorNetworkCanvas;
	private MobileSensorNetworkTable mobileSensorNetworkTable;
	private MobileSensorNetworkInfoPanel mobileSensorNetworkInfoPanel;
	private MobileSensorNetworkChartPanel mobileSensorNetworkChartPanel;
	private Timer timer;

	public MobileSensorNetworkGUI(MobileSensorNetwork sensorNetwork) {
		super("MobileSensorNetwork");
		this.sensorNetwork = sensorNetwork;
		sensorNetworkCanvas = new SensorNetworkCanvas(sensorNetwork);
		mobileSensorNetworkTable = new MobileSensorNetworkTable(sensorNetwork);
		mobileSensorNetworkInfoPanel = new MobileSensorNetworkInfoPanel(sensorNetwork);
		mobileSensorNetworkChartPanel = new MobileSensorNetworkChartPanel(sensorNetwork);

		setLayout(new BorderLayout());
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(sensorNetworkCanvas, BorderLayout.CENTER);
		centerPanel.add(mobileSensorNetworkInfoPanel, BorderLayout.PAGE_END);
		add(centerPanel, BorderLayout.CENTER);
		JPanel lineEndPanel = new JPanel();
		lineEndPanel.setLayout(new BorderLayout());
		lineEndPanel.add(new JScrollPane(mobileSensorNetworkTable), BorderLayout.PAGE_END);
		lineEndPanel.add(mobileSensorNetworkChartPanel, BorderLayout.CENTER);
		add(lineEndPanel, BorderLayout.LINE_END);

		setSize(width, height);

		setVisible(true);
	}

	public void start() {
		timer = new Timer(this, 1.0 / 30);
		timer.start();
		sensorNetworkCanvas.start();
		// mobileSensorNetworkTable.start();
		// mobileSensorNetworkInfoPanel.start();
		// mobileSensorNetworkChartPanel.start();
	}

	public void iterate() {
		mobileSensorNetworkTable.iterate();
		mobileSensorNetworkInfoPanel.iterate();
		mobileSensorNetworkChartPanel.iterate();
	}
}
