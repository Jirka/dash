package cz.vutbr.fit.dashapp.eval.metric.widget.raster;

import cz.vutbr.fit.dashapp.eval.metric.widget.IWidgetMetric;

/**
 * Set of metrics that works with widgets of dashboard and their color ratio. 
 * 
 * Ratio calculator calculates the color ratio of widgets. 
 * 
 * @author Jiri Hynek
 *
 */
public interface IWidgetRasterMetric extends IWidgetMetric {
	
	IWidgetRasterMetric setRatioCalculator(RasterRatioCalculator ratioCalculator);
	
	RasterRatioCalculator getRatioCalculator();

}
