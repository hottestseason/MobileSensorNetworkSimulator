package mobilesensornetwork;

import java.awt.Dimension;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MobileSensorNetworkChartPanel extends JPanel implements TimerListener {
	static Integer refreshRate = 20;

	private MobileSensorNetwork mobileSensorNetwork;
	private XYSeries coveraeSeries = new XYSeries("Coverage");
	private JFreeChart coverageChart;
	private Integer lastAddedIterationNo = 0;

	private Timer timer;

	public MobileSensorNetworkChartPanel(MobileSensorNetwork mobileSensorNetwork) {
		this.mobileSensorNetwork = mobileSensorNetwork;
		XYSeriesCollection coverageDataset = new XYSeriesCollection();
		coverageDataset.addSeries(coveraeSeries);
		coverageChart = ChartFactory.createXYLineChart("Coverage", "IterationNo", "Coverage", coverageDataset, PlotOrientation.VERTICAL, false, true, false);
		ChartPanel coveragePanel = new ChartPanel(coverageChart);
		coveragePanel.setPreferredSize(new Dimension(750, 320));
		add(coveragePanel);
	}

	public void start() {
		timer = new Timer(this, 1.0 / refreshRate);
		timer.start();
	}

	public void iterate() {
		Integer nextIterationNo = lastAddedIterationNo + 1;
		if (mobileSensorNetwork.getCoverage(nextIterationNo) != null) {
			coveraeSeries.add(nextIterationNo, mobileSensorNetwork.getCoverage(lastAddedIterationNo + 1));
			lastAddedIterationNo = nextIterationNo;
		}
	}
}
