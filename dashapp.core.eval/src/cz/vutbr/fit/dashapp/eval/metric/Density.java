package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class Density extends AbstractMetric implements IMetric {

	public Density(Dashboard dashboard, GEType[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "DM";
	}

	@Override
	public Object measure() {
		return 1.0-2*Math.abs(0.5-(((double) dashboard.getElementsArea(getTypes()))/dashboard.area()));
	}

}
