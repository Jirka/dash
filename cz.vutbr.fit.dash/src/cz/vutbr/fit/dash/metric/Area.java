package cz.vutbr.fit.dash.metric;

import java.util.List;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class Area extends AbstractMetric implements IMetric {
	
	public Area(Dashboard dashboard, Type[] types) {
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
		List<GraphicalElement> elements = dashboard.getGraphicalElements(types);
		for (GraphicalElement ge : elements) {
			area += ge.area();
		}
		
		;
		
		return ((double) area)/dashboard.area();
	}

}
