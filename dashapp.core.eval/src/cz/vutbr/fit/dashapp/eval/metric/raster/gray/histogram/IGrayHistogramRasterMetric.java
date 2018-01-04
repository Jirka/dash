package cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.IGrayRasterMetric;

/**
 * Set of metrics which work with histogram of raw gray-scale values.
 * 
 * @author Jiri Hynek
 *
 */
public interface IGrayHistogramRasterMetric extends IGrayRasterMetric {
	
	public MetricResult[] measureGrayHistogram(int histogram[]);

}
