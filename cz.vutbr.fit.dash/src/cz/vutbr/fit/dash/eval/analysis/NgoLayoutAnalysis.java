package cz.vutbr.fit.dash.eval.analysis;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;

public class NgoLayoutAnalysis implements IAnalysis {
	
	public static final String NAME = "Ngo Layout Analyses";
	private Dashboard dashboard;
	
	public NgoLayoutAnalysis(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public String getName() {
		return NAME;
	}
	
	public static double getVerticalBalance(Dashboard dashboard) {
		// vertical center of dashboard
		double center = dashboard.halfSizeX();
		// initialize weights
		double weightLeft = 0.0;
		double weightRight = 0.0;
		// count weights
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			double distanceCenter = graphicalElement.centerX() - center;
			if(distanceCenter < 0) {
				// left side
				weightLeft += graphicalElement.area()*(-distanceCenter);
			} else if(distanceCenter > 0) {
				// ride side
				weightRight += graphicalElement.area()*(distanceCenter);
			}
		}
		// return vertical balance
		return (weightLeft-weightRight)/Math.max(Math.abs(weightLeft), Math.abs(weightRight));
	}
	
	public static double getHorizontalBalance(Dashboard dashboard) {
		// horizontal center of dashboard
		double center = dashboard.halfSizeY();
		// initialize weights
		double weightTop = 0.0;
		double weightBottom = 0.0;
		// count weights
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			double distanceCenter = graphicalElement.centerY() - center;
			if(distanceCenter < 0) {
				// top side
				weightTop += graphicalElement.area()*(-distanceCenter);
			} else if(distanceCenter > 0) {
				// bottom side
				weightBottom += graphicalElement.area()*(distanceCenter);
			}
		}
		// return horizontal balance
		return (weightTop-weightBottom)/Math.max(Math.abs(weightTop), Math.abs(weightBottom));
	}
	
	public static double getBalance(Dashboard dashboard) {
		return 1-(Math.abs(getVerticalBalance(dashboard))+Math.abs(getHorizontalBalance(dashboard)))/2.0;
	}
	
	public static double getEquilibriumX(Dashboard dashboard) {
		double centerX = dashboard.width/2.0;
		double EM_x = 0.0;
		int areas = 0;
		
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			int area = graphicalElement.area();
			EM_x += area*(graphicalElement.centerX()-centerX);
			areas += area;
		}
		
		int elemCount = dashboard.n(GEType.ALL_TYPES);
		return 2*EM_x/(elemCount*dashboard.width*areas);
	}
	
	public static double getEquilibriumY(Dashboard dashboard) {
		double centerY = dashboard.height/2.0;
		double EM_y = 0.0;
		int areas = 0;
		
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			int area = graphicalElement.area();
			EM_y += area*(graphicalElement.centerY()-centerY);
			areas += area;
		}
		
		int elemCount = dashboard.n(GEType.ALL_TYPES);
		return 2*EM_y/(elemCount*dashboard.height*areas);
	}
	
	public static double getEquilibrium(Dashboard dashboard) {
		
		double centerX = dashboard.width/2.0;
		double centerY = dashboard.height/2.0;
		
		double EM_x = 0.0;
		double EM_y = 0.0;
		int areas = 0;
		
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			int area = graphicalElement.area();
			EM_x += area*(graphicalElement.centerX()-centerX);
			EM_y += area*(graphicalElement.centerY()-centerY);
			areas += area;
		}
		
		int elemCount = dashboard.n(GEType.ALL_TYPES);
		
		EM_x = 2*EM_x/(elemCount*dashboard.width*areas);
		EM_y = 2*EM_y/(elemCount*dashboard.height*areas);
		
		return 1-(Math.abs(EM_x)+Math.abs(EM_y))/2.0;
	}
	
	public static double getVerticalSymetry(Dashboard dashboard) {
		return 0.0; 
	}
	
	public static double[] getSymetry(Dashboard dashboard) {
		
		double centerX = dashboard.halfSizeX();
		double centerY = dashboard.halfSizeY();
		
		double X_UL = 0.0, X_UR = 0.0, X_LL = 0.0, X_LR = 0.0;
		double Y_UL = 0.0, Y_UR = 0.0, Y_LL = 0.0, Y_LR = 0.0;
		double H_UL = 0.0, H_UR = 0.0, H_LL = 0.0, H_LR = 0.0;
		double B_UL = 0.0, B_UR = 0.0, B_LL = 0.0, B_LR = 0.0;
		double O_UL = 0.0, O_UR = 0.0, O_LL = 0.0, O_LR = 0.0;
		double R_UL = 0.0, R_UR = 0.0, R_LL = 0.0, R_LR = 0.0;
		
		double d_x, d_y, d_fract, d_sqrt;
		
		/*double X_min = 1.0, Y_min = 1.0, H_min = 1.0, B_min = 1.0, O_min = 1.0, R_min = 1.0;
		double X_max = dashboard.width/2, Y_max = dashboard.height/2, H_max = dashboard.height, B_max = dashboard.width,
				O_max = (Y_max)/(X_min), R_max = Math.sqrt(X_max*X_max+Y_max*Y_max);*/
		
		double X_min = 0.5, Y_min = 0.5, H_min = 1.0, B_min = 1.0;
		double X_max = dashboard.width/2, Y_max = dashboard.height/2, H_max = dashboard.height, B_max = dashboard.width;
		double O_min = (Y_min)/(X_max), R_min = Math.sqrt(X_min*X_min+Y_min*Y_min);
		double O_max = (Y_max)/(X_min), R_max = Math.sqrt(X_max*X_max+Y_max*Y_max);
		
		
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			d_x = graphicalElement.dx(centerX);
			d_y = graphicalElement.dy(centerY);
			d_fract = Math.abs(d_y/d_x);
			d_sqrt = Math.sqrt(d_x*d_x+d_y*d_y);
			
			if(d_x < 0) {
				if(d_y < 0) {
					X_UL += Math.abs(d_x);
					Y_UL += Math.abs(d_y);
					H_UL += dashboard.height;
					B_UL += dashboard.width;
					O_UL += d_fract;
					R_UL += d_sqrt;
				}
				if(d_y > 0) {
					X_UR += Math.abs(d_x);
					Y_UR += d_y;
					H_UR += dashboard.height;
					B_UR += dashboard.width;
					O_UL += d_fract;
					R_UL += d_sqrt;
				}
			}
			
			if(d_x > 0) {
				if(d_y < 0) {
					X_LL += d_x;
					Y_LL += Math.abs(d_y);
					H_LL += dashboard.height;
					B_LL += dashboard.width;
					O_UL += d_fract;
					R_UL += d_sqrt;
				}
				if(d_y > 0) {
					X_LR += d_x;
					Y_LR += d_y;
					H_LR += dashboard.height;
					B_LR += dashboard.width;
					O_UL += d_fract;
					R_UL += d_sqrt;
				}
			}
		}
		
		X_UL = normalize(X_UL, X_min, X_max);
		X_UR = normalize(X_UR, X_min, X_max);
		X_LL = normalize(X_LL, X_min, X_max);
		X_LR = normalize(X_LR, X_min, X_max);

		Y_UL = normalize(Y_UL, Y_min, Y_max);
		Y_UR = normalize(Y_UR, Y_min, Y_max);
		Y_LL = normalize(Y_LL, Y_min, Y_max);
		Y_LR = normalize(Y_LR, Y_min, Y_max);

		H_UL = normalize(H_UL, H_min, H_max);
		H_UR = normalize(H_UR, H_min, H_max);
		H_LL = normalize(H_LL, H_min, H_max);
		H_LR = normalize(H_LR, H_min, H_max);

		B_UL = normalize(B_UL, B_min, B_max);
		B_UR = normalize(B_UR, B_min, B_max);
		B_LL = normalize(B_LL, B_min, B_max);
		B_LR = normalize(B_LR, B_min, B_max);

		O_UL = normalize(O_UL, O_min, O_max);
		O_UR = normalize(O_UR, O_min, O_max);
		O_LL = normalize(O_LL, O_min, O_max);
		O_LR = normalize(O_LR, O_min, O_max);

		R_UL = normalize(R_UL, R_min, R_max);
		R_UR = normalize(R_UR, R_min, R_max);
		R_LL = normalize(R_LL, R_min, R_max);
		R_LR = normalize(R_LR, R_min, R_max);
		
		double SYM_v = (Math.abs(X_UL - X_UR) + Math.abs(X_LL - X_LR) + Math.abs(Y_UL - Y_UR) + Math.abs(Y_LL - Y_LR)
		 + Math.abs(H_UL - H_UR) + Math.abs(H_LL - H_LR) + Math.abs(B_UL - B_UR) + Math.abs(B_LL - B_LR)
		 + Math.abs(O_UL - O_UR) + Math.abs(O_LL - O_LR) + Math.abs(R_UL - R_UR) + Math.abs(R_LL - R_LR))/12.0;
		
		double SYM_h = (Math.abs(X_UL - X_LL) + Math.abs(X_UR - X_LR) + Math.abs(Y_UL - Y_LL) + Math.abs(Y_UR - Y_LR)
		 + Math.abs(H_UL - H_LL) + Math.abs(H_UR - H_LR) + Math.abs(B_UL - B_LL) + Math.abs(B_UR - B_LR)
		 + Math.abs(O_UL - O_LL) + Math.abs(O_UR - O_LR) + Math.abs(R_UL - R_LL) + Math.abs(R_UR - R_LR))/12.0;
		
		double SYM_r = (Math.abs(X_UL - X_LR) + Math.abs(X_UR - X_LL) + Math.abs(Y_UL - Y_LR) + Math.abs(Y_UR - Y_LL)
		 + Math.abs(H_UL - H_LR) + Math.abs(H_UR - H_LL) + Math.abs(B_UL - B_LR) + Math.abs(B_UR - B_LL)
		 + Math.abs(O_UL - O_LR) + Math.abs(O_UR - O_LL) + Math.abs(R_UL - R_LR) + Math.abs(R_UR - R_LL))/12.0;
		
		double SYM = 1-(Math.abs(SYM_v)+Math.abs(SYM_h)+Math.abs(SYM_r))/3;
		
		return new double[] { SYM, SYM_v, SYM_h, SYM_r };
	}
	
	public static double normalize(double x, double min, double max) {
		return (x-min)/(max-min);
	}
	
	public static double getSequence(Dashboard dashboard) {
		
		double centerX = dashboard.halfSizeX();
		double centerY = dashboard.halfSizeY();
		
		double wA = 0.0, wB = 0.0, wC = 0.0, wD = 0.0;
		double dx, dy;
		int area;
		
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			dx = graphicalElement.dx(centerX);
			dy = graphicalElement.dy(centerY);
			area = graphicalElement.area();
			
			if(dx < 0) {
				if(dy < 0) {
					wA += area;
				} else if(dy > 0) {
					wB += area;
				}
			} else if(dx > 0) {
				if(dy < 0) {
					wC += area;
				} else if(dy > 0) {
					wD += area;
				}
			}
			
			wA*=4;wB*=3;wC*=2;wD*=1;
		}
		
		double SQM = Math.abs(4-compareQuadrants(wA, wB, wC, wD));
		SQM += Math.abs(3-compareQuadrants(wB, wA, wC, wD));
		SQM += Math.abs(2-compareQuadrants(wC, wA, wB, wD));
		SQM += Math.abs(1-compareQuadrants(wD, wA, wB, wC));
		SQM = 1-SQM/8;
		
		return SQM;
	}
	
	private static int compareQuadrants(double q, double a, double b, double c) {
		int result = 1;
		if(q > a) {
			result++;
		}
		if(q > b) {
			result++;
		}
		if(q > c) {
			result++;
		}
		return result;
	}
	
	public static int getLayoutWidth(Dashboard dashboard) {
		// calculate width and height of layout
		int minX = dashboard.width, maxX = 0;
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			if(minX > graphicalElement.x) {
				minX = graphicalElement.x;
			}
			if(maxX < graphicalElement.x+graphicalElement.width) {
				maxX = graphicalElement.x+graphicalElement.width;
			}
		}
		return maxX-minX;
	}
	
	public static int getLayoutHeight(Dashboard dashboard) {
		int minY = dashboard.height, maxY = 0;
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			if(minY > graphicalElement.y) {
				minY = graphicalElement.y;
			}
			if(maxY < graphicalElement.y+graphicalElement.height) {
				maxY = graphicalElement.y+graphicalElement.height;
			}
		}
		
		return maxY-minY;
	}
	
	public static int getLayoutArea(Dashboard dashboard) {
		return getLayoutWidth(dashboard)*getLayoutHeight(dashboard);
	}
	
	public static double getCohesion(Dashboard dashboard) {
		
		// calculate width and height of layout
		double ratioLaout = (((double) getLayoutHeight(dashboard))/((double) getLayoutWidth(dashboard)));
		double CM_fl = ratioLaout/
				(((double) dashboard.height)/((double)dashboard.width));
		if(CM_fl > 1.0) {
			CM_fl = 1/CM_fl;
		}
		
		double CM_lo = 0.0, ti;
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) { 
			ti = (((double) graphicalElement.height)/ ((double) graphicalElement.width)) / ratioLaout;
			if(ti > 1.0) {
				ti = 1/ti;
			}
			CM_lo+=ti;
		}
		CM_lo = CM_lo/dashboard.n(GEType.ALL_TYPES);
		
		return (Math.abs(CM_fl)+Math.abs(CM_lo))/2.0;
	}
	
	public static int getNumberOfSizes(Dashboard dashboard) {
		Set<Point> sizes = new HashSet<Point>();
		Point p;
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			p = new Point(graphicalElement.width, graphicalElement.height);
			if(!(sizes.contains(p))) {
				sizes.add(p);
			}
		}
		return sizes.size();
	}
	
	public static int getElementsArea(Dashboard dashboard) {
		int areas = 0;
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			areas += graphicalElement.area();
		}
		return areas;
	}
	
	public static double getUnity(Dashboard dashboard) {
		
		int areas = getElementsArea(dashboard);
		double UM_form = 1 - (((double)(getNumberOfSizes(dashboard)-1))/dashboard.n(GEType.ALL_TYPES));
		double UM_space = 1 - ((double) (getLayoutArea(dashboard)-areas))/(dashboard.area()-areas);
		
		double x = 0.32515;
		x = x*2-Math.abs(UM_space);
		x = -x+1;
		x = x*dashboard.n(GEType.ALL_TYPES)+1;
		
		return (Math.abs(UM_form)+Math.abs(UM_space))/2;
	}
	
	public static double getProportion(Dashboard dashboard) {
		
		double pi, min, min2;
		double[] constants = {1, 1/1.414, 1/1.618, 1/1.732, 1.0/2};
		
		// proportion of objects
		double PM_object = 0.0;
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			pi = ((double) graphicalElement.height)/graphicalElement.width;
			if(pi > 1.0) {
				pi = 1/pi;
			}
			min = 0.5;
			for (double d : constants) {
				min2 = Math.abs(d-pi);
				if(min2 < min) {
					min = min2;
				}
			}
			PM_object += 1-(min/0.5);
		}
		PM_object *= 1.0/dashboard.n(GEType.ALL_TYPES);
		
		// proportion of layout
		pi = ((double) getLayoutHeight(dashboard))/getLayoutWidth(dashboard);
		if(pi > 1.0) {
			pi = 1.0/pi;
		}
		min = 0.5;
		for (double d : constants) {
			min2 = Math.abs(d-pi);
			if(min2 < min) {
				min = min2;
			}
		}
		
		double PM_layout = 1-(min/0.5);
		
		return (Math.abs(PM_object)+Math.abs(PM_layout))/2;
	}
	
	public static int getHAP(Dashboard dashboard) {
		Set<Integer> listX = new HashSet<Integer>();
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			if(!listX.contains(graphicalElement.x)) {
				listX.add(graphicalElement.x);
			}
		}
		return listX.size();
	}
	
	public static int getVAP(Dashboard dashboard) {
		Set<Integer> listY = new HashSet<Integer>();
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			if(!listY.contains(graphicalElement.y)) {
				listY.add(graphicalElement.y);
			}
		}
		return listY.size();
	}
	
	public static double getSimplicity(Dashboard dashboard) {
		return 3.0/(getHAP(dashboard)+getVAP(dashboard)+dashboard.n(GEType.ALL_TYPES));
	}
	
	public static double getDensity(Dashboard dashboard) {
		return 1.0-2*Math.abs(0.5-(((double) getElementsArea(dashboard))/dashboard.area()));
	}
	
	public static double getRegularity(Dashboard dashboard) {
		double RM_alignment, RM_spacing;
		int n_spacing = 4; // TODO
		
		if(dashboard.n(GEType.ALL_TYPES) == 1) {
			RM_alignment = RM_spacing = 1;
		} else {
			RM_alignment = 1-(((double) getVAP(dashboard)+getHAP(dashboard))/(2*dashboard.n(GEType.ALL_TYPES)));
			RM_spacing = 1-(((double) n_spacing-1)/(2*(dashboard.n(GEType.ALL_TYPES)-1)));
			System.out.println(RM_alignment);
			System.out.println(RM_spacing);
		}
		
		/*double x = 0.37500;
		//x = (2*x)-Math.abs(RM_alignment);
		//System.out.println(RM_spacing);
		x = ((-(x-1))*(2*dashboard.n()-1))+1;
		System.out.println("n_spacing:" x);*/
		
		return (Math.abs(RM_alignment)+Math.abs(RM_spacing))/2;
	}
	
	public static double getEconomy(Dashboard dashboard) {
		return 1.0/getNumberOfSizes(dashboard);
	}
	
	// TODO
	public static int fact(int n) {
		int result = 1;
		for (int i = 1; i <= n; i++) {
			result = result * i;
		}
		return result;
	}
	
	public static double getHomogenity(Dashboard dashboard) {
		double N = fact(dashboard.n(GEType.ALL_TYPES));
		
		double centerX = dashboard.halfSizeX();
		double centerY = dashboard.halfSizeY();
		double dx, dy;
		int countA = 0, countB = 0, countC = 0, countD = 0;
		for (GraphicalElement graphicalElement : dashboard.getChildren(GEType.ALL_TYPES)) {
			dx = graphicalElement.dx(centerX);
			dy = graphicalElement.dy(centerY);
			if(dx <= 0) {
				if(dy <= 0) {
					countA++;
				}
				
				if(dy >= 0) {
					countB++;
				}
			}
			
			if(dx >= 0) {
				if(dy <= 0) {
					countC++;
				}
				
				if(dy >= 0) {
					countD++;
				}
			}
		}
		
		double W = ((double) N)/(fact(countA)*fact(countB)*fact(countC)*fact(countD));
		double W_max = ((double) N)/Math.pow((((double) N)/24), 4);
		
		/*System.out.println("n = " + dashboard.n());
		System.out.println("n! = " + N);
		System.out.println("W_max = " + W_max);
		System.out.println("counts = " + countA + " " + countB + " " + countC + " " + countD + " ");
		System.out.println("counts! = " + fact(countA) + " " + fact(countB) + " " + fact(countC) + " " + fact(countD) + " ");
		System.out.println("counts! = " + (fact(countA)*fact(countB)*fact(countC)*fact(countD)));
		System.out.println("W = " + W);
		double x = 0.00463*W_max; 
		System.out.println("expected W = " + x);
		System.out.println("expected counts! = " + N/x);*/
		
		return W/W_max;
	}

	public String analyse() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("===== BALANCE =====\n\n");
		
		buffer.append("BM_vertical = ");
		buffer.append(getVerticalBalance(dashboard));
		buffer.append("\n");
		
		buffer.append("BM_horizontal = ");
		buffer.append(getHorizontalBalance(dashboard));
		buffer.append("\n");
		
		buffer.append("BM = ");
		buffer.append(getBalance(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== EQUILIBRIUM =====\n\n");
		
		buffer.append("EM_x = ");
		buffer.append(getEquilibriumX(dashboard));
		buffer.append("\n");
		
		buffer.append("EM_y = ");
		buffer.append(getEquilibriumY(dashboard));
		buffer.append("\n");
		
		buffer.append("EM = ");
		buffer.append(getEquilibrium(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== SYMETRY =====\n\n");
		
		double[] SYM = getSymetry(dashboard);
		buffer.append("SYM_vertical = ");
		buffer.append(SYM[1]);
		buffer.append("\n");
		
		buffer.append("SYM_horizontal = ");
		buffer.append(SYM[2]);
		buffer.append("\n");
		
		buffer.append("SYM_radial = ");
		buffer.append(SYM[3]);
		buffer.append("\n");
		
		buffer.append("SYM = ");
		buffer.append(SYM[0]);
		buffer.append("\n\n");
		
		buffer.append("===== SEQUENCE =====\n\n");
		
		buffer.append("SQM = ");
		buffer.append(getSequence(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== COHESION =====\n\n");
		
		buffer.append("CM = ");
		buffer.append(getCohesion(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== UNITY =====\n\n");
		
		buffer.append("UM = ");
		buffer.append(getUnity(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== PROPORTION =====\n\n");
		
		buffer.append("PM = ");
		buffer.append(getProportion(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== SIMPLICITY =====\n\n");
		
		buffer.append("SMM = ");
		buffer.append(getSimplicity(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== DENSITY =====\n\n");
		
		buffer.append("DM = ");
		buffer.append(getDensity(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== REGULARTITY =====\n\n");
		
		buffer.append("RM = ");
		buffer.append(getRegularity(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== ECONOMY =====\n\n");
		
		buffer.append("ECM = ");
		buffer.append(getEconomy(dashboard));
		buffer.append("\n\n");
		
		buffer.append("===== HOMOGENEITY =====\n\n");
		
		buffer.append("HM = ");
		buffer.append(getHomogenity(dashboard));
		buffer.append("\n\n");
		
		return buffer.toString();
	}

}
