package cz.vutbr.fit.dashapp.eval.metric.raster;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;

/**
 * Set of metrics which analyze dashboard raster represented as matrix of RGB pixels (integers).
 * 
 * @author Jiri Hynek
 *
 */
public interface IRasterMetric extends IMetric {
	
	MetricResult[] measure(int[][] matrix);

}
