package cz.vutbr.fit.dashapp.eval.metric.widget.ngo;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.widget.AbstractWidgetMetric;
import cz.vutbr.fit.dashapp.eval.util.QuadrantMap;
import cz.vutbr.fit.dashapp.eval.util.QuadrantResolver;
import cz.vutbr.fit.dashapp.eval.util.QuadrantUpdater;
import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoSymmetry extends AbstractWidgetMetric {
	
	private QuadrantMap<SymmetryValues> quadrants;

	@Override
	public MetricResult[] measure(Dashboard dashboard, GEType[] types) {
		// initialize map of quadrants containing symmetry values
		quadrants = new QuadrantMap<SymmetryValues>(new SymmetryValues());
		
		// calculate symmetry values for all quadrants
		new QuadrantResolver() {
			
			private double dfract;
			private double dsqrt;
			
			protected void prePerform(GraphicalElement graphicalElement) {
				if(dx == 0) {
					dfract = Math.abs(dy);
				} else {
					dfract = Math.abs(dy/dx);
				}
				dsqrt = Math.sqrt(dx*dx+dy*dy);
			};
			
			protected void performAll(GraphicalElement graphicalElement) {
				SymmetryValues value = quadrants.get(q);
				value.n++;
				value.x += Math.abs(dx);
				value.y += Math.abs(dy);
				value.w += graphicalElement.width;
				value.h += graphicalElement.height;
				value.o += dfract;
				value.r += dsqrt;
			};
			
		}.perform(dashboard, types, QuadrantResolver.BY_CENTER, false);
		
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
		
		double xx = q1.x + q2.x + q3.x + q4.x;
		double yy = q1.y + q2.y + q3.y + q4.y;
		double ww = q1.w + q2.w + q3.w + q4.w;
		double hh = q1.h + q2.h + q3.h + q4.h;
		double oo = q1.o + q2.o + q3.o + q4.o;
		double rr = q1.r + q2.r + q3.r + q4.r;
		
		q1.normalize(xx, yy, ww, hh, oo, rr);
		q2.normalize(xx, yy, ww, hh, oo, rr);
		q3.normalize(xx, yy, ww, hh, oo, rr);
		q4.normalize(xx, yy, ww, hh, oo, rr);
		
		double SYM_v = (diff(q1.x, q2.x) + diff(q3.x, q4.x) + diff(q1.y, q2.y) + diff(q3.y, q4.y)
					+ diff(q1.h, q2.h) + diff(q3.h, q4.h) + diff(q1.w, q2.w) + diff(q3.w, q4.w)
					+ diff(q1.o, q2.o) + diff(q3.o, q4.o) + diff(q1.r, q2.r) + diff(q3.r, q4.r))/12.0;
		
		double SYM_h = (diff(q1.x, q3.x) + diff(q2.x, q4.x) + diff(q1.y, q3.y) + diff(q2.y, q4.y)
					+ diff(q1.h, q3.h) + diff(q2.h, q4.h) + diff(q1.w, q3.w) + diff(q2.w, q4.w)
					+ diff(q1.o, q3.o) + diff(q2.o, q4.o) + diff(q1.r, q3.r) + diff(q2.r, q4.r))/12.0;
		
		double SYM_r = (diff(q1.x, q4.x) + diff(q2.x, q3.x) + diff(q1.y, q4.y) + diff(q2.y, q3.y)
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
		
		public SymmetryValues() {
			this(0, 0, 0, 0, 0, 0, 0);
		};

		public SymmetryValues(double n, double x, double y, double w, double h, double o, double r) {
			this.n = n;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.o = o;
			this.r = r;
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
		
		public void normalize(double xx, double yy, double ww, double hh, double oo, double rr) {
			x = xx != 0 ? x/xx : 0;
			y = yy != 0 ? y/yy : 0;
			w = ww != 0 ? w/ww : 0;
			h = hh != 0 ? h/hh : 0;
			o = oo != 0 ? o/oo : 0;
			r = rr != 0 ? r/rr : 0;
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
			
			return "n = " + n + "\nx = " + x + "\ny = " + y + "\nw = " + w + "\nh = " + h + "\no = " + o + "\nr = " + r;
		}
		
	}

}
