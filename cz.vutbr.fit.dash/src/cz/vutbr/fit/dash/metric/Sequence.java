package cz.vutbr.fit.dash.metric;

import java.util.Map.Entry;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.Quadrant;
import cz.vutbr.fit.dash.util.QuadrantMap;
import cz.vutbr.fit.dash.util.QuadrantResolver;
import cz.vutbr.fit.dash.util.QuadrantUpdater;

public class Sequence extends AbstractMetric implements IMetric {
	
	/**
	 * Weight of quadrants
	 */
	private QuadrantMap<Integer> quadrants;
	//private QuadrantMap<Double> quadrants2;

	public Sequence(Dashboard dashboard) {
		super(dashboard);
	}

	@Override
	public String getInicials() {
		return "SQM";
	}
	
	private int getQuadrantValue(Quadrant q, QuadrantMap<Integer> map) {
		int value = 1;
		Integer item = map.get(q);
		for(Entry<Quadrant, Integer> act : quadrants.entrySet()) {
			if(act.getKey() != q && item > act.getValue()) {
				value++;
			}
		}
		return value;
	}

	@Override
	public Object measure() {
		// initialize quadrants
		quadrants = new QuadrantMap<Integer>(0);
		//quadrants2 = new QuadrantMap<Double>(0.0);
		
		// calculate areas of graphical elements in quadrants
		new QuadrantResolver() {
			
			@Override
			protected void performAll(GraphicalElement graphicalElement) {
				quadrants.replace(this.q, quadrants.get(q)+graphicalElement.area());
			}
		}.perform(dashboard, QuadrantResolver.BY_CENTER, false);
		
		/*new QuadrantResolver() {
			
			@Override
			protected void performAll(GraphicalElement graphicalElement) {
				quadrants2.replace(this.q, quadrants2.get(q)+graphicalElement.area(q));
			}
		}.perform(dashboard, QuadrantResolver.BY_CENTER, false);*/
		
		// every quadrant has its own default weight of user interest
		new QuadrantUpdater<Integer, Integer>(quadrants, new Integer[] {4,3,2,1}, false) {

			@Override
			protected Integer computeValue() {
				return quadrants.get(q)*o;
			}
		}.perform();
		
		// final quadrants evaluation
		quadrants = new QuadrantUpdater<Integer, Integer>(quadrants, new Integer[] {4,3,2,1}, true) {

			@Override
			protected Integer computeValue() {
				return Math.abs(o-getQuadrantValue(q, quadrants));
			}
		}.perform();
		
		// make sum of quadrants
		int SQM = 0;
		for (Integer value : quadrants.values()) {
			SQM += value;
		}
		
		return 1-SQM/8.0;
	}

}
