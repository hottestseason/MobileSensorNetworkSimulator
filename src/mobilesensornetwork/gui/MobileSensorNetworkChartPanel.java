package mobilesensornetwork.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import mobilesensornetwork.MobileSensorNetwork;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MobileSensorNetworkChartPanel extends JPanel {
	private MobileSensorNetwork mobileSensorNetwork;
	private XYSeries areaCoverageSeries = new XYSeries("Area Coverage");
	private XYSeries eventCoverageSeries = new XYSeries("Event Coverage");
	private XYSeries startedNodesSeries = new XYSeries("Started Nodes");
	private JFreeChart coverageChart;

	public MobileSensorNetworkChartPanel(MobileSensorNetwork mobileSensorNetwork) {
		this.mobileSensorNetwork = mobileSensorNetwork;
		XYSeriesCollection coverageDataset = new XYSeriesCollection();
		coverageDataset.addSeries(areaCoverageSeries);
		// coverageDataset.addSeries(eventCoverageSeries);
		coverageDataset.addSeries(startedNodesSeries);
		coverageChart = ChartFactory.createXYLineChart(null, "IterationNo", "ratio", coverageDataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) coverageChart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.getRangeAxis().setRange(0.0, 1.0);
		ChartPanel coveragePanel = new ChartPanel(coverageChart);
		coveragePanel.setPreferredSize(new Dimension(750, 593));
		add(coveragePanel);
	}

	public void update() {
		startedNodesSeries.add((Number) mobileSensorNetwork.getIterationNo(), (double) mobileSensorNetwork.getStartedNodeSize() / mobileSensorNetwork.size());
		// if (mobileSensorNetwork.getIterationNo() %
		// (mobileSensorNetwork.getMaxMessageHop() / 2) != 0) {
		// return;
		// }
		for (int i = Math.max(0, mobileSensorNetwork.getIterationNo() - mobileSensorNetwork.getMaxMessageHop()); i < mobileSensorNetwork.getIterationNo(); i++) {
			// if (i % 5 != 0) {
			// continue;
			// }
			Double areaCoverage = mobileSensorNetwork.getAreaCoverage(i);
			if (areaCoverage != null) {
				try {
					areaCoverageSeries.remove((Number) i);
				} catch (ArrayIndexOutOfBoundsException e) {
				}
				areaCoverageSeries.add((Number) i, areaCoverage);
			}
			// Double eventCoverage =
			// mobileSensorNetwork.getEventCoverage(i);
			// if (eventCoverage != null) {
			// try {
			// eventCoverageSeries.remove((Number) i);
			// } catch (ArrayIndexOutOfBoundsException e) {
			// }
			// eventCoverageSeries.add((Number) i, eventCoverage);
			// }
		}
		eventCoverageSeries.fireSeriesChanged();
	}
}
