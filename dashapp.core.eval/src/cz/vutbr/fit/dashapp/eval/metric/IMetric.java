package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.IDashboardFile;

/**
 * Metrics which measures one or more aspects of dashboard represented by dashboard file.
 * 
 * @author Jiri Hynek
 *
 */
public interface IMetric {
	
	public static final MetricResult[] EMPTY_RESULT = new MetricResult[0];
	
	String getName();

	MetricResult[] measure(IDashboardFile dashboardFile);

}
