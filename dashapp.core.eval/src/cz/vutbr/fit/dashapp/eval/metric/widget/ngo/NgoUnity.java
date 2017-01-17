package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoUnity extends AbstractWidgetMetric {

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		int areas = dashboard.getElementsArea(types);
		double UM_form = 1 - (((double)(dashboard.getNumberOfSizes(types)-1))/dashboard.n(types));
		double UM_space = 1 - ((double) (dashboard.getLayoutArea(types)-areas))/(dashboard.area()-areas);
		
		return new MetricResult[] {
				new MetricResult("Unity", "UM", (Math.abs(UM_form)+Math.abs(UM_space))/2)	
		};
	}

}
