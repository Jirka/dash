package cz.vutbr.fit.dashapp.eval.metric.raster.gray;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.AbstractRasterMetric;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

/**
 * Abstract implementation of metrics which works with raw gray-scale values (0-255).
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractGrayRasterMetric extends AbstractRasterMetric implements IGrayRasterMetric {

	@Override
	public MetricResult[] measure(IDashboardFile dashboardFile) {
		return measure(ColorMatrix.printImageToMatrix(dashboardFile.getImage(), dashboardFile.getDashboard(true)));
	}
	
	@Override
	public MetricResult[] measure(int matrix[][]) {
		// convert to raw gray-scale values
		int matrixGrayValue[][] = ColorMatrix.toGrayScale(matrix, true, true);
		return measureGrayMatrix(matrixGrayValue);
	}

	public abstract MetricResult[] measureGrayMatrix(int matrix[][]);

}
