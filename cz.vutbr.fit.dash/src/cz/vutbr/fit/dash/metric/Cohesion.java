package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;

public class Cohesion extends AbstractMetric implements IMetric {
	
	public Cohesion(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getInicials() {
		return "CM";
	}

	@Override
	public Object measure() {
		// calculate width and height of layout
		double ratioLayout = (((double) dashboard.getLayoutHeight())/((double) dashboard.getLayoutWidth()));
		double CM_fl = ratioLayout/(((double) dashboard.height)/((double) dashboard.width));
		if(CM_fl > 1.0) {
			CM_fl = 1/CM_fl;
		}
		
		double CM_lo = 0.0, ti;
		for (GraphicalElement graphicalElement : dashboard.getGraphicalElements()) { 
			ti = (((double) graphicalElement.height)/((double) graphicalElement.width)) / ratioLayout;
			if(ti > 1.0) {
				ti = 1/ti;
			}
			CM_lo+=ti;
		}
		CM_lo = CM_lo/dashboard.n();
		
		return (Math.abs(CM_fl)+Math.abs(CM_lo))/2.0;
	}

}
