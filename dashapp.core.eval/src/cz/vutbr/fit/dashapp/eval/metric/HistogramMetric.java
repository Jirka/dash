package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public abstract class HistogramMetric extends AbstractMetric implements IMetric {
	
	protected int[] histogram;

	public HistogramMetric(Dashboard dashboard, int[] historgram) {
		super(dashboard, GEType.ALL_TYPES);
		this.histogram = historgram;
	}
	
	protected int getArea() {
		int area = 0;
		for (int i : histogram) {
			area += i;
		}
		return area;
	}

}
