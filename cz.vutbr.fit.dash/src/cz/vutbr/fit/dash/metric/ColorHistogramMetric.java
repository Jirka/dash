package cz.vutbr.fit.dash.metric;

import java.util.Map;

import cz.vutbr.fit.dash.model.Dashboard;

public abstract class ColorHistogramMetric extends AbstractMetric implements IMetric {
	
	protected Map<Integer, Integer> histogram;

	public ColorHistogramMetric(Dashboard dashboard, Map<Integer, Integer> histogram) {
		super(dashboard);
		this.histogram = histogram;
	}

}
