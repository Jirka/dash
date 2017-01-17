package cz.vutbr.fit.dashapp.eval.metric.widget;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public abstract class AbstractWidgetMetric implements IWidgetMetric {
	
	@Override
	public MetricResult[] measure(DashboardFile dashboardFile) {
		return measure(dashboardFile.getDashboard(true), GEType.ALL_TYPES);
	}
	
	@Override
	public MetricResult[] measure(DashboardFile dashboardFile, boolean forceReload, GEType[] types) {
		return measure(dashboardFile.getDashboard(forceReload), types);
	}

	@Override
	public abstract MetricResult[] measure(Dashboard dashboard, GEType[] types);

}
