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
public class NgoUnity extends AbstractWidgetMetric {
	
	public NgoUnity() {
		super();
	}
	
	public NgoUnity(GEType[] geTypes) {
		super(geTypes);
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		int areas = dashboard.getElementsArea(getGeTypes(), true);
		
		double UM_form = 1 - (((double)(dashboard.getNumberOfSizes(getGeTypes())-1))/dashboard.n(getGeTypes()));
		double UM_space = 0;
		
		int emptyArea = dashboard.area()-areas;
		if(emptyArea != 0) {
			UM_space = 1 - ((double) (dashboard.getLayoutArea(getGeTypes())-areas))/(emptyArea);
		}
		
		return new MetricResult[] {
				new MetricResult("Unity", "UM", (Math.abs(UM_form)+Math.abs(UM_space))/2)	
		};
	}

}
