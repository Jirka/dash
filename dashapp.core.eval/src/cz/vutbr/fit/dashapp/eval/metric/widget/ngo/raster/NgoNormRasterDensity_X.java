package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.MathUtils;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class NgoNormRasterDensity_X extends AbstractWidgetRasterMetric {

	public NgoNormRasterDensity_X(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			double areas = ((double) dashboard.getElementsArea(types, true));
			boolean[][] geMatrix = BooleanMatrix.printDashboard(dashboard, true, GEType.ALL_TYPES);
			double ratio = MathUtils.adaptNormalized(ratioCalculator.getRatio(matrix, geMatrix), 0.75, 1.0);
			areas = areas*ratio;
			
			double div = ((areas)/dashboard.area());
			if(div > 1.0) {
				div = 1.0;
			}
			
			return new MetricResult[] {
					new MetricResult("Density", "DM", 1.0-2*Math.abs(0.5-div))
			};
		}
		
		return EMPTY_RESULT;
	}

}
