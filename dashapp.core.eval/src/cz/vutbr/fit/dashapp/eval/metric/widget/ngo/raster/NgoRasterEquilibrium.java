package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

public class NgoRasterEquilibrium extends AbstractWidgetRasterMetric {
	
	public NgoRasterEquilibrium(RasterRatioCalculator ratioCalculator) {
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
		double centerX = dashboard.width/2.0;
		double centerY = dashboard.height/2.0;
		
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
			
			int area;
			double ratio;
			for (GraphicalElement graphicalElement : dashboard.getChildren(types)) {
				area = graphicalElement.area();
				ratio = ratioCalculator.getRatio(matrix, graphicalElement, null);
				if(findMaxRatio && maxRatio < ratio) {
					maxRatio = ratio;
				}
				EM_x += area*(graphicalElement.dx(centerX))*ratio;
				EM_y += area*(graphicalElement.dy(centerY))*ratio;
				areas += area;
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

}
