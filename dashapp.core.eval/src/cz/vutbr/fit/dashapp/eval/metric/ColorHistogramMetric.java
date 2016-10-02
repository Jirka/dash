package cz.vutbr.fit.dashapp.eval.metric;

import java.util.Map;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public abstract class ColorHistogramMetric extends AbstractMetric implements IMetric {
	
	protected Map<Integer, Integer> histogram;

	public ColorHistogramMetric(Dashboard dashboard, Map<Integer, Integer> histogram) {
		super(dashboard, GEType.ALL_TYPES);
		this.histogram = histogram;
	}

}
