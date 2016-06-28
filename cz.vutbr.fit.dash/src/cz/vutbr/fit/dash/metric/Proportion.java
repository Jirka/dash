package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class Proportion extends AbstractMetric implements IMetric {
	
	public static final double[] constants = { 1, 1/1.414, 1/1.618, 1/1.732, 1.0/2 };

	public Proportion(Dashboard dashboard, Type[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "PM";
	}
	
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
	public Object measure() {
		double aspectRatio, dist, min2;
		
		// proportion of objects
		double PM_object = 0.0;
		for (GraphicalElement graphicalElement : dashboard.getGraphicalElements(getTypes())) {
			// normalized aspect ratio of graphical element 
			aspectRatio = graphicalElement.getAspectRatio(true);
			// find out if aspect ratio is close to some preferred constant 
			dist = calculateDistance(aspectRatio);
			// add distance
			PM_object += 1-(dist*2);
		}
		PM_object /= dashboard.n(getTypes());
		
		// proportion of layout
		aspectRatio = dashboard.getAspectRatio(true);
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
		
		return (Math.abs(PM_object)+Math.abs(PM_layout))/2;
	}

}
