package mobilesensornetwork;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MobileSensorNetworkTable extends JTable implements TimerListener {
	static int refreshRate = 3;
	static String[] columnNames = { "node", "speed", "remainedEnergy" };
	private MobileSensorNetwork sensorNetwork;
	private DefaultTableModel tableModel;
	private Timer timer;

	public MobileSensorNetworkTable(MobileSensorNetwork sensorNetwork) {
		this.sensorNetwork = sensorNetwork;
		tableModel = new DefaultTableModel(columnNames, sensorNetwork.size());
		setModel(tableModel);
	}

	public void start() {
		timer = new Timer(this, 1.0 / refreshRate);
		timer.start();
	}

	public void iterate() {
		for (int i = 0; i < sensorNetwork.size(); i++) {
			Robot robot = sensorNetwork.get(i);
			tableModel.setValueAt(robot.getId(), i, 0);
			tableModel.setValueAt(robot.getSpeed(), i, 1);
			tableModel.setValueAt(robot.getRemainedEnergy(), i, 2);
		}
	}
}
