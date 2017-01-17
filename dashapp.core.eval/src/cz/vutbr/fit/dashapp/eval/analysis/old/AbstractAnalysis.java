package cz.vutbr.fit.dashapp.eval.analysis.old;

import java.text.DecimalFormat;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;

public abstract class AbstractAnalysis implements IAnalysis {

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
