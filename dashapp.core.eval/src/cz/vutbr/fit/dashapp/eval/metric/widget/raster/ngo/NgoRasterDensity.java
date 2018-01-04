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
 * NgoRasterDensity - enableTotalWidgetArea=true 
 * NgoRasterDensity_X
 * NgoNormRasterDensity - enableTotalWidgetArea=true, rangeMin = 0.75, rangeMax = 1.0
 * NgoNormRasterDensity_X - rangeMin = 0.75, rangeMax = 1.0
 * 
 * MyRasterDensity - enableTotalWidgetArea=true, divide by DIVIDE_BY_ALL_AREA_RATIO
 * MyRasterDensity_X - divide by DIVIDE_BY_ALL_AREA_RATIO
 * MyRasterFinalDensity - enableTotalWidgetArea=true, divide by DIVIDE_BY_MAX_RATIO (or DIVIDE_BY_MAX_USED_RATIO), measureDensityLevel=true
 * MyRasterFinalDensity_X - divide by DIVIDE_BY_MAX_RATIO (or DIVIDE_BY_MAX_USED_RATIO), measureDensityLevel=true
 *
 */
public class NgoRasterDensity extends AbstractWidgetRasterMetric {
	
	public enum DivideByType {
		DIVIDE_BY_1,
		DIVIDE_BY_ALL_AREA_RATIO,
		DIVIDE_BY_MAX_RATIO,
		DIVIDE_BY_MAX_USED_RATIO,
	}
	
	public static final DivideByType DEFAULT_DIVIDE_BY_KIND = DivideByType.DIVIDE_BY_1;
	
	protected double rangeMin = 0;
	protected double rangeMax = 1;
	protected boolean enableTotalWidgetArea = false;
	protected boolean measureDensityLevel = false;
	protected DivideByType divideBy = DEFAULT_DIVIDE_BY_KIND;

	public NgoRasterDensity() {
		super();
	}
	
	public NgoRasterDensity(GEType[] geTypes) {
		super(geTypes);
	}
	
	public NgoRasterDensity(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public NgoRasterDensity(GEType[] geTypes, RasterRatioCalculator ratioCalculator) {
		super(geTypes, ratioCalculator);
	}
	
	public NgoRasterDensity(GEType[] geTypes, RasterRatioCalculator ratioCalculator, boolean enableTotalWidgetArea, boolean measureDensityLevel, DivideByType divideBy) {
		super(geTypes, ratioCalculator);
		setTotalWidgetArea(enableTotalWidgetArea);
		setMeasureDensityLevel(measureDensityLevel);
		setDivideBy(divideBy);
	}
	
	public NgoRasterDensity(GEType[] geTypes, RasterRatioCalculator ratioCalculator, boolean enableTotalWidgetArea, boolean measureDensityLevel, DivideByType divideBy, double rangeMin, double rangeMax) {
		super(geTypes, ratioCalculator);
		setTotalWidgetArea(enableTotalWidgetArea);
		setMeasureDensityLevel(measureDensityLevel);
		setDivideBy(divideBy);
		setRangeMin(rangeMin);
		setRangeMax(rangeMax);
	}
	
	@Override
	public String getName() {
		String rangeLabel = null;
		if(getRangeMin() != 0.0 || getRangeMax() != 1.0) {
			rangeLabel = "_" + getRangeMin() + "-" + getRangeMax();
		}
		return super.getName()
				+ (isTotalWidgetArea() ? "_total" : "")
				+ (isMeasuredDensityLevel() ? "_dl" : "")
				+ "_" + (getDivideBy().name().toLowerCase())
				+ (rangeLabel != null ? rangeLabel : "")
				;
	}
	
	public NgoRasterDensity setRangeMin(double rangeMin) {
		this.rangeMin = rangeMin;
		return this;
	}
	
	public double getRangeMin() {
		return rangeMin;
	}
	
	public NgoRasterDensity setRangeMax(double rangeMax) {
		this.rangeMax = rangeMax;
		return this;
	}
	
	public double getRangeMax() {
		return rangeMax;
	}
	
	public NgoRasterDensity setTotalWidgetArea(boolean enableTotalWidgetArea) {
		this.enableTotalWidgetArea = enableTotalWidgetArea;
		return this;
	}
	
	public boolean isTotalWidgetArea() {
		return enableTotalWidgetArea;
	}
	
	public NgoRasterDensity setMeasureDensityLevel(boolean measureDensityLevel) {
		this.measureDensityLevel = measureDensityLevel;
		return this;
	}
	
	public boolean isMeasuredDensityLevel() {
		return measureDensityLevel;
	}
	
	public NgoRasterDensity setDivideBy(DivideByType divideBy) {
		this.divideBy = divideBy;
		return this;
	}
	
	public DivideByType getDivideBy() {
		return divideBy;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			RasterRatioCalculator ratioCalculator = getRatioCalculator();
			GEType[] geTypes = getGeTypes();
			double rangeMin = getRangeMin();
			double rangeMax = getRangeMax();
			
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			
			double divideByValue = 1.0;
			DivideByType divideBy = getDivideBy();
			if(divideBy == DivideByType.DIVIDE_BY_ALL_AREA_RATIO) {
				divideByValue = ratioCalculator.getRatio(matrix, dashboard, null);
			} else if(divideBy == DivideByType.DIVIDE_BY_MAX_RATIO) {
				divideByValue = ratioCalculator.getMaxRatio();
				if(divideByValue == Double.POSITIVE_INFINITY) {
					// max infinity cant be used -> use max used ratio instead
					divideByValue = Double.NEGATIVE_INFINITY;
					divideBy = DivideByType.DIVIDE_BY_MAX_USED_RATIO;
				}
			} else if(divideBy == DivideByType.DIVIDE_BY_MAX_USED_RATIO) {
				divideByValue = Double.NEGATIVE_INFINITY;
			}
			
			double areas = 0.0;
			if(isTotalWidgetArea()) {
				List<GraphicalElement> ges = dashboard.getChildren(geTypes);
				//List<GraphicalElement> ges = dashboard.getVisibleChildren(types);
				double actRatio;
				for (GraphicalElement ge : ges) {
					//areas += ge.area()*ratioCalculator.getRatio(matrix, ge, null);
					actRatio = MathUtils.adaptNormalized(ratioCalculator.getRatio(matrix, ge, null), rangeMin, rangeMax);
					if(divideBy == DivideByType.DIVIDE_BY_MAX_USED_RATIO && actRatio > divideByValue) {
						divideByValue = actRatio;
					}
					areas += ge.area()*actRatio;
				}
			} else {
				areas = ((double) dashboard.getElementsArea(geTypes, true));
				boolean[][] geMatrix = BooleanMatrix.printDashboard(dashboard, true, geTypes);
				//double ratio = ratioCalculator.getRatio(matrix, geMatrix);
				double ratio = MathUtils.adaptNormalized(ratioCalculator.getRatio(matrix, geMatrix), rangeMin, rangeMax);
				if(divideBy == DivideByType.DIVIDE_BY_MAX_USED_RATIO) {
					divideByValue = ratio;
				}
				areas = areas*ratio;
			}
			
			double div = ((areas)/dashboard.area()*divideByValue);
			if(div > 1.0) {
				div = 1.0;
			}
			
			if(!isMeasuredDensityLevel()) {
				div = 1.0-2*Math.abs(0.5-div); // according to Ngo (rate of densitiy level close to 0.5)
			}
			
			return new MetricResult[] {
					new MetricResult("Density", "DM", div)
			};
		}
		
		return EMPTY_RESULT;
	}

}
