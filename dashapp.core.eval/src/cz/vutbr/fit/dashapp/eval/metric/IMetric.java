package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public interface IMetric {

	MetricResult[] measure(DashboardFile dashboardFile);

	MetricResult[] measure(DashboardFile dashboardFile, boolean forceReload, GEType[] types);

}
