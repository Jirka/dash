package cz.vutbr.fit.dashapp.eval.metric.raster;

import cz.vutbr.fit.dashapp.eval.metric.AbstractMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

/**
 * Abstract implementation of metric which analyze dashboard raster represented as matrix of RGB pixels (integers).
 * 
 * It provides method which creates matrix of pixels.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractRasterMetric extends AbstractMetric implements IRasterMetric {

	@Override
	public MetricResult[] measure(IDashboardFile dashboardFile) {
		return measure(ColorMatrix.printImageToMatrix(dashboardFile.getImage(), dashboardFile.getDashboard(true)));
	}
	
	@Override
	public abstract MetricResult[] measure(int matrix[][]);

}
