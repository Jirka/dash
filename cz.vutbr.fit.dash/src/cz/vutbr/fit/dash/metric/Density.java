package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class Density extends AbstractMetric implements IMetric {

	public Density(Dashboard dashboard, Type[] types) {
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
