package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.quadrant.QuadrantMap;
import cz.vutbr.fit.dashapp.util.quadrant.QuadrantResolver;
import cz.vutbr.fit.dashapp.util.quadrant.QuadrantUpdater;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoSequence extends AbstractWidgetMetric {
	
	public NgoSequence() {
		super();
	}
	
	public NgoSequence(GEType[] geTypes) {
		super(geTypes);
	}
	
	/**
	 * Weight of quadrants
	 */
	//private QuadrantMap<Integer> quadrants;
	private QuadrantMap<Double> quadrants2;
	
	private int getQuadrantValue(Quadrant q, QuadrantMap<Double> map) {
		int value = 1;
		Double item = map.get(q);
		int qIndex = q.getIndex();
		int i = 0;
		for(Entry<Quadrant, Double> act : quadrants2.entrySet()) {
			if(act.getKey() != q && item >= act.getValue()) {
				if(item == act.getValue()) {
					if(qIndex < i) {
						value++;
					}
				} else {
					value++;
				}
			}
			i++;
		}
		return value;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		// initialize quadrants
		//quadrants = new QuadrantMap<Integer>(0);
		quadrants2 = new QuadrantMap<Double>(0.0);
		
		// calculate areas of graphical elements in quadrants
		/*new QuadrantResolver() {
			
			@Override
			protected void performAll(GraphicalElement graphicalElement) {
				quadrants.replace(this.q, quadrants.get(q)+graphicalElement.area());
			}
		}.perform(dashboard, types, QuadrantResolver.BY_CENTER, false);*/
		
		new QuadrantResolver() {
			
			@Override
			protected void performAllPre(GraphicalElement graphicalElement) {
				quadrants2.replace(this.q, quadrants2.get(q)+graphicalElement.area(q));
			}
		}.perform(dashboard, getGeTypes(), QuadrantResolver.BY_CENTER, false);
		
		// every quadrant has its own default weight of user interest
		new QuadrantUpdater<Double, Integer>(quadrants2, new Integer[] {4,3,2,1}, false) {

			@Override
			protected Double computeValue() {
				return quadrants2.get(q)*o;
			}
		}.perform();
		
		// final quadrants evaluation
		quadrants2 = new QuadrantUpdater<Double, Integer>(quadrants2, new Integer[] {4,3,2,1}, true) {

			@Override
			protected Double computeValue() {
				return (double) Math.abs(o-getQuadrantValue(q, quadrants2));
			}
		}.perform();
		
		// make sum of quadrants
		double SQM = 0;
		for (Double value : quadrants2.values()) {
			SQM += value;
		}
		
		return new MetricResult[] {
				new MetricResult("Sequence", "SQM", 1-SQM/8.0)
		};
	}

}
