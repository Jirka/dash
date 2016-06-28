package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class Equilibrium extends AbstractMetric implements IMetric {

	public Equilibrium(Dashboard dashboard, Type[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "EM";
	}
	
	@Override
	public String[] getSubNames() {
		return new String[] { "", "X", "Y" };
	}
	
	public double getEquilibrium(Dashboard dashboard, int dimension) {
		double dashboardCenter = dashboard.ownCenter(dimension);
		double EM = 0.0;
		int areas = 0;
		
		for (GraphicalElement graphicalElement : dashboard.getGraphicalElements(getTypes())) {
			int area = graphicalElement.area();
			EM += area*(graphicalElement.d(dashboardCenter, dimension));
			areas += area;
		}
		
		int elemCount = dashboard.n(getTypes());
		return 2*EM/(elemCount*dashboard.length(dimension)*areas);
	}

	@Override
	public Object measure() {
		// optimal version (equilibrium is calculated for both dimensions)
		double centerX = dashboard.width/2.0;
		double centerY = dashboard.height/2.0;
		
		double EM_x = 0.0;
		double EM_y = 0.0;
		int areas = 0;
		
		for (GraphicalElement graphicalElement : dashboard.getGraphicalElements(getTypes())) {
			int area = graphicalElement.area();
			EM_x += area*(graphicalElement.dx(centerX));
			EM_y += area*(graphicalElement.dy(centerY));
			areas += area;
		}
		
		int elemCount = dashboard.n(getTypes());
		EM_x = 2*EM_x/(elemCount*dashboard.width*areas);
		EM_y = 2*EM_y/(elemCount*dashboard.height*areas);
		
		return new Object[] { 1-(Math.abs(EM_x)+Math.abs(EM_y))/2.0, EM_x, EM_y };
	}

}
