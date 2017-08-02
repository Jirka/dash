package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class NgoRasterBalance extends AbstractWidgetRasterMetric {

	public NgoRasterBalance(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public double getBalance(Dashboard dashboard, GEType[] types, ColorSpace[][] image, int dimension) {
		// vertical center of dashboard
		double center = dashboard.halfSize(dimension);
		// initialize weights
		double weight1 = 0.0;
		double weight2 = 0.0;
		// count weights
		List<GraphicalElement> ges = dashboard.getChildren(types);
		for (GraphicalElement ge : ges) {
			double ratio = ratioCalculator.getRatio(image, ge, null);
			//if(ratio == Double.NaN) {
				ratio = ratioCalculator.getRatio(image, ge, null);
			//}
			double distanceCenter = ge.center(dimension) - center;
			if(distanceCenter < 0) {
				// left side
				weight1 += ge.area()*(-distanceCenter)*ratio;
			} else if(distanceCenter > 0) {
				// ride side
				weight2 += ge.area()*(distanceCenter)*ratio;
			}
		}
		// return balance for particular dimension
		double b = Math.max(Math.abs(weight1), Math.abs(weight2));
		if(b == 0) {
			// there are no objects to compare -> balanced
			return 0.0;
		}
		return (weight1-weight2)/b;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		IDashboardFile dashboardFile = dashboard.getDashboardFile();
		if(dashboardFile != null) {
			ColorSpace[][] imageMatrix = ratioCalculator.prepareImage(dashboardFile);
			double BM_V = getBalance(dashboard, types, imageMatrix, Constants.X);
			//double BM_V2 = getBalance2(dashboard, types, image, Constants.X);
			double BM_H = getBalance(dashboard, types, imageMatrix, Constants.Y);
			//double BM_H2 = getBalance2(dashboard, types, image, Constants.X);
			return new MetricResult[] {
					new MetricResult("Balance", "BM", 1-(Math.abs(BM_V)+Math.abs(BM_H))/2.0),
					new MetricResult("Vertical Balance", "BM_v", BM_V),
					new MetricResult("Horizontal Balance", "BM_h", BM_H)
			};
		}
		return EMPTY_RESULT;
	}

}
