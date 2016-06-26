package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public abstract class AbstractMetric implements IMetric {

	protected Dashboard dashboard;
	protected String name;

	public AbstractMetric(Dashboard dashboard, String name) {
		this.dashboard = dashboard;
		this.name = name;
	}
	
	public AbstractMetric(Dashboard dashboard) {
		this.dashboard = dashboard;
	}
	
	@Override
	public String getName() {
		return name != null ? name : this.getClass().getSimpleName();
	}
	
	@Override
	public String[] getSubNames() {
		return null;
	}
}
