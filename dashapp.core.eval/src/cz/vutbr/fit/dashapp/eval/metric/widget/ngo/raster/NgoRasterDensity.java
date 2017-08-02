package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class NgoRasterDensity extends AbstractWidgetRasterMetric {

	public NgoRasterDensity(RasterRatioCalculator ratioCalculator) {
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
			for (GraphicalElement ge : ges) {
				areas += ge.area()*ratioCalculator.getRatio(matrix, ge, null);
			}
			
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
