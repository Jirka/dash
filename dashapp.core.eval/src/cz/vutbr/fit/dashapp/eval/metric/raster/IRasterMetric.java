package cz.vutbr.fit.dashapp.eval.metric.raster;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;

public interface IRasterMetric extends IMetric {
	
	MetricResult[] measure(int[][] matrix);

}
