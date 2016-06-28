package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public abstract class HistogramMetric extends AbstractMetric implements IMetric {
	
	protected int[] histogram;

	public HistogramMetric(Dashboard dashboard, int[] historgram) {
		super(dashboard, Type.ALL_TYPES);
		this.histogram = historgram;
	}

}
