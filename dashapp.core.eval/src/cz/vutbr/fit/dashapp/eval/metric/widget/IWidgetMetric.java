package cz.vutbr.fit.dashapp.eval.metric.widget;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public interface IWidgetMetric extends IMetric {
	
	public static final MetricResult[] EMPTY_RESULT = new MetricResult[0];

	public MetricResult[] measure(Dashboard dashboard, GEType[] types);

}
