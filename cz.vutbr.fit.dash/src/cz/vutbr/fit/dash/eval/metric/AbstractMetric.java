package cz.vutbr.fit.dash.eval.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;

public abstract class AbstractMetric implements IMetric {

	protected Dashboard dashboard;
	protected String name;
	protected GEType[] types;

	public AbstractMetric(Dashboard dashboard, GEType[] types, String name) {
		this.dashboard = dashboard;
		this.types = types;
		this.name = name;
	}
	
	public AbstractMetric(Dashboard dashboard, GEType[] types) {
		this.dashboard = dashboard;
		this.types = types;
	}

	@Override
	public String getName() {
		return name != null ? name : this.getClass().getSimpleName();
	}
	
	@Override
	public String[] getSubNames() {
		return null;
	}
	
	public GEType[] getTypes() {
		return types;
	}
}
