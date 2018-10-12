package cz.vutbr.fit.dashapp.view.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import cz.vutbr.fit.dashapp.view.IComponent;

/**
 * Histogram GUI component.
 * 
 * @author Jiri Hynek
 *
 */
public class Histogram implements IComponent {

	private JFrame frame;
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private DefaultCategoryDataset dataset;

	private int[] histogram;
	private String label;
	
	public static final String IMPLICIT_LABEL = "Histogram";
	
	public Histogram(int[] histogram) {
		this(IMPLICIT_LABEL, histogram);
	}

	public Histogram(String label, int[] histogram) {
		this.label = label;
		this.histogram = histogram;
		initDataset();
		initGUI();
	}

	private void initDataset() {
		// create the dataset...
		dataset = new DefaultCategoryDataset();

		for (int i = 0; i < histogram.length; i++) {
			dataset.addValue(histogram[i], "n", String.valueOf(i));
		}
	}

	private void initGUI() {
		chart = ChartFactory.createBarChart(
				this.label, // chart title
				"Color", // domain axis label
				"Pixel count", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tool tips
				false // URLs?
		);

		chart.setBackgroundPaint(SystemColor.white);
		chart.setBorderPaint(SystemColor.black);
		final CategoryPlot plot = chart.getCategoryPlot();
		BarRenderer.setDefaultBarPainter(new StandardBarPainter());
		BarRenderer renderer = new BarRenderer();
		renderer.setSeriesPaint(0, Color.black);
		plot.setDomainGridlinePaint(SystemColor.black);
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		BarRenderer.setDefaultShadowsVisible(false);
		renderer.setShadowXOffset(0);
		renderer.setShadowYOffset(0);
		plot.setRenderer(renderer);

		chartPanel = new ChartPanel(chart);
	}

	@Override
	public JComponent getComponent() {
		return chartPanel;
	}

	public void openWindow() {
		frame = new JFrame();
		Toolkit toolkit = frame.getToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setBounds((screenSize.width / 2 - 450), screenSize.height / 2 - 300, 900, 600);
		frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
		frame.setVisible(true);
	}

}
