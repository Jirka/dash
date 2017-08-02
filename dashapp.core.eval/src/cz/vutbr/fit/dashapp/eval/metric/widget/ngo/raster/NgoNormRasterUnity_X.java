package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.MathUtils;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class NgoNormRasterUnity_X extends AbstractWidgetRasterMetric {

	public NgoNormRasterUnity_X(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		//int areas = dashboard.getElementsArea(types);
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			double areas = ((double) dashboard.getElementsArea(types, true));
			boolean[][] geMatrix = BooleanMatrix.printDashboard(dashboard, true, GEType.ALL_TYPES);
			double ratio = MathUtils.adaptNormalized(ratioCalculator.getRatio(matrix, geMatrix), 0.75, 1.0);
			areas = areas*ratio;
			
			double UM_form = 1 - (((double)(dashboard.getNumberOfSizes(types)-1))/dashboard.n(types));
			//double UM_form = 1 - (((double)(dashboard.getNumberOfVisibleSizes(types)-1))/ges.size());
			double UM_space = 0;
			
			double emptyArea = dashboard.area()-areas;
			if(emptyArea < 0) {
				emptyArea = 0.0;
			}
			if(emptyArea != 0) {
				double div = ((double) (dashboard.getLayoutArea(types)-areas))/(emptyArea);
				if(div > 1.0) {
					div = 1.0;
				}
				if(div < 0) {
					div = 0;
				}
				UM_space = 1 - div;
			}
			
			return new MetricResult[] {
					new MetricResult("Unity", "UM", (Math.abs(UM_form)+Math.abs(UM_space))/2)	
			};
		}
		
		return EMPTY_RESULT;
	}

}
