package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public abstract class AbstractMetric implements IMetric {

	protected Dashboard dashboard;
	protected String name;
	protected Type[] types;

	public AbstractMetric(Dashboard dashboard, Type[] types, String name) {
		this.dashboard = dashboard;
		this.types = types;
		this.name = name;
	}
	
	public AbstractMetric(Dashboard dashboard, Type[] types) {
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
	
	public Type[] getTypes() {
		return types;
	}
}
