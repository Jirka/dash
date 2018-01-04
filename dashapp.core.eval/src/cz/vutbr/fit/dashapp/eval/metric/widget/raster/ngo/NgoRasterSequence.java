package cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo;

import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.quadrant.QuadrantMap;
import cz.vutbr.fit.dashapp.util.quadrant.QuadrantResolver;
import cz.vutbr.fit.dashapp.util.quadrant.QuadrantUpdater;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoRasterSequence extends AbstractWidgetRasterMetric {
	
	public static final int QUADRANT_AREA = 0;
	public static final int ALL_AREA = 1;
	
	public static final int DEFAULT_AREA_TYPE = QUADRANT_AREA;
	
	private int areaType = DEFAULT_AREA_TYPE;
	
	public NgoRasterSequence() {
		super();
	}
	
	public NgoRasterSequence(GEType[] geTypes) {
		super(geTypes);
	}
	
	public NgoRasterSequence(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public NgoRasterSequence(GEType[] geTypes, RasterRatioCalculator ratioCalculator) {
		super(geTypes, ratioCalculator);
	}
	
	public NgoRasterSequence(GEType[] geTypes, RasterRatioCalculator ratioCalculator, int areaType) {
		super(geTypes, ratioCalculator);
		setAreaType(areaType);
	}
	
	public NgoRasterSequence(RasterRatioCalculator ratioCalculator, int areaType) {
		super(ratioCalculator);
		setAreaType(areaType);
	}
	
	@Override
	public String getName() {
		return super.getName() + (getAreaType() == QUADRANT_AREA ? "_QUADRANT" : "_ALL");
	}
	
	public NgoRasterSequence setAreaType(int areaType) {
		this.areaType = areaType;
		return this;
	}
	
	public int getAreaType() {
		return areaType;
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
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			RasterRatioCalculator ratioCalculator = getRatioCalculator();
			GEType[] geTypes = getGeTypes();
			int areaType = getAreaType();
			
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			
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
					if(areaType == QUADRANT_AREA) {
						quadrants2.replace(this.q, quadrants2.get(q)+(graphicalElement.area(q)*ratioCalculator.getRatio(matrix, graphicalElement, q)));
					} else if(areaType == ALL_AREA) {
						quadrants2.replace(this.q, quadrants2.get(q)+(graphicalElement.area()*ratioCalculator.getRatio(matrix, graphicalElement, null)));
					}
				}
			}.perform(dashboard, geTypes, QuadrantResolver.BY_CENTER, false);
			
			// every quadrant has its own default weight of user interest
			new QuadrantUpdater<Double, Integer>(quadrants2, new Integer[] {4,3,2,1}, false) {

				@Override
				protected Double computeValue() {
					return quadrants2.get(q)*o;
				}
			}.perform();
			
			/*new QuadrantUpdater<Double, Double>(quadrants2, new Double[] {2.0,1.66,1.33,1.0}, false) {

				@Override
				protected Double computeValue() {
					return quadrants2.get(q)*o;
				}
			}.perform();*/
			
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
		
		return EMPTY_RESULT;
	}

}
