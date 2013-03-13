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
		coverageChart = ChartFactory.createXYLineChart(null, "Time (s)", "Ratio (%)", coverageDataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) coverageChart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.getRangeAxis().setRange(0.0, 100.0);
		ChartPanel coveragePanel = new ChartPanel(coverageChart);
		coveragePanel.setPreferredSize(new Dimension(750, 593));
		add(coveragePanel);
	}

	public void update() {
		startedNodesSeries.add(mobileSensorNetwork.getIterationNo() * mobileSensorNetwork.getIterationInterval(), (double) mobileSensorNetwork.getStartedNodeSize() * 100.0 / mobileSensorNetwork.size());
		for (int i = Math.max(0, mobileSensorNetwork.getIterationNo() - mobileSensorNetwork.getMaxMessageHop()); i < mobileSensorNetwork.getIterationNo(); i++) {
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
	}
}
