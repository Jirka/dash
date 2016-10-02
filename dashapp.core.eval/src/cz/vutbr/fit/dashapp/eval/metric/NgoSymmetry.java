package cz.vutbr.fit.dashapp.eval.metric;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.eval.util.QuadrantMap;
import cz.vutbr.fit.dashapp.eval.util.QuadrantResolver;
import cz.vutbr.fit.dashapp.eval.util.QuadrantUpdater;
import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

public class NgoSymmetry extends AbstractMetric implements IMetric {
	
	private QuadrantMap<SymmetryValues> quadrants;

	public NgoSymmetry(Dashboard dashboard, GEType[] types) {
		super(dashboard, types);
	}
	
	public NgoSymmetry(Dashboard dashboard, GEType[] types, String name) {
		super(dashboard, types, name);
	}

	@Override
	public String getInicials() {
		return "SYM";
	}
	
	public String[] getSubNames() {
		return new String[] { "", "V", "H", "R" };
	};

	@Override
	public Object measure() {
		// initialize map of quadrants containing symmetry values
		quadrants = new QuadrantMap<SymmetryValues>(new SymmetryValues());
		
		// calculate symmetry values for all quadrants
		new QuadrantResolver() {
			
			private double dfract;
			private double dsqrt;
			
			protected void prePerform(GraphicalElement graphicalElement) {
				dfract = Math.abs(dy/dx);
				dsqrt = Math.sqrt(dx*dx+dy*dy);
			};
			
			protected void performAll(GraphicalElement graphicalElement) {
				SymmetryValues value = quadrants.get(q);
				value.x += Math.abs(dx);
				value.y += Math.abs(dy);
				value.w += dashboard.width;
				value.h += dashboard.height;
				value.o += dfract;
				value.r += dsqrt;
			};
			
		}.perform(dashboard, getTypes(), QuadrantResolver.BY_CENTER, false);
		
		// normalize symmetry values in each quadrant
		quadrants = new QuadrantUpdater<SymmetryValues, Object>(quadrants, null, false) {
			
			SymmetryValues min = new SymmetryValues();
			SymmetryValues max = new SymmetryValues();
			
			protected void prePerform() {
				for (SymmetryValues act : map.values()) {
					min.x = Math.min(min.x, act.x);
					min.y = Math.min(min.y, act.y);
					min.w = Math.min(min.w, act.w);
					min.h = Math.min(min.h, act.h);
					min.o = Math.min(min.o, act.o);
					min.r = Math.min(min.r, act.r);
					
					max.x = Math.max(max.x, act.x);
					max.y = Math.max(max.y, act.y);
					max.w = Math.max(max.w, act.w);
					max.h = Math.max(max.h, act.h);
					max.o = Math.max(max.o, act.o);
					max.r = Math.max(max.r, act.r);
				}
			};

			protected SymmetryValues computeValue() {
				v.normalize(min, max);
				return v;
			}
			
		}.perform();
		
		// for shorter code
		SymmetryValues q1 = quadrants.get(Quadrant.I);
		SymmetryValues q2 = quadrants.get(Quadrant.II);
		SymmetryValues q3 = quadrants.get(Quadrant.III);
		SymmetryValues q4 = quadrants.get(Quadrant.IV);
		
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
		
		return new Object[] { SYM, SYM_v, SYM_h, SYM_r };
	}
	
	private double diff(double a, double b) {
		return Math.abs(a - b);
	}
	
	private static class SymmetryValues implements cz.vutbr.fit.dashapp.eval.util.Cloneable {
		public double x;
		public double y;
		public double w;
		public double h;
		public double o;
		public double r;
		
		public SymmetryValues() {
			this(0, 0, 0, 0, 0, 0);
		};

		public SymmetryValues(double x, double y, double w, double h, double o, double r) {
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
			copy.x = x;
			copy.y = y;
			copy.w = w;
			copy.h = h;
			copy.o = o;
			copy.r = r;
			return copy;
		}
		
		public void normalize(SymmetryValues min, SymmetryValues max) {
			x = normalize(x, min.x, max.x);
			y = normalize(y, min.y, max.y);
			w = normalize(w, min.w, max.w);
			h = normalize(h, min.h, max.h);
			o = normalize(o, min.o, max.o);
			r = normalize(r, min.r, max.r);
		}
		
		private double normalize(double act, double min, double max) {
			return (act-min)/(max-min);
		}
		
		@Override
		public String toString() {
			
			return "x = " + x + "\ny = " + y + "\nw = " + w + "\nh = " + h + "\no = " + o + "\nr = " + r;
		}
		
	}

}
