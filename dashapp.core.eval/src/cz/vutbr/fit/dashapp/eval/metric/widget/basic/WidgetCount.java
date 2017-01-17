package cz.vutbr.fit.dashapp.eval.metric.widget.basic;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class WidgetCount extends AbstractWidgetMetric {

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		
		return new MetricResult[] {
				new MetricResult("Widget Count", "n", dashboard.n(types))
		};
	}

}
