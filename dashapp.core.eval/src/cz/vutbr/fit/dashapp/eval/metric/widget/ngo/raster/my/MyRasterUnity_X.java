package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class MyRasterUnity_X extends AbstractWidgetRasterMetric {

	public MyRasterUnity_X(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			double areas = ((double) dashboard.getElementsArea(types, true));
			boolean[][] geMatrix = BooleanMatrix.printDashboard(dashboard, true, GEType.ALL_TYPES);
			double ratio = ratioCalculator.getRatio(matrix, geMatrix);
			areas = areas*ratio;
			
			double UM_form = 1 - (((double)(dashboard.getNumberOfSizes(types)-1))/dashboard.n(types));
			double UM_space = 0;
			
			double emptyArea = dashboard.area()*ratioCalculator.getRatio(matrix, dashboard, null)-areas;
			if(emptyArea < 0) {
				emptyArea = 0;
			}
			
			if(emptyArea != 0) {
				double div = (
						(double) (dashboard.getLayoutArea(types)
						*ratioCalculator.getRatio(matrix, dashboard.getLayoutRectangle(GEType.ALL_TYPES))
						-areas)
						)
						/(emptyArea);
				
				if(div > 1) {
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
