package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoEconomy extends AbstractWidgetMetric {
	
	public NgoEconomy() {
		super();
	}
	
	public NgoEconomy(GEType[] geTypes) {
		super(geTypes);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		return new MetricResult[] {
				new MetricResult("Economy", "ECM", 1.0/dashboard.getNumberOfSizes(getGeTypes()))
		};
	}

}
