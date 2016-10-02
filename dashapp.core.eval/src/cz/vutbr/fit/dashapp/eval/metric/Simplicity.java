package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class Simplicity extends AbstractMetric implements IMetric {

	public Simplicity(Dashboard dashboard, GEType[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "SMM";
	}

	@Override
	public Object measure() {
		return 3.0/(dashboard.getHAP(getTypes())+dashboard.getVAP(getTypes())+dashboard.n(getTypes()));
	}

}
