package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoCohesion extends AbstractWidgetMetric {

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		// calculate width and height of layout
		double ratioLayout = (((double) dashboard.getLayoutHeight(types))/((double) dashboard.getLayoutWidth(types)));
		double CM_fl = ratioLayout/(((double) dashboard.height)/((double) dashboard.width));
		if(CM_fl > 1.0) {
			CM_fl = 1/CM_fl;
		}
		
		double CM_lo = 0.0, ti;
		for (GraphicalElement graphicalElement : dashboard.getChildren(types)) { 
			ti = (((double) graphicalElement.height)/((double) graphicalElement.width)) / ratioLayout;
			if(ti > 1.0) {
				ti = 1/ti;
			}
			CM_lo+=ti;
		}
		CM_lo = CM_lo/dashboard.n(types);
		
		return new MetricResult[] {
				new MetricResult("Cohesion", "CM", (Math.abs(CM_fl)+Math.abs(CM_lo))/2.0)
		};
	}

}
