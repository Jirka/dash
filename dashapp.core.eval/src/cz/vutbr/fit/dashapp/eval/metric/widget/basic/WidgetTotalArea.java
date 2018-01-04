package cz.vutbr.fit.dashapp.eval.metric.widget.basic;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * Use sum of areas of all widgets. Some widgets may share screen area.
 * Sum of areas can be higher that area of screen.
 * 
 * @author Jiri Hynek
 *
 */
public class WidgetTotalArea extends AbstractWidgetMetric {
	
	public WidgetTotalArea() {
		super();
	}
	
	public WidgetTotalArea(GEType[] geTypes) {
		super(geTypes);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		// calculate width and height of layout
		int area = 0;
		List<GraphicalElement> elements = dashboard.getChildren(getGeTypes());
		for (GraphicalElement ge : elements) {
			area += ge.area();
		}
		
		return new MetricResult[] {
				new MetricResult("Area", "A", ((double) area)/dashboard.area())
		};
	}

}
