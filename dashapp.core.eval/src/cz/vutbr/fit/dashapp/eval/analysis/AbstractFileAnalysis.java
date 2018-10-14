package cz.vutbr.fit.dashapp.eval.analysis;

import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;

/**
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractFileAnalysis extends AbstractAnalysis implements IFileAnalysis {

	protected void formatMetric(StringBuffer buffer, MetricResult[] results, DecimalFormat df) {
		for (MetricResult result : results) {
			buffer.append(((MetricResult) result).name + " = " + df.format(((MetricResult) result).value) + "\n");
		}
	}
	
	protected String formatCompoundName(String name, String subName) {
		if(subName != null && !subName.isEmpty()) {
			return name + " " + subName;
		}
		return name;
	}

}
