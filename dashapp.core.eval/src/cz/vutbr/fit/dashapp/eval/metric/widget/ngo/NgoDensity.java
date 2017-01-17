package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoDensity extends AbstractWidgetMetric {

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		return new MetricResult[] {
				new MetricResult("Density", "DM", 1.0-2*Math.abs(0.5-(((double) dashboard.getElementsArea(types))/dashboard.area())))
		};
	}

}
