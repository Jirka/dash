package cz.vutbr.fit.dashapp.eval.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.IWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils.MeanStatistics;

/**
 * Tool for multiple measurements. 
 * 
 * @author Jiri Hynek
 *
 */
public class MetricCalculator {
	
	public static Map<IMetric, MetricResultsCollection> measure(DashboardCollection dc, IMetric[] metrics, GEType[] types) {
		Map<IMetric, MetricResultsCollection> results = new LinkedHashMap<>();
		for (IMetric metric : metrics) {
			results.put(metric, measure(dc, metric, types));
		}
		return results;
	}

	public static MetricResultsCollection measure(DashboardCollection dc, IMetric metric, GEType[] types) {
		Dashboard[] dashboards = dc.dashboards;
		int length = dashboards.length;
		MetricResultsCollection resultsCollection = new MetricResultsCollection(length);
		for (int i = 0; i < length; i++) {
			if(metric instanceof IWidgetMetric) {
				// optimization
				resultsCollection.results[i] = ((IWidgetMetric) metric).measure(dashboards[i]);
			} else {
				resultsCollection.results[i] = metric.measure(dashboards[i].getDashboardFile());
			}
			//System.out.println(resultsCollection.results[i][0].value.toString());
		}
		//System.out.println("------");
		return resultsCollection;
	}

	public static Map<IMetric, MeanStatistics[]> statistics(Map<IMetric, MetricResultsCollection> resultsCollection, int filterExtremeItems) {
		Map<IMetric, MeanStatistics[]> meanResults = new LinkedHashMap<>();
		for (Entry<IMetric, MetricResultsCollection> resultsEntry : resultsCollection.entrySet()) {
			meanResults.put(resultsEntry.getKey(), resultsEntry.getValue().meanStatistics(filterExtremeItems));
		}
		return meanResults;
	}
}
