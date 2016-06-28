package cz.vutbr.fit.dash.metric;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.Type;

public class Homogenity extends AbstractMetric implements IMetric {

	public Homogenity(Dashboard dashboard, Type[] types) {
		super(dashboard, types);
	}

	@Override
	public String getInicials() {
		return "HM";
	}
	
	// TODO
	private int fact(int n) {
		int result = 1;
		for (int i = 1; i <= n; i++) {
			result = result * i;
		}
		return result;
	}

	@Override
	public Object measure() {
		double N = fact(dashboard.n(getTypes()));
		
		double centerX = dashboard.ownCenterX();
		double centerY = dashboard.ownCenterY();
		double dx, dy;
		int countA = 0, countB = 0, countC = 0, countD = 0;
		for (GraphicalElement graphicalElement : dashboard.getGraphicalElements(getTypes())) {
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
		
		return W/W_max;
	}

}
