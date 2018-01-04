package cz.vutbr.fit.dashapp.eval.metric.widget;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * Set of metrics that works with widgets of dashboard.
 * 
 * @author Jiri Hynek
 *
 */
public interface IWidgetMetric extends IMetric {
	
	IWidgetMetric setGETypes(GEType[] geTypes);
	
	GEType[] getGeTypes();
	
	MetricResult[] measure(IDashboardFile dashboardFile, boolean forceReload);

	MetricResult[] measure(Dashboard dashboard);

}
