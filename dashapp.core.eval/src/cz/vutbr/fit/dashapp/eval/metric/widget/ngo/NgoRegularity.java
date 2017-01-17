package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Constants;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoRegularity extends AbstractWidgetMetric {
	
	private int computeNumberOfSpacings(Dashboard dashboard, GEType[] types, int dimension) {
		int n = dashboard.n(types);
		if(dashboard.n(types) > 0) {
			int[] points = new int[n];
			int i = 0;
			for (GraphicalElement graphicalElement : dashboard.getChildren(types)) {
				points[i] = graphicalElement.p(dimension);
				i++;
			}
			Arrays.sort(points);
			
			int prev = points[0];
			int d;
			List<Integer> distances = new ArrayList<>();
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
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		double RM_alignment, RM_spacing;
		int n_spacing = computeNumberOfSpacings(dashboard, types, Constants.X) + computeNumberOfSpacings(dashboard, types, Constants.Y);//4; // TODO
		//int n_spacing = 16; // TODO
		
		if(dashboard.n(types) == 1) {
			RM_alignment = RM_spacing = 1;
		} else {
			RM_alignment = 1-(((double) dashboard.getVAP(types)+dashboard.getHAP(types))/(2*dashboard.n(types)));
			RM_spacing = 1-(((double) n_spacing-1)/(2*(dashboard.n(types)-1)));
			System.out.println(RM_alignment);
			System.out.println(RM_spacing);
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
