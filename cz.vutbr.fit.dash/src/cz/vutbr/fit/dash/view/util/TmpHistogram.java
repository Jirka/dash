package cz.vutbr.fit.dash.view.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import cz.vutbr.fit.dash.view.IComponent;

public class TmpHistogram implements IComponent {
	
	private JFrame frame;
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private DefaultCategoryDataset dataset;
	
	public TmpHistogram() {
		initDataset();
		initGUI();
	}

	private void initDataset() {
		// row keys...
        final String series1 = "First";
        final String series2 = "Second";
        final String series3 = "Third";

        // column keys...
        final String category1 = "Category 1";
        final String category2 = "Category 2";
        final String category3 = "Category 3";
        final String category4 = "Category 4";
        final String category5 = "Category 5";

        // create the dataset...
        dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);
        dataset.addValue(5.0, series1, category5);

        dataset.addValue(5.0, series2, category1);
        dataset.addValue(7.0, series2, category2);
        dataset.addValue(6.0, series2, category3);
        dataset.addValue(8.0, series2, category4);
        dataset.addValue(4.0, series2, category5);

        dataset.addValue(4.0, series3, category1);
        dataset.addValue(3.0, series3, category2);
        dataset.addValue(2.0, series3, category3);
        dataset.addValue(3.0, series3, category4);
        dataset.addValue(6.0, series3, category5);
	}

	private void initGUI() {
		
		chart = ChartFactory.createBarChart(
	            "Histogram",         // chart title
	            "Color",               // domain axis label
	            "Pixel count",                  // range axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );
		
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
		frame.setBounds((screenSize.width/2-450), screenSize.height/2-300, 900, 600);
		frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.setVisible(true);
	}

}
