package cz.vutbr.fit.dashapp.eval.metric.widget.basic;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class WidgetCount extends AbstractWidgetMetric {
	
	public WidgetCount() {
		super();
	}
	
	public WidgetCount(GEType[] geTypes) {
		super(geTypes);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		
		return new MetricResult[] {
				new MetricResult("Widget Count", "n", dashboard.n(getGeTypes()))
		};
	}

}
