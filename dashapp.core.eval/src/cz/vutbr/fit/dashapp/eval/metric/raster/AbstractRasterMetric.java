package cz.vutbr.fit.dashapp.eval.metric.raster;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

public abstract class AbstractRasterMetric implements IRasterMetric {

	@Override
	public MetricResult[] measure(DashboardFile dashboardFile) {
		return measure(ColorMatrix.printImageToMatrix(dashboardFile.getImage(), dashboardFile.getDashboard(true)));
	}

	@Override
	public MetricResult[] measure(DashboardFile dashboardFile, boolean forceReload, GEType[] types) {
		return measure(dashboardFile);
	}
	
	@Override
	public abstract MetricResult[] measure(int matrix[][]);

}
