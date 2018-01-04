package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoProportion extends AbstractWidgetMetric {
	
	public NgoProportion() {
		super();
	}
	
	public NgoProportion(GEType[] geTypes) {
		super(geTypes);
	}
	
	public static final double[] constants = { 1.0, 1.0/1.414, 1.0/1.618, 1.0/1.732, 1.0/2.0 };
	
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
	public MetricResult[] measure(Dashboard dashboard) {
		double aspectRatio, dist, actDist;
		
		// proportion of objects
		double PM_object = 0.0;
		List<GraphicalElement> children = dashboard.getChildren(getGeTypes());
		for (GraphicalElement graphicalElement : children) {
			// normalized aspect ratio of graphical element 
			aspectRatio = graphicalElement.aspectRatio(true);
			// find out if aspect ratio is close to some preferred constant 
			dist = calculateDistance(aspectRatio);
			// add distance
			PM_object += (1-(dist*2));
		}
		PM_object /= dashboard.n(getGeTypes());
		
		// proportion of layout
		aspectRatio = dashboard.aspectRatio(true);
		dist = 0.5;
		for (double d : constants) {
			actDist = Math.abs(d-aspectRatio);
			if(actDist < dist) {
				dist = actDist;
			}
		}
		
		double PM_layout = (1-(dist*2));
		
		return new MetricResult[] {
				new MetricResult("Proportion", "PM", (Math.abs(PM_object)+Math.abs(PM_layout))/2)	
		};
	}

}
