package cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.AbstractGrayRasterMetric;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;

/**
 * Abstract implementation of metrics which works with histogram of raw gray-scale values.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractGrayHistogramMetric extends AbstractGrayRasterMetric implements IGrayHistogramRasterMetric {
	
	@Override
	public MetricResult[] measureGrayMatrix(int matrix[][]) {
		return measureGrayHistogram(HistogramUtils.getGrayscaleHistogram(matrix));
	}
	
	public abstract MetricResult[] measureGrayHistogram(int histogram[]);
	
	protected int getArea(int[] histogram) {
		int area = 0;
		for (int i : histogram) {
			area += i;
		}
		return area;
	}
}
