package cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.IRasterMetric;

public interface IHistogramMetric extends IRasterMetric {
	
	MetricResult[] measure(int[][] matrix);

}
