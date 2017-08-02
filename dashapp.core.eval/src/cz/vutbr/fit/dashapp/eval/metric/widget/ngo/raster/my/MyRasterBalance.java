package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.model.Constants.Side;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class MyRasterBalance extends AbstractWidgetRasterMetric {

	public MyRasterBalance(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public double getBalance(Dashboard dashboard, GEType[] types, ColorSpace[][] image, int dimension) {
		Side s1, s2;
		if(dimension == Constants.X) {
			s1 = Side.LEFT;
			s2 = Side.RIGHT;
		} else {
			s1 = Side.UP;
			s2 = Side.DOWN;
		}
		
		// initialize weights
		double weight1 = 0.0;
		double weight2 = 0.0;
		// count weights
		List<GraphicalElement> ges = dashboard.getChildren(types);
		double a;
		for (GraphicalElement ge : ges) {
			// one side
			a = ge.area(s1);
			if(a > 0) {
				weight1 += a*ge.depth(s1)*ratioCalculator.getRatio(image, ge, s1);
			}
			// second side
			a = ge.area(s2);
			if(a > 0) {
				weight2 += a*ge.depth(s2)*ratioCalculator.getRatio(image, ge, s2);
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
