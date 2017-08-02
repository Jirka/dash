package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Constants.Side;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class MyRasterEquilibrium extends AbstractWidgetRasterMetric {
	
	public MyRasterEquilibrium(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public double getEquilibrium(Dashboard dashboard, GEType[] types, int dimension) {
		double dashboardCenter = dashboard.halfSize(dimension);
		double EM = 0.0;
		int areas = 0;
		
		for (GraphicalElement graphicalElement : dashboard.getChildren(types)) {
			int area = graphicalElement.area();
			EM += area*(graphicalElement.d(dashboardCenter, dimension));
			areas += area;
		}
		
		int elemCount = dashboard.n(types);
		return 2*EM/(elemCount*dashboard.size(dimension)*areas);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		// optimal version (equilibrium is calculated for both dimensions)
		//double centerX = dashboard.width/2.0;
		//double centerY = dashboard.height/2.0;
		
		double EM_x = 0.0;
		double EM_y = 0.0;
		double areas = 0;
		
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			
			double maxRatio = ratioCalculator.getMaxRatio();
			boolean findMaxRatio = false;
			if(maxRatio == Double.POSITIVE_INFINITY) {
				maxRatio = Double.MIN_VALUE;
				findMaxRatio = true;
			}
			
			double[] ratios = new double[4];
			for (GraphicalElement ge : dashboard.getChildren(types)) {
				ratios[0] = ratioCalculator.getRatio(matrix, ge, Side.LEFT);
				ratios[1] = ratioCalculator.getRatio(matrix, ge, Side.RIGHT);
				ratios[2] = ratioCalculator.getRatio(matrix, ge, Side.UP);
				ratios[3] = ratioCalculator.getRatio(matrix, ge, Side.DOWN);
				if(findMaxRatio) {
					maxRatio = getMaxRatio(ratios, maxRatio);
				}
				EM_x += ge.area(Side.LEFT)*ge.depth(Side.LEFT)*ratios[0]-ge.area(Side.RIGHT)*ge.depth(Side.RIGHT)*ratios[1];
				EM_y += ge.area(Side.UP)*ge.depth(Side.UP)*ratios[2]-ge.area(Side.DOWN)*ge.depth(Side.DOWN)*ratios[3];
				areas += ge.area();
			}
			
			//double elemCount = dashboard.n(types);
			EM_x = (2*EM_x)/(/*elemCount**/dashboard.width*areas*maxRatio);
			EM_y = (2*EM_y)/(/*elemCount**/dashboard.height*areas*maxRatio);
			// it makes no sense to apply elemCount in equation (mistake in the paper?)
			
			return new MetricResult[] { 
					new MetricResult("Equilibrium", "EM", 1-(Math.abs(EM_x)+Math.abs(EM_y))/2.0),
					new MetricResult("Equilibrium (X)", "EM_x", EM_x),
					new MetricResult("Equilibrium (Y)", "EM_y", EM_y)
				};
		}
		return EMPTY_RESULT;
	}

	private double getMaxRatio(double[] ratios, double maxRatio) {
		for (double ratio : ratios) {
			if(ratio > maxRatio) {
				maxRatio = ratio;
			}
		}
		return maxRatio;
	}

}
