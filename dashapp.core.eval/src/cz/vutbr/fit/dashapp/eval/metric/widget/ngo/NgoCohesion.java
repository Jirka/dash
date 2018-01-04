package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoCohesion extends AbstractWidgetMetric {
	
	public NgoCohesion() {
		super();
	}
	
	public NgoCohesion(GEType[] geTypes) {
		super(geTypes);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		// calculate width and height of layout
		double ratioLayout = (((double) dashboard.getLayoutHeight(getGeTypes()))/((double) dashboard.getLayoutWidth(getGeTypes())));
		double CM_fl = ratioLayout/(((double) dashboard.height)/((double) dashboard.width));
		if(CM_fl > 1.0) {
			CM_fl = 1/CM_fl;
		}
		
		double CM_lo = 0.0, ti;
		for (GraphicalElement graphicalElement : dashboard.getChildren(getGeTypes())) { 
			ti = (((double) graphicalElement.height)/((double) graphicalElement.width)) / ratioLayout;
			if(ti > 1.0) {
				ti = 1/ti;
			}
			CM_lo+=ti;
		}
		CM_lo = CM_lo/dashboard.n(getGeTypes());
		
		return new MetricResult[] {
				new MetricResult("Cohesion", "CM", (Math.abs(CM_fl)+Math.abs(CM_lo))/2.0)
		};
	}

}
