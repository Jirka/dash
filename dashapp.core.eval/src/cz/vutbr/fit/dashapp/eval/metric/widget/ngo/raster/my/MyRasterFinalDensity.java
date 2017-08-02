package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class MyRasterFinalDensity extends AbstractWidgetRasterMetric {

	public MyRasterFinalDensity(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			List<GraphicalElement> ges = dashboard.getChildren(types);
			//List<GraphicalElement> ges = dashboard.getVisibleChildren(types);
			double areas = 0.0;
			double maxRatio = ratioCalculator.getMaxRatio();
			if(maxRatio == Double.POSITIVE_INFINITY) {
				maxRatio = Double.NEGATIVE_INFINITY;
			}
			double actRatio;
			for (GraphicalElement ge : ges) {
				actRatio=ratioCalculator.getRatio(matrix, ge, null);
				if(actRatio > maxRatio) {
					maxRatio = actRatio;
				}
				areas += ge.area()*actRatio;
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
