/**
 * 
 */
package Applet;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;

/**
 * @author Tres
 * 
 */
public class EmbeddedChart extends JInternalFrame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1246450656920570148L;
	String title;
	private ChartPanel chartPanel;
	String[] legends;
	List<double[][]> yvals;
	boolean gofr;

	/**
	 * Creates a new demo.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public EmbeddedChart(final String title, String[] legends,
			List<double[][]> yVals, boolean gofr) {

		super();
		this.title = title;
		this.legends = legends;
		this.yvals = yVals;
		this.gofr = gofr;
		updateInfo(legends, yVals, gofr);
	}

	public void restore() {
		chartPanel.restoreAutoBounds();
	}

	public void updateInfo(String[] legends, List<double[][]> yVals,
			boolean gofr) {
		removeAll();
		XYDataset dataset = createDataset(legends, yVals);
		JFreeChart chart = createChart(dataset, title, gofr);
		chartPanel = new ChartPanel(chart);
		chartPanel.setMinimumDrawWidth(0);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMaximumDrawWidth(2000);
		chartPanel.setMaximumDrawHeight(2000);
		add(chartPanel);
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return a sample dataset.
	 */
	private XYDataset createDataset(String[] legends, List<double[][]> yVals) {

		final XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series;
		for (int i = 0; i < legends.length; i++) {
			series = new XYSeries(legends[i]);
			double[][] arr = yVals.get(i);
			int j = 0;
			while (j < arr[0].length && arr[0][j] < 1)
				j++;
			for (; j < arr[0].length; j++)
				series.add(arr[0][j], arr[1][j]);
			dataset.addSeries(series);
		}

		return dataset;

	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the data for the chart.
	 * 
	 * @return a chart.
	 */
	private JFreeChart createChart(final XYDataset dataset, String title,
			boolean gofr) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart(null, // chart
																		// title
				"Radial Distance r", // x axis label
				title, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		if (gofr) {
			XYTitleAnnotation xyta = new XYTitleAnnotation(0.98, 0.98,
					chart.getLegend(), RectangleAnchor.TOP_RIGHT);
			chart.removeLegend();
			plot.addAnnotation(xyta);
		}

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setRenderer(renderer);

		return chart;

	}

	public EmbeddedChart copy() {
		return new EmbeddedChart(title, legends, yvals, gofr);
	}

}
