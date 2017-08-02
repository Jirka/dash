package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class MyRasterFinalDensity_X extends AbstractWidgetRasterMetric {

	public MyRasterFinalDensity_X(RasterRatioCalculator ratioCalculator) {
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
			
			double maxRatio = ratioCalculator.getMaxRatio();
			// problem with CIE Lch colorfulness 
			if(maxRatio == Double.POSITIVE_INFINITY) {
				maxRatio = Double.NEGATIVE_INFINITY;
				double actRatio;
				List<GraphicalElement> ges = dashboard.getChildren(types);
				for (GraphicalElement ge : ges) {
					actRatio=ratioCalculator.getRatio(matrix, ge, null);
					if(actRatio > maxRatio) {
						maxRatio = actRatio;
					}
				}
			}
			
			double div = ((areas)/(dashboard.area()*maxRatio));
			if(div > 1.0) {
				div = 1.0;
			}
			
			return new MetricResult[] {
					new MetricResult("Density", "DM", div)
			};
		}
		
		return EMPTY_RESULT;
	}

}
