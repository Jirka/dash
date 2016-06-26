package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;

public abstract class HistogramMetric extends AbstractMetric implements IMetric {
	
	protected int[] histogram;

	public HistogramMetric(Dashboard dashboard, int[] historgram) {
		super(dashboard);
		this.histogram = historgram;
	}

}
