package cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo;

import java.util.List;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.MathUtils;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;
import cz.vutbr.fit.dashapp.model.IDashboardFile;

/**
 * 
 * @author Jiri Hynek
 * 
 * note: This class was created by combination of:
 * 
 * NgoRasterUnity - enableTotalWidgetArea=true 
 * NgoRasterUnity_X
 * NgoNormRasterUnity - enableTotalWidgetArea=true, rangeMin = 0.75, rangeMax = 1.0
 * NgoNormRasterUnity_X - rangeMin = 0.75, rangeMax = 1.0
 * 
 * MyRasterUnity - enableTotalWidgetArea=true, multiple by MULTIPLY_BY_ALL_AREA_RATIO
 * MyRasterUnity_X - multiple by MULTIPLY_BY_ALL_AREA_RATIO
 *
 */
public class NgoRasterUnity extends AbstractWidgetRasterMetric {
	
	public enum MultiplyByType {
		MULTIPLY_BY_1,
		MULTIPLY_BY_ALL_AREA_RATIO,
	}
	
	public static final MultiplyByType DEFAULT_MULTIPLY_BY_KIND = MultiplyByType.MULTIPLY_BY_1;

	protected double rangeMin = 0;
	protected double rangeMax = 1;
	protected boolean enableTotalWidgetArea = false;
	protected MultiplyByType multiplyBy = DEFAULT_MULTIPLY_BY_KIND;

	public NgoRasterUnity() {
		super();
	}
	
	public NgoRasterUnity(GEType[] geTypes) {
		super(geTypes);
	}
	
	public NgoRasterUnity(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public NgoRasterUnity(GEType[] geTypes, RasterRatioCalculator ratioCalculator) {
		super(geTypes, ratioCalculator);
	}
	
	public NgoRasterUnity(GEType[] geTypes, RasterRatioCalculator ratioCalculator, boolean enableTotalWidgetArea, MultiplyByType multiplyBy) {
		super(geTypes, ratioCalculator);
		setTotalWidgetArea(enableTotalWidgetArea);
		setMultiplyBy(multiplyBy);
	}
	
	public NgoRasterUnity(GEType[] geTypes, RasterRatioCalculator ratioCalculator, boolean enableTotalWidgetArea, MultiplyByType multiplyBy, double rangeMin, double rangeMax) {
		super(geTypes, ratioCalculator);
		setTotalWidgetArea(enableTotalWidgetArea);
		setMultiplyBy(multiplyBy);
		setRangeMin(rangeMin);
		setRangeMax(rangeMax);
	}
	
	@Override
	public String getName() {
		String rangeLabel = null;
		if(getRangeMin() != 0.0 || getRangeMax() != 1.0) {
			rangeLabel = "_" + getRangeMax() + "-" + getRangeMax();
		}
		return super.getName()
				+ (isTotalWidgetArea() ? "_total" : "")
				+ "_" + getMultiplyBy().name().toLowerCase()
				+ (rangeLabel != null ? rangeLabel : "")
				;
	}
	
	public NgoRasterUnity setRangeMin(double rangeMin) {
		this.rangeMin = rangeMin;
		return this;
	}
	
	public double getRangeMin() {
		return rangeMin;
	}
	
	public NgoRasterUnity setRangeMax(double rangeMax) {
		this.rangeMax = rangeMax;
		return this;
	}
	
	public double getRangeMax() {
		return rangeMax;
	}
	
	public NgoRasterUnity setTotalWidgetArea(boolean enableTotalWidgetArea) {
		this.enableTotalWidgetArea = enableTotalWidgetArea;
		return this;
	}
	
	public boolean isTotalWidgetArea() {
		return enableTotalWidgetArea;
	}
	
	public NgoRasterUnity setMultiplyBy(MultiplyByType multiplyBy) {
		this.multiplyBy = multiplyBy;
		return this;
	}
	
	public MultiplyByType getMultiplyBy() {
		return multiplyBy;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		//int areas = dashboard.getElementsArea(types);
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			RasterRatioCalculator ratioCalculator = getRatioCalculator();
			GEType[] geTypes = getGeTypes();
			double rangeMin = getRangeMin();
			double rangeMax = getRangeMax();
			
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			
			double areas = 0.0;
			if(isTotalWidgetArea()) {
				List<GraphicalElement> ges = dashboard.getChildren(geTypes);
				//List<GraphicalElement> ges = dashboard.getVisibleChildren(types);
				for (GraphicalElement ge : ges) {
					//areas += ge.area()*ratioCalculator.getRatio(matrix, ge, null);
					areas += ge.area()*MathUtils.adaptNormalized(ratioCalculator.getRatio(matrix, ge, null), rangeMin, rangeMax);
				}
			} else {
				areas = ((double) dashboard.getElementsArea(geTypes, true));
				boolean[][] geMatrix = BooleanMatrix.printDashboard(dashboard, true, geTypes);
				//double ratio = ratioCalculator.getRatio(matrix, geMatrix);
				double ratio = MathUtils.adaptNormalized(ratioCalculator.getRatio(matrix, geMatrix), rangeMin, rangeMax);
				areas = areas*ratio;
			}
			
			double UM_form = 1 - (((double)(dashboard.getNumberOfSizes(geTypes)-1))/dashboard.n(geTypes));
			//double UM_form = 1 - (((double)(dashboard.getNumberOfVisibleSizes(types)-1))/ges.size());
			double UM_space = 0;
			
			double multiplyByValue = 1.0;
			MultiplyByType multiplyBy = getMultiplyBy();
			if(multiplyBy == MultiplyByType.MULTIPLY_BY_ALL_AREA_RATIO) {
				multiplyByValue = ratioCalculator.getRatio(matrix, dashboard, null);
			}
			double emptyArea = dashboard.area()*multiplyByValue-areas;
			
			if(emptyArea < 0) {
				emptyArea = 0.0;
			}
			
			if(emptyArea != 0) {
				if(multiplyBy == MultiplyByType.MULTIPLY_BY_ALL_AREA_RATIO) {
					multiplyByValue = ratioCalculator.getRatio(matrix, dashboard.getLayoutRectangle(geTypes));
				} else {
					multiplyByValue = 1.0;
				}
				double div = ((double) (dashboard.getLayoutArea(geTypes)*multiplyByValue-areas))/(emptyArea);
				if(div > 1.0) {
					div = 1.0;
				}
				if(div < 0) {
					div = 0;
				}
				UM_space = 1 - div;
			}
			
			return new MetricResult[] {
					new MetricResult("Unity", "UM", (Math.abs(UM_form)+Math.abs(UM_space))/2)	
			};
		}
		
		return EMPTY_RESULT;
	}

}
