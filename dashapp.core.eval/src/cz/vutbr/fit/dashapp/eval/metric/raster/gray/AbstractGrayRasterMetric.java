package cz.vutbr.fit.dashapp.eval.metric.raster.gray;

import cz.vutbr.fit.dashapp.eval.metric.AbstractMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.IRasterMetric;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

public abstract class AbstractGrayRasterMetric extends AbstractMetric implements IRasterMetric {

	@Override
	public MetricResult[] measure(DashboardFile dashboardFile) {
		return measure(ColorMatrix.printImageToMatrix(dashboardFile.getImage(), dashboardFile.getDashboard(true)));
	}

	@Override
	public MetricResult[] measure(DashboardFile dashboardFile, boolean forceReload, GEType[] types) {
		return measure(dashboardFile);
	}
	
	@Override
	public MetricResult[] measure(int matrix[][]) {
		int matrixGrayValue[][] = ColorMatrix.toGrayScale(matrix, true, true);
		return measureGrayMatrix(matrixGrayValue);
	}

	public abstract MetricResult[] measureGrayMatrix(int matrix[][]);

}
