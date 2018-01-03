package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;


import org.jfree.ui.ApplicationFrame;

public class GrapgView extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DefaultCategoryDataset dataset;

	/**
	 * creates a new Graph view
	 * 
	 * @param title
	 *            the title of the graph window
	 */
	public GrapgView(String title) {
		super(title);
		dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(chartPanel);
	}

	/**
	 * creates a new dataset
	 * 
	 * @return the dataSet created
	 */
	private DefaultCategoryDataset createDataset() {

		// row keys...

		// final String series2 = "Second";
		// final String series3 = "Third";

		// create the dataset...
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// dataset.addValue(0.0, ENERGY_STR, "0");

		return dataset;

	}

	/**
	 * getter for the dataSet
	 * 
	 * @return the dataSet of the graph
	 */
	public DefaultCategoryDataset getDataSet() {
		return dataset;
	}

	/**
	 * Creates a sample chart.
	 * 
	 * @param dataset
	 *            a dataset.
	 * 
	 * @return The chart.
	 */
	private JFreeChart createChart(final CategoryDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createLineChart(
				"best energy over genrations", // chart title
				"Generation", // domain axis label
				"Best energy", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips
				false // urls
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);
		// legend.setShapeScaleX(1.5);
		// legend.setShapeScaleY(1.5);
		// legend.setDisplaySeriesLines(true);

		chart.setBackgroundPaint(Color.white);

		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.white);

		// customise the range axis...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);

		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		// renderer.setDrawShapes(true);

		renderer.setSeriesStroke(0, new BasicStroke(2.0f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
				new float[] { 10.0f, 6.0f }, 0.0f));
		renderer.setSeriesStroke(1, new BasicStroke(2.0f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
				new float[] { 6.0f, 6.0f }, 0.0f));
		renderer.setSeriesStroke(2, new BasicStroke(2.0f,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
				new float[] { 2.0f, 6.0f }, 0.0f));
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;
	}
}
