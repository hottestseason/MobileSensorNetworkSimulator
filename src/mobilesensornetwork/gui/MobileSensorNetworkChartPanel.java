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

import sensornetwork.SensorNode;

public class MobileSensorNetworkChartPanel extends JPanel {
	private MobileSensorNetwork mobileSensorNetwork;
	private XYSeries areaCoverageSeries = new XYSeries("Area Coverage");
	private XYSeries eventCoverageSeries = new XYSeries("Event Coverage");
	private XYSeries startedNodesSeries = new XYSeries("Started Nodes");
	private XYSeries remainedBattery = new XYSeries("Remained Battery");
	private XYSeries endedNodesSeries = new XYSeries("Ended Nodes");
	private JFreeChart coverageChart;

	private Integer lastAddedNo = 0;

	public MobileSensorNetworkChartPanel(MobileSensorNetwork mobileSensorNetwork) {
		this.mobileSensorNetwork = mobileSensorNetwork;
		XYSeriesCollection coverageDataset = new XYSeriesCollection();
		coverageDataset.addSeries(areaCoverageSeries);
		// coverageDataset.addSeries(eventCoverageSeries);
		coverageDataset.addSeries(startedNodesSeries);
		coverageDataset.addSeries(endedNodesSeries);
		coverageDataset.addSeries(remainedBattery);
		coverageChart = ChartFactory.createXYLineChart(null, "Time (s)", "Ratio (%)", coverageDataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) coverageChart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.getRangeAxis().setRange(0.0, 100.0);
		ChartPanel coveragePanel = new ChartPanel(coverageChart);
		coveragePanel.setPreferredSize(new Dimension(750, 593));
		add(coveragePanel);

		areaCoverageSeries.add(0.0, 0.0);
	}

	public void update() {
		for (int i = lastAddedNo + 1; i <= mobileSensorNetwork.getIterationNo(); i++) {
			if (i % 5 == 0) {
				startedNodesSeries.add(i * mobileSensorNetwork.getIterationInterval(), (double) mobileSensorNetwork.getStartedNodeSize(i) * 100.0 / mobileSensorNetwork.size(), false);
				Integer endedNodeSize = mobileSensorNetwork.getEndedNodeSize(i);
				if (endedNodeSize > 0) {
					endedNodesSeries.add(i * mobileSensorNetwork.getIterationInterval(), (double) endedNodeSize * 100.0 / mobileSensorNetwork.size(), false);
				}
				remainedBattery.add(i * mobileSensorNetwork.getIterationInterval(), (double) mobileSensorNetwork.getRemainedBatteryRatio(i) * 100.0, false);
			}
		}
		startedNodesSeries.fireSeriesChanged();
		endedNodesSeries.fireSeriesChanged();
		remainedBattery.fireSeriesChanged();
		int start = Math.max(1, Math.min(lastAddedNo + 1, mobileSensorNetwork.getIterationNo()) - mobileSensorNetwork.getMaxMessageHop() * mobileSensorNetwork.getSensingInterval());
		start = ((int) (start / mobileSensorNetwork.getSensingInterval())) * mobileSensorNetwork.getSensingInterval();
		for (int i = start; i < mobileSensorNetwork.getIterationNo(); i += mobileSensorNetwork.getSensingInterval()) {
			Double areaCoverage = mobileSensorNetwork.getAreaCoverage(i);
			if (areaCoverage != null) {
				int updateIndex = areaCoverageSeries.indexOf(i * mobileSensorNetwork.getIterationInterval());
				if (updateIndex >= 0) {
					areaCoverageSeries.remove(updateIndex);
				}
				areaCoverageSeries.add(i * mobileSensorNetwork.getIterationInterval(), (double) areaCoverage * 100.0, false);
			}
		}
		areaCoverageSeries.fireSeriesChanged();
		lastAddedNo = mobileSensorNetwork.getIterationNo();
	}
}
