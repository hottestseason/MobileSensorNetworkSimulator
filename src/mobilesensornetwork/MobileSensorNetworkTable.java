package mobilesensornetwork;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MobileSensorNetworkTable extends JTable {
	static String[] columnNames = { "node", "speed", "remainedEnergy" };
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
			Robot robot = sensorNetwork.get(i);
			tableModel.setValueAt(robot.getId(), i, 0);
			tableModel.setValueAt(robot.getSpeed(), i, 1);
			tableModel.setValueAt(robot.getRemainedEnergy(), i, 2);
		}
		tableModel.fireTableDataChanged();
	}
}
