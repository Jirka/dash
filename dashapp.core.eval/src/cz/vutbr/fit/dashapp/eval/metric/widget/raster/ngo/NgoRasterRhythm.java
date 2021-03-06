package cz.vutbr.fit.dashapp.eval.metric.widget.raster.ngo;

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
import cz.vutbr.fit.dashapp.model.IDashboardFile;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class NgoRasterRhythm extends AbstractWidgetRasterMetric {
	
	public static final int SUM = 0;
	public static final int MAX = 1;
	
	public static final int DEFAULT_NORMALIZATION_TYPE = MAX;
	
	private int normalizationType = DEFAULT_NORMALIZATION_TYPE;
	
	public NgoRasterRhythm() {
		super();
	}
	
	public NgoRasterRhythm(GEType[] geTypes) {
		super(geTypes);
	}
	
	public NgoRasterRhythm(RasterRatioCalculator ratioCalculator) {
		super(ratioCalculator);
	}
	
	public NgoRasterRhythm(GEType[] geTypes, RasterRatioCalculator ratioCalculator) {
		super(geTypes, ratioCalculator);
	}
	
	public NgoRasterRhythm(GEType[] geTypes, RasterRatioCalculator ratioCalculator, int normalizationType) {
		super(geTypes, ratioCalculator);
		setNormalizationType(normalizationType);
	}
	
	public NgoRasterRhythm(RasterRatioCalculator ratioCalculator, int normalizationType) {
		super(ratioCalculator);
		setNormalizationType(normalizationType);
	}
	
	@Override
	public String getName() {
		return super.getName() + (getNormalizationType() == MAX ? "_MAX" : "_SUM");
	}
	
	public void setNormalizationType(int normalizationType) {
		this.normalizationType = normalizationType;
	}
	
	public int getNormalizationType() {
		return normalizationType;
	}
	
	private QuadrantMap<RhythmValues> quadrants;

	@Override
	public MetricResult[] measure(Dashboard dashboard) {
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			RasterRatioCalculator ratioCalculator = getRatioCalculator();
			GEType[] geTypes = getGeTypes();
			int normalizationType = getNormalizationType();
			
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			
			// initialize map of quadrants containing symmetry values
			quadrants = new QuadrantMap<RhythmValues>(new RhythmValues());
			
			// calculate symmetry values for all quadrants
			new QuadrantResolver() {
				
				protected void performAllPre(GraphicalElement graphicalElement) {
					RhythmValues value = quadrants.get(q);
					double ratio = ratioCalculator.getRatio(matrix, graphicalElement, null);
					value.n++;
					value.x += Math.abs(dx)*ratio;
					value.y += Math.abs(dy)*ratio;
					value.a += graphicalElement.area()*ratio;
					
					/*quadrants.get(Quadrant.I).a += graphicalElement.area(Quadrant.I);
					quadrants.get(Quadrant.II).a += graphicalElement.area(Quadrant.II);
					quadrants.get(Quadrant.III).a += graphicalElement.area(Quadrant.III);
					quadrants.get(Quadrant.IV).a += graphicalElement.area(Quadrant.IV);*/
				};
				
			}.perform(dashboard, geTypes, QuadrantResolver.BY_CENTER, false);
			
			// for shorter code
			RhythmValues q1 = quadrants.get(Quadrant.I);
			RhythmValues q2 = quadrants.get(Quadrant.II);
			RhythmValues q3 = quadrants.get(Quadrant.III);
			RhythmValues q4 = quadrants.get(Quadrant.IV);
			
			/*double nn = q1.n + q2.n + q3.n + q4.n;
			//double xx = nn*dashboard.width/2;
			double xx = q1.x + q2.x + q3.x + q4.x;
			//double yy = nn*dashboard.height/2;
			double yy = q1.y + q2.y + q3.y + q4.y;
			double aa = dashboard.area()/4;//q1.a + q2.a + q3.a + q4.a;*/
			
			if(normalizationType == SUM) {
				double xx = sum(new double[] { q1.x , q2.x , q3.x , q4.x });
				double yy = sum(new double[] { q1.y , q2.y , q3.y , q4.y });
				double aa = sum(new double[] { q1.a , q2.a , q3.a , q4.a });
				q1.normalize((xx), (yy), aa);
				q2.normalize((xx), (yy), aa);
				q3.normalize((xx), (yy), aa);
				q4.normalize((xx), (yy), aa);
			} else if(normalizationType == MAX) {
				double xx = max(new double[] { q1.x , q2.x , q3.x , q4.x });
				double yy = max(new double[] { q1.y , q2.y , q3.y , q4.y });
				double aa = max(new double[] { q1.a , q2.a , q3.a , q4.a });
				q1.normalize((xx), (yy), aa);
				q2.normalize((xx), (yy), aa);
				q3.normalize((xx), (yy), aa);
				q4.normalize((xx), (yy), aa);
			}
			
			double RHM_x = (diff(q1.x, q2.x) + diff(q3.x, q4.x) + diff(q1.x, q3.x) + diff(q2.x, q4.x)
							+ diff(q1.x, q4.x) + diff(q2.x, q3.x))/6.0;
			
			double RHM_y = (diff(q1.y, q2.y) + diff(q3.y, q4.y) + diff(q1.y, q3.y) + diff(q2.y, q4.y)
							+ diff(q1.y, q4.y) + diff(q2.y, q3.y))/6.0;
			
			double RHM_a = (diff(q1.a, q2.a) + diff(q3.a, q4.a) + diff(q1.a, q3.a) + diff(q2.a, q4.a)
							+ diff(q1.a, q4.a) + diff(q2.a, q3.a))/6.0;
			
			double RHM = 1-(Math.abs(RHM_x)+Math.abs(RHM_y)+Math.abs(RHM_a))/3;
			
			return new MetricResult[] {
					new MetricResult("Rhythm", "RHM", RHM),
					new MetricResult("X Rhythm", "RHM_x", RHM_x),
					new MetricResult("Y Rhythm", "RHM_y", RHM_y),
					new MetricResult("Area Rhythm", "RHM_a", RHM_a)
				};
		}
		return EMPTY_RESULT;
	}
	
	private double diff(double a, double b) {
		return Math.abs(a - b);
	}
	
	private double sum(double[] ds) {
		double sum = 0.0;
		for (double d : ds) {
			sum += d;
		}
		return sum;
	}
	
	private double max(double[] ds) {
		double max = Double.MIN_VALUE;
		for (double d : ds) {
			if(max < d) {
				max = d;
			}
		}
		return max;
	}
	
	private static class RhythmValues implements cz.vutbr.fit.dashapp.model.Cloneable {
		public double n;
		public double x;
		public double y;
		public double a;
		
		public RhythmValues() {
			this(0, 0, 0, 0);
		};

		public RhythmValues(double n, double x, double y, double a) {
			this.n = n;
			this.x = x;
			this.y = y;
			this.a = a;
		};
		
		@Override
		public Object copy() {
			RhythmValues copy = new RhythmValues();
			copy.n = n;
			copy.x = x;
			copy.y = y;
			copy.a = a;
			return copy;
		}
		
		public void normalize(double xx, double yy, double aa) {
			x = xx != 0 ? x/xx : 0;
			y = yy != 0 ? y/yy : 0;
			a = aa != 0 ? a/aa : 0;
		}
		
		@Override
		public String toString() {
			
			return "n = " + n + "\nx = " + x + "\ny = " + y + "\nw = " + a;
		}
		
	}

}
