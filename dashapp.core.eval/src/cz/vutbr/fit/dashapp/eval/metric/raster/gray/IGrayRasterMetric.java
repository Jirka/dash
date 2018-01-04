package cz.vutbr.fit.dashapp.eval.metric.raster.gray;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.IRasterMetric;

/**
 * Set of metrics which works with raw gray-scale values (0-255).
 * 
 * @author Jiri Hynek
 *
 */
public interface IGrayRasterMetric extends IRasterMetric {
	
	public MetricResult[] measureGrayMatrix(int matrix[][]);

}
