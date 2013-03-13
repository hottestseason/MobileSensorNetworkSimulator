package simulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mobilesensornetwork.gui.MobileSensorNetworkCanvas;
import mobilesensornetwork.gui.MobileSensorNetworkChartPanel;
import mobilesensornetwork.gui.MobileSensorNetworkInfoPanel;
import mobilesensornetwork.gui.MobileSensorNetworkTable;
import utils.Timer;
import utils.TimerListener;

public abstract class MobileSensorNetworkGuiSimulator extends MobileSensorNetworkSimulator implements TimerListener, ActionListener {
	static int width = 1366, height = 768;
	static Double speed = 100.0;

	protected MobileSensorNetworkCanvas mobileSensorNetworkCanvas;
	protected MobileSensorNetworkTable mobileSensorNetworkTable;
	protected MobileSensorNetworkInfoPanel mobileSensorNetworkInfoPanel;
	protected MobileSensorNetworkChartPanel mobileSensorNetworkChartPanel;

	private Timer timer;

	public void startGui() {
		JFrame frame = new JFrame();

		frame.setLayout(new BorderLayout());
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(mobileSensorNetworkCanvas, BorderLayout.CENTER);
		centerPanel.add(mobileSensorNetworkInfoPanel, BorderLayout.PAGE_END);
		frame.add(centerPanel, BorderLayout.CENTER);
		JPanel lineEndPanel = new JPanel();
		lineEndPanel.setLayout(new BorderLayout());
		JScrollPane tableScrollPane = new JScrollPane(mobileSensorNetworkTable);
		tableScrollPane.setPreferredSize(new Dimension(0, (int) (height * 0.18)));
		lineEndPanel.add(tableScrollPane, BorderLayout.PAGE_END);
		lineEndPanel.add(mobileSensorNetworkChartPanel, BorderLayout.CENTER);
		mobileSensorNetworkInfoPanel.setPreferredSize(new Dimension(mobileSensorNetworkCanvas.getWidth(), height - mobileSensorNetworkCanvas.getHeight() - 20));
		frame.add(lineEndPanel, BorderLayout.LINE_END);

		frame.setSize(width, height);

		frame.setVisible(true);
	}

	public void start() {
		if (timer == null || !timer.isRunning()) {
			timer = new Timer(this, iterationInterval / speed);
			timer.start();
		}
	}

	public void stop() {
		timer.stop();
	}

	public void resume() {
		timer.start();
	}

	public void restart() {
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("start")) {
			start();
		} else if (command.equals("stop")) {
			stop();
		} else if (command.equals("resume")) {
			resume();
		} else if (command.equals("restart")) {
			restart();
		}
	}

	protected void updateGui() {
		long before = System.nanoTime();
		mobileSensorNetworkInfoPanel.update();
		System.out.print("infoPanel " + (System.nanoTime() - before) / 1000L + "us ");
		if (mobileSensorNetworkInfoPanel.updatesCanvas()) {
			before = System.nanoTime();
			mobileSensorNetworkCanvas.repaint();
			System.out.print("canvas " + (System.nanoTime() - before) / 1000L + "us ");
		}
		if (mobileSensorNetworkInfoPanel.updatesNodesTable()) {
			before = System.nanoTime();
			mobileSensorNetworkTable.update();
			System.out.print("table " + (System.nanoTime() - before) / 1000L + "us ");
		}
		if (mobileSensorNetworkInfoPanel.updatesChart()) {
			before = System.nanoTime();
			mobileSensorNetworkChartPanel.update();
			System.out.print("chart " + (System.nanoTime() - before) / 1000L + "us ");
		}
	}
}
