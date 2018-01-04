package cz.vutbr.fit.dashapp.eval.metric.widget;

import cz.vutbr.fit.dashapp.eval.metric.AbstractMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

/**
 * Abstract implementation of metric that works with widget of metrics.
 * 
 * Works with GE types.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractWidgetMetric extends AbstractMetric implements IWidgetMetric {
	
	protected GEType[] geTypes;
	
	public AbstractWidgetMetric() {
		this(GEType.ALL_TYPES);
	}
	
	public AbstractWidgetMetric(GEType[] geTypes) {
		super();
		setGETypes(geTypes);
	}
	
	@Override
	public IWidgetMetric setGETypes(GEType[] geTypes) {
		this.geTypes = geTypes;
		return this;
	}
	
	@Override
	public GEType[] getGeTypes() {
		return geTypes;
	}
	
	@Override
	public MetricResult[] measure(IDashboardFile dashboardFile) {
		return measure(dashboardFile, true);
	}
	
	@Override
	public MetricResult[] measure(IDashboardFile dashboardFile, boolean forceReload) {
		return measure(dashboardFile.getDashboard(forceReload));
	}

	@Override
	public abstract MetricResult[] measure(Dashboard dashboard);

}
