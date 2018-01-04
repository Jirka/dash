package cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.model.Constants.Side;

/**
 * 
 * @author Jiri Hynek
 * 
 * note: This class was created by combination of:
 * 
 * NgoRasterBalance
 * MyRasterBalance - Works only with area of widget that is located in particular side.
 *
 */
public class NgoRasterBalance extends AbstractWidgetRasterMetric {
	
	public static final int BASIC = 0;
	public static final int AREA_OF_SIDE = 1;
	
	protected int balanceKind = BASIC;

	public NgoRasterBalance() {
		super();
	}
	
	public NgoRasterBalance(GEType[] geTypes) {
		super(geTypes);
	}
	
	public NgoRasterBalance(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public NgoRasterBalance(GEType[] geTypes, RasterRatioCalculator ratioCalculator) {
		super(geTypes, ratioCalculator);
	}
	
	public NgoRasterBalance(GEType[] geTypes, RasterRatioCalculator ratioCalculator, int balanceKind) {
		super(geTypes, ratioCalculator);
		setBalanceKind(balanceKind);
	}
	
	@Override
	public String getName() {
		return super.getName() + (getBalanceKind() == BASIC ? "" : "_side-area");
	}
	
	public NgoRasterBalance setBalanceKind(int balanceKind) {
		this.balanceKind = balanceKind;
		return this;
	}
	
	public int getBalanceKind() {
		return balanceKind;
	}
	
	protected double getBalance(Dashboard dashboard, GEType[] types, RasterRatioCalculator ratioCalculator, ColorSpace[][] image, int dimension) {
		int balanceKind = getBalanceKind();
		if(balanceKind == AREA_OF_SIDE) {
			return getBalanceSideArea(dashboard, types, ratioCalculator, image, dimension);
		}
		
		return getBalanceBasic(dashboard, types, ratioCalculator, image, dimension);
	}
	
	protected double getBalanceBasic(Dashboard dashboard, GEType[] types, RasterRatioCalculator ratioCalculator, ColorSpace[][] image, int dimension) {
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
	
	protected double getBalanceSideArea(Dashboard dashboard, GEType[] types, RasterRatioCalculator ratioCalculator, ColorSpace[][] image, int dimension) {
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
	public MetricResult[] measure(Dashboard dashboard) {
		IDashboardFile dashboardFile = dashboard.getDashboardFile();
		if(dashboardFile != null) {
			RasterRatioCalculator ratioCalculator = getRatioCalculator();
			ColorSpace[][] imageMatrix = ratioCalculator.prepareImage(dashboardFile);
			double BM_V = getBalance(dashboard, getGeTypes(), ratioCalculator, imageMatrix, Constants.X);
			//double BM_V2 = getBalance2(dashboard, types, image, Constants.X);
			double BM_H = getBalance(dashboard, getGeTypes(), ratioCalculator, imageMatrix, Constants.Y);
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
