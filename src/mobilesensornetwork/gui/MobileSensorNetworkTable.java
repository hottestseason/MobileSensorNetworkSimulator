package mobilesensornetwork.gui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mobilesensornetwork.MobileSensorNetwork;
import mobilesensornetwork.SensorRobot;

public class MobileSensorNetworkTable extends JTable {
	static String[] columnNames = { "node", "speed", "remainedEnergy", "transmissionEnergy", "movementEnergy" };
	private MobileSensorNetwork sensorNetwork;
	private DefaultTableModel tableModel;

	public MobileSensorNetworkTable(MobileSensorNetwork sensorNetwork) {
		this.sensorNetwork = sensorNetwork;
		tableModel = new DefaultTableModel(columnNames, sensorNetwork.size());
		setModel(tableModel);
		getColumn("node").setMaxWidth(50);
		setAutoCreateRowSorter(true);
	}

	public void update() {
		for (int i = 0; i < sensorNetwork.size(); i++) {
			SensorRobot sensorRobot = sensorNetwork.get(i);
			if (sensorRobot.isRunning()) {
				tableModel.setValueAt(sensorRobot.getId(), i, 0);
				tableModel.setValueAt(sensorRobot.getSpeed().getNorm(), i, 1);
				tableModel.setValueAt(sensorRobot.getRemainedEnergy(), i, 2);
				tableModel.setValueAt(sensorRobot.getTransmissionConsumedEnergy(), i, 3);
				tableModel.setValueAt(sensorRobot.getMovementConsumedEnergy(), i, 4);
			}
		}
		tableModel.fireTableDataChanged();
	}
}
