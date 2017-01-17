package cz.vutbr.fit.dashapp.eval.metric.raster.color;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.MatrixUtils;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel;
import cz.vutbr.fit.dashapp.util.MatrixUtils.ColorChannel.ColorChannelType;

public class Colorfulness implements IMetric {
	
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

	public MetricResult[] measure(ColorChannel[][] matrix, ColorChannelType type) {
		double mean = MatrixUtils.getColorChannelMean(matrix, type);
		double stdDev = MatrixUtils.getColorChannelStdDev(matrix, mean, type);
		return new MetricResult[] {
				new MetricResult("Colorfulness (m+s)", "CLR", mean+stdDev),
				new MetricResult("Colorfulness m", "CLR_m", mean),
				new MetricResult("Colorfulness s", "CLR_s", stdDev)
		};
	}
}
