package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoHomogenity extends AbstractWidgetMetric {
	
	// TODO
	private int fact(int n) {
		int result = 1;
		for (int i = 1; i <= n; i++) {
			result = result * i;
		}
		return result;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		double N = fact(dashboard.n(types));
		
		double centerX = dashboard.halfSizeX();
		double centerY = dashboard.halfSizeY();
		double dx, dy;
		int countA = 0, countB = 0, countC = 0, countD = 0;
		for (GraphicalElement graphicalElement : dashboard.getChildren(types)) {
			dx = graphicalElement.dx(centerX);
			dy = graphicalElement.dy(centerY);
			if(dx <= 0) {
				if(dy <= 0) {
					countA++;
				}
				
				if(dy >= 0) {
					countB++;
				}
			}
			
			if(dx >= 0) {
				if(dy <= 0) {
					countC++;
				}
				
				if(dy >= 0) {
					countD++;
				}
			}
		}
		
		double W = ((double) N)/(fact(countA)*fact(countB)*fact(countC)*fact(countD));
		double W_max = ((double) N)/Math.pow((((double) N)/24), 4);
		
		/*System.out.println("n = " + dashboard.n());
		System.out.println("n! = " + N);
		System.out.println("W_max = " + W_max);
		System.out.println("counts = " + countA + " " + countB + " " + countC + " " + countD + " ");
		System.out.println("counts! = " + fact(countA) + " " + fact(countB) + " " + fact(countC) + " " + fact(countD) + " ");
		System.out.println("counts! = " + (fact(countA)*fact(countB)*fact(countC)*fact(countD)));
		System.out.println("W = " + W);
		double x = 0.00463*W_max; 
		System.out.println("expected W = " + x);
		System.out.println("expected counts! = " + N/x);*/
		
		return new MetricResult[] { 
				new MetricResult("Homogenity", "HM", W/W_max)
		};
	}

}
