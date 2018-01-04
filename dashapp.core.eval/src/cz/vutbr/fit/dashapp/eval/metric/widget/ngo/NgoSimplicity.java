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
public class NgoSimplicity extends AbstractWidgetMetric {
	
	public NgoSimplicity() {
		super();
	}
	
	public NgoSimplicity(GEType[] geTypes) {
		super(geTypes);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		return new MetricResult[] {
				new MetricResult("Simplicity", "SMM",
						3.0/(dashboard.getHAP(getGeTypes())+dashboard.getVAP(getGeTypes())+dashboard.n(getGeTypes())))	
		};
	}

}
