package cz.vutbr.fit.dashapp.eval.metric;

import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class Area extends AbstractMetric implements IMetric {
	
	public Area(Dashboard dashboard, GEType[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "Area";
	}

	@Override
	public Object measure() {
		// calculate width and height of layout
		int area = 0;
		List<GraphicalElement> elements = dashboard.getChildren(types);
		for (GraphicalElement ge : elements) {
			area += ge.area();
		}
		
		;
		
		return ((double) area)/dashboard.area();
	}

}
