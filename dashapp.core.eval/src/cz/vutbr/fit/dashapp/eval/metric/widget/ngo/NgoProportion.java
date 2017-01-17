package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoProportion extends AbstractWidgetMetric {
	
	public static final double[] constants = { 1, 1/1.414, 1/1.618, 1/1.732, 1.0/2 };
	
	private double calculateDistance(double aspectRatio) {
		double min = 0.5;
		double act;
		for (double c : constants) {
			act = Math.abs(c-aspectRatio);
			if(act < min) {
				min = act;
			}
		}
		return min;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		double aspectRatio, dist, min2;
		
		// proportion of objects
		double PM_object = 0.0;
		for (GraphicalElement graphicalElement : dashboard.getChildren(types)) {
			// normalized aspect ratio of graphical element 
			aspectRatio = graphicalElement.aspectRatio(true);
			// find out if aspect ratio is close to some preferred constant 
			dist = calculateDistance(aspectRatio);
			// add distance
			PM_object += 1-(dist*2);
		}
		PM_object /= dashboard.n(types);
		
		// proportion of layout
		aspectRatio = dashboard.aspectRatio(true);
		if(aspectRatio > 1.0) {
			aspectRatio = 1.0/aspectRatio;
		}
		dist = 0.5;
		for (double d : constants) {
			min2 = Math.abs(d-aspectRatio);
			if(min2 < dist) {
				dist = min2;
			}
		}
		
		double PM_layout = 1-(dist/0.5);
		
		return new MetricResult[] {
				new MetricResult("Proportion", "PM", (Math.abs(PM_object)+Math.abs(PM_layout))/2)	
		};
	}

}
