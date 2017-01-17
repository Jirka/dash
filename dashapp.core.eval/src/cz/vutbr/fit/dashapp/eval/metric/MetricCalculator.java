package cz.vutbr.fit.dashapp.eval.metric;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.eval.metric.widget.IWidgetMetric;
import cz.vutbr.fit.dashapp.image.MathUtils.MeanSatistics;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.DashboardCollection;

public class MetricCalculator {
	
	public Map<IWidgetMetric, MetricResultsCollection> measure(DashboardCollection dc, IWidgetMetric[] widgetMetrics, GEType[] types) {
		Map<IWidgetMetric, MetricResultsCollection> results = new LinkedHashMap<>();
		for (IWidgetMetric widgetMetric : widgetMetrics) {
			results.put(widgetMetric, measure(dc, widgetMetric, types));
		}
		return results;
	}

	public MetricResultsCollection measure(DashboardCollection dc, IWidgetMetric widgetMetric, GEType[] types) {
		Dashboard[] dashboards = dc.dashboards;
		int length = dashboards.length;
		MetricResultsCollection resultsCollection = new MetricResultsCollection(length);
		for (int i = 0; i < length; i++) {
			resultsCollection.results[i] = widgetMetric.measure(dashboards[i], types);
		}
		return resultsCollection;
	}

	public Map<IWidgetMetric, MeanSatistics[]> statistics(Map<IWidgetMetric, MetricResultsCollection> resultsCollection) {
		Map<IWidgetMetric, MeanSatistics[]> meanResults = new LinkedHashMap<>();
		for (Entry<IWidgetMetric, MetricResultsCollection> resultsEntry : resultsCollection.entrySet()) {
			meanResults.put(resultsEntry.getKey(), resultsEntry.getValue().meanStatistics());
		}
		return meanResults;
	}
}
