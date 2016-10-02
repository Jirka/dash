package cz.fit.vutbr.view.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.util.List;
import java.util.Locale.Category;
import java.awt.Paint;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import cz.vutbr.fit.dashapp.view.IComponent;

public class Histogram implements IComponent {
	
	private JFrame frame;
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private DefaultCategoryDataset dataset;
	
	private int[] histogram;
	
	public Histogram(int[] histogram) {
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
	            "Histogram",         // chart title
	            "Color",               // domain axis label
	            "Pixel count",                  // range axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            false,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
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
		/*plot.domainGridlinePaint = Color.white
		plot.rangeGridlinePaint = Color.white
		plot.outlineVisible = false*/
		BarRenderer.setDefaultShadowsVisible(false);
		 renderer.setShadowXOffset(0);
         renderer.setShadowYOffset(0);
	    plot.setRenderer(renderer);
	    //CategoryAxis axis = new CategoryAxis();
	    //axis.setTickLabelsVisible(false);
	     
	   //CategoryAxis axis = plot.getDomainAxis();
	   //axis.setUpperMargin(0.0);
	    
	    
	   
	   //plot.setDomainAxis(axis);
	   
		/*((BarRenderer)chart.getRenderer()).setBarPainter(new StandardBarPainter());

	    BarRenderer r = (BarRenderer)chart.getCategoryPlot().getRenderer();
	    r.setSeriesPaint(0, Color.blue);*/
		
		chartPanel = new ChartPanel(chart);
	}
	
	/**
     * A custom renderer that returns a different color for each item in a single series.
     */
    class CustomRenderer extends BarRenderer {

        /** The colors. */
        private Paint[] colors;

        /**
         * Creates a new renderer.
         *
         * @param colors  the colors.
         */
        public CustomRenderer(final Paint[] colors) {
            this.colors = colors;
        }

        /**
         * Returns the paint for an item.  Overrides the default behaviour inherited from
         * AbstractSeriesRenderer.
         *
         * @param row  the series.
         * @param column  the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint(final int row, final int column) {
            return this.colors[column % this.colors.length];
        }
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
