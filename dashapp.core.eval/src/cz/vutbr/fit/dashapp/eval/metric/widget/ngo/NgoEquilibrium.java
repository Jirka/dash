package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoEquilibrium extends AbstractWidgetMetric {
	
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
		
		for (GraphicalElement graphicalElement : dashboard.getChildren(types)) {
			int area = graphicalElement.area();
			EM_x += area*(graphicalElement.dx(centerX));
			EM_y += area*(graphicalElement.dy(centerY));
			areas += area;
		}
		
		//double elemCount = dashboard.n(types);
		EM_x = (2*EM_x)/(/*elemCount**/dashboard.width*areas);
		EM_y = (2*EM_y)/(/*elemCount**/dashboard.height*areas);
		// it makes no sense to apply elemCount in equation (mistake in the paper?)
		
		return new MetricResult[] { 
				new MetricResult("Equilibrium", "EM", 1-(Math.abs(EM_x)+Math.abs(EM_y))/2.0),
				new MetricResult("Equilibrium (X)", "EM_x", EM_x),
				new MetricResult("Equilibrium (Y)", "EM_y", EM_y)
			};
	}

}
