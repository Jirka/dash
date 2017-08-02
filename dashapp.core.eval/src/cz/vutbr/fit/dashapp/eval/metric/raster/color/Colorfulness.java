package cz.vutbr.fit.dashapp.eval.metric.raster.color;

import cz.vutbr.fit.dashapp.eval.metric.AbstractMetric;
import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.image.colorspace.ColorChannelUtils;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class Colorfulness extends AbstractMetric implements IMetric {
	
	@Override
	public MetricResult[] measure(DashboardFile dashboardFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetricResult[] measure(DashboardFile dashboardFile, boolean forceReload, GEType[] types) {
		// TODO Auto-generated method stub
		return null;
	}

	public MetricResult[] measure(ColorSpace[][] matrix, int colorChannel) {
		double mean = ColorChannelUtils.getColorChannelMean(matrix, colorChannel);
		double stdDev = ColorChannelUtils.getColorChannelStdDev(matrix, mean, colorChannel);
		return new MetricResult[] {
				new MetricResult("Colorfulness (m+s)", "CLR", mean+stdDev),
				new MetricResult("Colorfulness m", "CLR_m", mean),
				new MetricResult("Colorfulness s", "CLR_s", stdDev)
		};
	}
}
