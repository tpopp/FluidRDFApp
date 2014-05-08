/**
 * 
 */
package Applet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
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

		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[] { 10, 0, 0};
		gbl.rowHeights = new int[] { 0, 0, 0 };
		gbl.columnWeights = new double[] { 0.0, 1.0, 1.0 };
		gbl.rowWeights = new double[] { 1.0, 1.0, 0.0 };
		setLayout(gbl);
		
		
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
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		chartPanel.getChart().setBackgroundPaint(null);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		add(chartPanel, gbc);
		
		GridBagConstraints gbcX1 = new GridBagConstraints();
		GridBagConstraints gbcX2 = new GridBagConstraints();
		GridBagConstraints gbcY1 = new GridBagConstraints();
		GridBagConstraints gbcY2 = new GridBagConstraints();
		GridBagConstraints gbcButton = new GridBagConstraints();
		
		final NumberAxis domain = (NumberAxis) ((XYPlot) chartPanel.getChart()
				.getPlot()).getDomainAxis();
		final NumberAxis range = (NumberAxis) ((XYPlot) chartPanel.getChart()
				.getPlot()).getRangeAxis();
		
		gbcX2.gridx = 2;
		gbcX2.gridy = 2;
		gbcX2.anchor = GridBagConstraints.EAST;
		gbcX2.insets = new Insets(0, 0, 0, 12);
		final JTextField xMax = new JTextField(5);
		xMax.setMinimumSize(new Dimension(30, 20));
		xMax.setText(""+domain.getUpperBound());
//		gbcX2.fill = GridBagConstraints.VERTICAL;
		add(xMax, gbcX2);
		xMax.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				domain.setRange(domain.getLowerBound(), Double.parseDouble(xMax.getText()));
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
		
		gbcX1.gridx = 1;
		gbcX1.gridy = 2;
		gbcX1.anchor = GridBagConstraints.WEST;
		gbcX1.insets = new Insets(0, 70, 0, 0);
		final JTextField xMin = new JTextField(5);
		xMin.setMinimumSize(new Dimension(30, 20));
		xMin.setText(""+domain.getLowerBound());
//		gbcX1.fill = GridBagConstraints.VERTICAL;
		add(xMin, gbcX1);
		xMin.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				domain.setRange(Double.parseDouble(xMin.getText()), domain.getUpperBound());
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
		
		
		gbcY1.gridx = 0;
		gbcY1.gridy = 1;
		gbcY1.anchor = GridBagConstraints.SOUTH;
		gbcY1.insets = new Insets(0, 0, 45, 0);
		final JTextField yMin = new JTextField(5);
		yMin.setMinimumSize(new Dimension(30, 20));
		yMin.setText(""+range.getLowerBound());
//		gbcY1.fill = GridBagConstraints.BOTH;
		add(yMin, gbcY1);
		yMin.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				range.setRange(Double.parseDouble(yMin.getText()), range.getUpperBound());
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
		
		gbcY2.gridx = 0;
		gbcY2.gridy = 0;
		gbcY2.anchor = GridBagConstraints.NORTH;
		gbcY2.insets = new Insets(10, 0, 0, 0);
		final JTextField yMax = new JTextField(5);
		yMax.setMinimumSize(new Dimension(30, 20));
		yMax.setText(""+range.getUpperBound());
//		gbcY2.fill = GridBagConstraints.BOTH;
		add(yMax, gbcY2);
		yMax.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				range.setRange(range.getLowerBound(), Double.parseDouble(yMax.getText()));
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {}
		});
		
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

	public void changeBounds(double x1, double x2, double y1, double y2) {
		NumberAxis domain = (NumberAxis) ((XYPlot) chartPanel.getChart()
				.getPlot()).getDomainAxis();
		NumberAxis range = (NumberAxis) ((XYPlot) chartPanel.getChart()
				.getPlot()).getRangeAxis();
		domain.setRange(x1, x2);
		range.setRange(y1, y2);
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
			XYTitleAnnotation xyta = new XYTitleAnnotation(0.98, 0.98,
					chart.getLegend(), RectangleAnchor.TOP_RIGHT);
			chart.removeLegend();
			plot.addAnnotation(xyta);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		plot.setRenderer(renderer);

		return chart;

	}

	public EmbeddedChart copy() {
		return new EmbeddedChart(title, legends, yvals, gofr);
	}

}
