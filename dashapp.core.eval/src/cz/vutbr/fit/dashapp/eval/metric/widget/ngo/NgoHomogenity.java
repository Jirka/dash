package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.quadrant.QuadrantMap;
import cz.vutbr.fit.dashapp.util.quadrant.QuadrantResolver;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoHomogenity extends AbstractWidgetMetric {
	
	public NgoHomogenity() {
		super();
	}
	
	public NgoHomogenity(GEType[] geTypes) {
		super(geTypes);
	}
	
	// TODO
	private double fact(int n) {
		double result = 1;
		for (int i = 1; i <= n; i++) {
			result = result * i;
		}
		return result;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		
		QuadrantMap<Integer> quadrants = new QuadrantMap<Integer>(0);
		
		// calculate occurence of graphical elements in quadrants
		new QuadrantResolver() {
			
			@Override
			protected void performAllPre(GraphicalElement graphicalElement) {
				quadrants.replace(this.q, quadrants.get(q)+1);
			}
		}.perform(dashboard, getGeTypes(), QuadrantResolver.BY_CENTER, false);
		int countA = quadrants.get(Quadrant.I), countB = quadrants.get(Quadrant.II), countC = quadrants.get(Quadrant.III), countD = quadrants.get(Quadrant.IV);
		
		int n = countA+countB+countC+countD;//dashboard.n(types);
		double N = fact(n);
		
		double j=(fact(countA)*fact(countB)*fact(countC)*fact(countD));
		double W = N/j;
		double jj = Math.pow(fact(n/4), 4);
		double W_max = N/jj;
		
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
