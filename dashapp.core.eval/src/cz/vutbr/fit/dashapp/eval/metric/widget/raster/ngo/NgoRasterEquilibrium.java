package cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo;

import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.model.Constants.Side;

/**
 * 
 * @author Jiri Hynek
 * 
 * note: This class was created by combination of:
 * 
 * NgoRasterEquilibrium
 * MyRasterEquilibrium - Works only with area of widget that is located in particular side.
 *
 */
public class NgoRasterEquilibrium extends AbstractWidgetRasterMetric {
	
	public static final int BASIC = 0;
	public static final int AREA_OF_SIDE = 1;
	
	protected int equilibriumKind = BASIC;
	
	public NgoRasterEquilibrium() {
		super();
	}
	
	public NgoRasterEquilibrium(GEType[] geTypes) {
		super(geTypes);
	}
	
	public NgoRasterEquilibrium(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public NgoRasterEquilibrium(GEType[] geTypes, RasterRatioCalculator ratioCalculator) {
		super(geTypes, ratioCalculator);
	}
	
	public NgoRasterEquilibrium(GEType[] geTypes, RasterRatioCalculator ratioCalculator, int equilibriumKind) {
		super(geTypes, ratioCalculator);
		setEquilibriumKind(equilibriumKind);
	}
	
	public NgoRasterEquilibrium setEquilibriumKind(int equilibriumKind) {
		this.equilibriumKind = equilibriumKind;
		return this;
	}
	
	@Override
	public String getName() {
		return super.getName() + (getEquilibriumKind() == BASIC ? "" : "_side-area");
	}
	
	public int getEquilibriumKind() {
		return equilibriumKind;
	}
	
	public MetricResult[] getEquilibrium(Dashboard dashboard, GEType[] types, RasterRatioCalculator ratioCalculator) {
		int equilibriumKind = getEquilibriumKind();
		if(equilibriumKind == AREA_OF_SIDE) {
			return getEquilibriumSideArea(dashboard, types, ratioCalculator);
		}
		
		return getEquilibriumBasic(dashboard, types, ratioCalculator);
	}
	
	private MetricResult[] getEquilibriumBasic(Dashboard dashboard, GEType[] types, RasterRatioCalculator ratioCalculator) {
		// optimal version (equilibrium is calculated for both dimensions)
		double centerX = dashboard.width/2.0;
		double centerY = dashboard.height/2.0;
		
		double EM_x = 0.0;
		double EM_y = 0.0;
		double areas = 0;
		
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			
			double maxRatio = ratioCalculator.getMaxRatio();
			boolean findMaxRatio = false;
			if(maxRatio == Double.POSITIVE_INFINITY) {
				maxRatio = Double.MIN_VALUE;
				findMaxRatio = true;
			}
			
			int area;
			double ratio;
			for (GraphicalElement graphicalElement : dashboard.getChildren(geTypes)) {
				area = graphicalElement.area();
				ratio = ratioCalculator.getRatio(matrix, graphicalElement, null);
				if(findMaxRatio && maxRatio < ratio) {
					maxRatio = ratio;
				}
				EM_x += area*(graphicalElement.dx(centerX))*ratio;
				EM_y += area*(graphicalElement.dy(centerY))*ratio;
				areas += area;
			}
			
			//double elemCount = dashboard.n(types);
			EM_x = (2*EM_x)/(/*elemCount**/dashboard.width*areas*maxRatio);
			EM_y = (2*EM_y)/(/*elemCount**/dashboard.height*areas*maxRatio);
			// it makes no sense to apply elemCount in equation (mistake in the paper?)
			
			return new MetricResult[] { 
					new MetricResult("Equilibrium", "EM", 1-(Math.abs(EM_x)+Math.abs(EM_y))/2.0),
					new MetricResult("Equilibrium (X)", "EM_x", EM_x),
					new MetricResult("Equilibrium (Y)", "EM_y", EM_y)
				};
		}
		return EMPTY_RESULT;
	}

	private MetricResult[] getEquilibriumSideArea(Dashboard dashboard, GEType[] types, RasterRatioCalculator ratioCalculator) {
		// optimal version (equilibrium is calculated for both dimensions)
		//double centerX = dashboard.width/2.0;
		//double centerY = dashboard.height/2.0;
		
		double EM_x = 0.0;
		double EM_y = 0.0;
		double areas = 0;
		
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			
			double maxRatio = ratioCalculator.getMaxRatio();
			boolean findMaxRatio = false;
			if(maxRatio == Double.POSITIVE_INFINITY) {
				maxRatio = Double.MIN_VALUE;
				findMaxRatio = true;
			}
			
			double[] ratios = new double[4];
			for (GraphicalElement ge : dashboard.getChildren(types)) {
				ratios[0] = ratioCalculator.getRatio(matrix, ge, Side.LEFT);
				ratios[1] = ratioCalculator.getRatio(matrix, ge, Side.RIGHT);
				ratios[2] = ratioCalculator.getRatio(matrix, ge, Side.UP);
				ratios[3] = ratioCalculator.getRatio(matrix, ge, Side.DOWN);
				if(findMaxRatio) {
					maxRatio = getMaxRatio(ratios, maxRatio);
				}
				EM_x += ge.area(Side.LEFT)*ge.depth(Side.LEFT)*ratios[0]-ge.area(Side.RIGHT)*ge.depth(Side.RIGHT)*ratios[1];
				EM_y += ge.area(Side.UP)*ge.depth(Side.UP)*ratios[2]-ge.area(Side.DOWN)*ge.depth(Side.DOWN)*ratios[3];
				areas += ge.area();
			}
			
			//double elemCount = dashboard.n(types);
			EM_x = (2*EM_x)/(/*elemCount**/dashboard.width*areas*maxRatio);
			EM_y = (2*EM_y)/(/*elemCount**/dashboard.height*areas*maxRatio);
			// it makes no sense to apply elemCount in equation (mistake in the paper?)
			
			return new MetricResult[] { 
					new MetricResult("Equilibrium", "EM", 1-(Math.abs(EM_x)+Math.abs(EM_y))/2.0),
					new MetricResult("Equilibrium (X)", "EM_x", EM_x),
					new MetricResult("Equilibrium (Y)", "EM_y", EM_y)
				};
		}
		return EMPTY_RESULT;
	}
	
	private double getMaxRatio(double[] ratios, double maxRatio) {
		for (double ratio : ratios) {
			if(ratio > maxRatio) {
				maxRatio = ratio;
			}
		}
		return maxRatio;
	}

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		return getEquilibrium(dashboard, getGeTypes(), getRatioCalculator());
	}

}
