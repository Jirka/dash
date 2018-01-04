package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoRegularity extends AbstractWidgetMetric {
	
	public NgoRegularity() {
		super();
	}
	
	public NgoRegularity(GEType[] geTypes) {
		super(geTypes);
	}
	
	private int computeNumberOfSpacings(Dashboard dashboard, GEType[] types, int dimension) {
		int n = dashboard.n(types);
		if(n > 0) {
			int[] points = new int[n];
			int i = 0;
			for (GraphicalElement graphicalElement : dashboard.getChildren(types)) {
				points[i] = graphicalElement.p(dimension);
				i++;
			}
			Arrays.sort(points);
			
			int prev = points[0];
			int d;
			Set<Integer> distances = new HashSet<>();
			for(i = 1; i < n; i++) {
				if(points[i] != prev) {
					d = points[i] - prev;
					if(!distances.contains(d)) {
						distances.add(d);
					}
					prev = points[i];
				}
			}
			return distances.size();
		} else {
			return 0;
		}
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		double RM_alignment, RM_spacing;
		int n_spacing = computeNumberOfSpacings(dashboard, getGeTypes(), Constants.X) + computeNumberOfSpacings(dashboard, getGeTypes(), Constants.Y);//4; // TODO
		//int n_spacing = 16; // TODO
		int n = dashboard.n(getGeTypes());
		
		if(n == 1) {
			RM_alignment = RM_spacing = 1;
		} else {
			RM_alignment = 1-(((double) dashboard.getVAP(getGeTypes())+dashboard.getHAP(getGeTypes()))/(2*n));
			RM_spacing = 1-(((double) n_spacing-1)/(2*(n-1)));
			//System.out.println(RM_alignment);
			//System.out.println(RM_spacing);
		}
		
		/*double x = 0.37500;
		//x = (2*x)-Math.abs(RM_alignment);
		//System.out.println(RM_spacing);
		x = ((-(x-1))*(2*dashboard.n()-1))+1;
		System.out.println("n_spacing:" x);*/
		
		return new MetricResult[] {
				new MetricResult("Regulartity", "RM", (Math.abs(RM_alignment)+Math.abs(RM_spacing))/2)
		};
	}

}
