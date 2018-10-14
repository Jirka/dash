package cz.vutbr.fit.dashapp.eval.analysis.file;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractFileAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.IFileAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.model.DashboardFile;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class FileMetricAnalysis extends AbstractFileAnalysis implements IFileAnalysis {
	
	public static final MetricType[] DEFAULT_METRICS = new MetricType[] { 
			MetricType.GrayBalance,
			MetricType.GraySymmetry,
	};
	
	public boolean enable_custom_metrics;
	public List<MetricType> metricTypes = Arrays.asList(DEFAULT_METRICS);

	@Override
	public String getLabel() {
		return "Simple metric analysis";
	}

	@Override
	public String processFile(DashboardFile dashboardFile) {
		StringBuffer output = new StringBuffer();
		final DecimalFormat df = new DecimalFormat("#.#####");
		IMetric metric;
		for (MetricType metricType : metricTypes) {
			metric = metricType.createMetric();
			MetricResult[] result = metric.measure(dashboardFile);
			formatMetric(output, result, df);				
		}
		return output.toString();
	}

}
