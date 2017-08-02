package cz.vutbr.fit.dashapp.eval.metric.raster.gray.histogram;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.AbstractGrayRasterMetric;
import cz.vutbr.fit.dashapp.image.util.HistogramUtils;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

public abstract class AbstractGrayHistogramMetric extends AbstractGrayRasterMetric {

	@Override
	public MetricResult[] measure(DashboardFile dashboardFile) {
		return measure(ColorMatrix.printImageToMatrix(dashboardFile.getImage(), dashboardFile.getDashboard(true)));
	}

	@Override
	public MetricResult[] measure(DashboardFile dashboardFile, boolean forceReload, GEType[] types) {
		return measure(dashboardFile);
	}
	
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
