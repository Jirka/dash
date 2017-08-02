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

public class MyRasterDensity extends AbstractWidgetRasterMetric {

	public MyRasterDensity(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			List<GraphicalElement> ges = dashboard.getChildren(types);
			double areas = 0.0;
			for (GraphicalElement ge : ges) {
				areas += ge.area()*ratioCalculator.getRatio(matrix, ge, null);
			}
			double div = Math.abs((areas)/(dashboard.area()*ratioCalculator.getRatio(matrix, dashboard, null)));
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
