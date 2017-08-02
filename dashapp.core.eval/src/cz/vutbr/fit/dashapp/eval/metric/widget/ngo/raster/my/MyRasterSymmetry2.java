package cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.my;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.AbstractWidgetRasterMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.raster.RasterRatioCalculator;
import cz.vutbr.fit.dashapp.eval.util.QuadrantMap;
import cz.vutbr.fit.dashapp.eval.util.QuadrantResolver;
import cz.vutbr.fit.dashapp.image.colorspace.ColorSpace;
import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.Constants.Side;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class MyRasterSymmetry2 extends AbstractWidgetRasterMetric {
	
	public static final int SUM = 0;
	public static final int MAX = 1;
	
	private int normalizationType;
	
	public MyRasterSymmetry2(RasterRatioCalculator ratioCalculator, int normalizationType) {
		super(ratioCalculator);
		this.normalizationType = normalizationType;
	}
	
	@Override
	public String getName() {
		return (normalizationType == MAX ? "MAX_" : "SUM_") + super.getName();
	}

	private QuadrantMap<SymmetryValues> quadrants;

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		// initialize map of quadrants containing symmetry values
		quadrants = new QuadrantMap<SymmetryValues>(new SymmetryValues());
		
		IDashboardFile df = dashboard.getDashboardFile();
		if(df != null) {
			ColorSpace[][] matrix = ratioCalculator.prepareImage(df);
			
			// calculate symmetry values for all quadrants
			new QuadrantResolver() {
				
				//private double dfract;
				//private double dsqrt;
				private double _dx;
				private double _dy;
				
				protected void performI(GraphicalElement graphicalElement) {
					_dx = Math.abs(graphicalElement.depth(Side.LEFT));
					_dy = Math.abs(graphicalElement.depth(Side.UP));
				};
				
				protected void performII(GraphicalElement graphicalElement) {
					_dx = Math.abs(graphicalElement.depth(Side.RIGHT));
					_dy = Math.abs(graphicalElement.depth(Side.UP));
				};
				
				protected void performIII(GraphicalElement graphicalElement) {
					_dx = Math.abs(graphicalElement.depth(Side.LEFT));
					_dy = Math.abs(graphicalElement.depth(Side.DOWN));
				};
				
				protected void performIV(GraphicalElement graphicalElement) {
					_dx = Math.abs(graphicalElement.depth(Side.RIGHT));
					_dy = Math.abs(graphicalElement.depth(Side.DOWN));
				};
				
				protected void performAllPost(GraphicalElement graphicalElement) {
					SymmetryValues value = quadrants.get(q);
					value.n++;
					double ratio = ratioCalculator.getRatio(matrix, graphicalElement, q);
					value.x += _dx*ratio;
					value.y += _dy*ratio;
					value.w += rectangle.width*ratio;
					value.h += rectangle.height*ratio;
					if(_dx == 0) {
						value.o = Math.abs(_dy)*ratio;
					} else {
						value.o = Math.abs(_dy/_dx)*ratio;
					}
					value.r = Math.sqrt(_dx*_dx+_dy*_dy)*ratio;
					//value.c += ratioCalculator.getRatio(matrix, graphicalElement, q);
				};
				
			}.perform(dashboard, types, QuadrantResolver.BY_AREA, false);
			
			// normalize symmetry values in each quadrant
			/*quadrants = new QuadrantUpdater<SymmetryValues, Object>(quadrants, null, false) {
				
				protected SymmetryValues computeValue() {
					v.normalize(dashboard);
					return v;
				}
				
			}.perform();*/
			
			// for shorter code
			SymmetryValues q1 = quadrants.get(Quadrant.I);
			SymmetryValues q2 = quadrants.get(Quadrant.II);
			SymmetryValues q3 = quadrants.get(Quadrant.III);
			SymmetryValues q4 = quadrants.get(Quadrant.IV);
			
			if(normalizationType == SUM) {
				double xx = sum(new double[] { q1.x , q2.x , q3.x , q4.x });
				double yy = sum(new double[] { q1.y , q2.y , q3.y , q4.y });
				double ww = sum(new double[] { q1.w , q2.w , q3.w , q4.w });
				double hh = sum(new double[] { q1.h , q2.h , q3.h , q4.h });
				double oo = sum(new double[] { q1.o , q2.o , q3.o , q4.o });
				double rr = sum(new double[] { q1.r , q2.r , q3.r , q4.r });
				double cc = sum(new double[] { q1.c , q2.c , q3.c , q4.c });
				
				q1.normalize(xx, yy, ww, hh, oo, rr, cc);
				q2.normalize(xx, yy, ww, hh, oo, rr, cc);
				q3.normalize(xx, yy, ww, hh, oo, rr, cc);
				q4.normalize(xx, yy, ww, hh, oo, rr, cc);
			} else if(normalizationType == MAX) {
				double xx = max(new double[] { q1.x , q2.x , q3.x , q4.x });
				double yy = max(new double[] { q1.y , q2.y , q3.y , q4.y });
				double ww = max(new double[] { q1.w , q2.w , q3.w , q4.w });
				double hh = max(new double[] { q1.h , q2.h , q3.h , q4.h });
				double oo = max(new double[] { q1.o , q2.o , q3.o , q4.o });
				double rr = max(new double[] { q1.r , q2.r , q3.r , q4.r });
				double cc = max(new double[] { q1.c , q2.c , q3.c , q4.c });
				
				q1.normalize(xx, yy, ww, hh, oo, rr, cc);
				q2.normalize(xx, yy, ww, hh, oo, rr, cc);
				q3.normalize(xx, yy, ww, hh, oo, rr, cc);
				q4.normalize(xx, yy, ww, hh, oo, rr, cc);
			}
			
			double SYM_v, SYM_h, SYM_r;
			SYM_v = (diff(q1.x, q2.x) + diff(q3.x, q4.x) + diff(q1.y, q2.y) + diff(q3.y, q4.y)
						+ diff(q1.h, q2.h) + diff(q3.h, q4.h) + diff(q1.w, q2.w) + diff(q3.w, q4.w)
						+ diff(q1.o, q2.o) + diff(q3.o, q4.o) + diff(q1.r, q2.r) + diff(q3.r, q4.r))/12.0;

			SYM_h = (diff(q1.x, q3.x) + diff(q2.x, q4.x) + diff(q1.y, q3.y) + diff(q2.y, q4.y)
						+ diff(q1.h, q3.h) + diff(q2.h, q4.h) + diff(q1.w, q3.w) + diff(q2.w, q4.w)
						+ diff(q1.o, q3.o) + diff(q2.o, q4.o) + diff(q1.r, q3.r) + diff(q2.r, q4.r))/12.0;
			
			SYM_r = (diff(q1.x, q4.x) + diff(q2.x, q3.x) + diff(q1.y, q4.y) + diff(q2.y, q3.y)
						+ diff(q1.h, q4.h) + diff(q2.h, q3.h) + diff(q1.w, q4.w) + diff(q2.w, q3.w)
						+ diff(q1.o, q4.o) + diff(q2.o, q3.o) + diff(q1.r, q4.r) + diff(q2.r, q3.r))/12.0;
			
			double SYM = 1-(Math.abs(SYM_v)+Math.abs(SYM_h)+Math.abs(SYM_r))/3;
			
			return new MetricResult[] {
					new MetricResult("Symmetry", "SYM", SYM),
					new MetricResult("Vertical Symmetry", "SYM_v", SYM_v),
					new MetricResult("Horizontal Symmetry", "SYM_h", SYM_h),
					new MetricResult("Radial Symmetry", "SYM_r", SYM_r)
			};
		}
		
		return EMPTY_RESULT;
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

	private double diff(double a, double b) {
		return Math.abs(a - b);
	}
	
	private static class SymmetryValues implements cz.vutbr.fit.dashapp.eval.util.Cloneable {
		public double n;
		public double x;
		public double y;
		public double w;
		public double h;
		public double o;
		public double r;
		public double c;
		
		public SymmetryValues() {
			this(0, 0, 0, 0, 0, 0, 0, 0);
		};

		public SymmetryValues(double n, double x, double y, double w, double h, double o, double r, double c) {
			this.n = n;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.o = o;
			this.r = r;
			this.c = c;
		};
		
		@Override
		public Object copy() {
			SymmetryValues copy = new SymmetryValues();
			copy.n = n;
			copy.x = x;
			copy.y = y;
			copy.w = w;
			copy.h = h;
			copy.o = o;
			copy.r = r;
			copy.c = c;
			return copy;
		}
		
		/*public void normalize(SymmetryValues min, SymmetryValues max) {
			x = normalize(x, min.x, max.x);
			y = normalize(y, min.y, max.y);
			w = normalize(w, min.w, max.w);
			h = normalize(h, min.h, max.h);
			o = normalize(o, min.o, max.o);
			r = normalize(r, min.r, max.r);
		}*/
		
		/*public void normalize(Dashboard d) {
			double dx = d.halfSizeX();
			double dy = d.halfSizeY();
			double dn = d.n(GEType.ALL_TYPES);
			x = normalize(x, 0, dx*dn);
			y = normalize(y, 0, dy*dn);
			w = normalize(w, 0, d.width*dn);
			h = normalize(h, 0, d.height*dn);
			o = normalize(o, 0, dy*n);
			r = normalize(r, 0, Math.sqrt(dx*dx+dy*dy)*dn);
		}*/
		
		public void normalize(double xx, double yy, double ww, double hh, double oo, double rr, double cc) {
			x = xx != 0 ? x/xx : 0;
			y = yy != 0 ? y/yy : 0;
			w = ww != 0 ? w/ww : 0;
			h = hh != 0 ? h/hh : 0;
			o = oo != 0 ? o/oo : 0;
			r = rr != 0 ? r/rr : 0;
			c = cc != 0 ? c/cc : 0;
		}
		
		/*private double normalize(double act, double min, double max) {
			double d = max-min;
			if(d == 0) {
				return 0;
			}
			return (act-min)/d;
		}*/
		
		@Override
		public String toString() {
			
			return "n = " + n + "\nx = " + x + "\ny = " + y + "\nw = " + w + "\nh = " + h + "\no = " + o + "\nr = " + r + "\nc = " + c;
		}
		
	}

}
