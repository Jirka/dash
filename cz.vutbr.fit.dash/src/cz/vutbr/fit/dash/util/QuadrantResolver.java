package cz.vutbr.fit.dash.util;

import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.Quadrant;

public abstract class QuadrantResolver {
	
	public static final int BY_CENTER = 0;
	public static final int BY_AREA = 1;
	
	protected Quadrant q;
	
	protected double dx, dy, dx2, dy2;
	
	public QuadrantResolver() {
		// TODO Auto-generated constructor stub
	}
	
	public void perform(Dashboard dashboard, int type, boolean excludeCenterLine) {
		
		double centerX = dashboard.ownCenterX();
		double centerY = dashboard.ownCenterY();
		
		for (GraphicalElement graphicalElement : dashboard.getGraphicalElements()) {
			
			if(type == BY_CENTER) {
				dx = graphicalElement.dx(centerX);
				dy = graphicalElement.dy(centerY);
				
				prePerform(graphicalElement);
				
				if(excludeCenterLine) {
					performQuadrants2(graphicalElement);
				} else {
					performQuadrants1(graphicalElement);
				}
			} else if(type == BY_AREA) {
				
				dx = graphicalElement.x-centerX;
				dy = graphicalElement.y-centerY;
				dx2 = graphicalElement.x2()-centerX;
				dy2 = graphicalElement.y2()-centerY;
				
				prePerform(graphicalElement);
				
				if(excludeCenterLine) {
					performQuadrants4(graphicalElement);
				} else {
					performQuadrants3(graphicalElement);
				}
			}
			
			postPerform(graphicalElement);
		}
	}
	
	private void performQuadrants1(GraphicalElement graphicalElement) {
		if(dy <= 0) {
			if(dx <= 0) {
				q = Quadrant.I;
				performAll(graphicalElement);
				performI(graphicalElement);
			}
			
			if(dx >= 0) {
				q = Quadrant.II;
				performAll(graphicalElement);
				performII(graphicalElement);
			}
		}
		
		if(dy >= 0) {
			if(dx <= 0) {
				q = Quadrant.III;
				performAll(graphicalElement);
				performIII(graphicalElement);
			}
			
			if(dx >= 0) {
				q = Quadrant.IV;
				performAll(graphicalElement);
				performIV(graphicalElement);
			}
		}
	}
	
	private void performQuadrants2(GraphicalElement graphicalElement) {
		if(dy < 0) {
			if(dx < 0) {
				q = Quadrant.I;
				performAll(graphicalElement);
				performI(graphicalElement);
			}
			
			if(dx > 0) {
				q = Quadrant.II;
				performAll(graphicalElement);
				performII(graphicalElement);
			}
		}
		
		if(dy > 0) {
			if(dx < 0) {
				q = Quadrant.III;
				performAll(graphicalElement);
				performIII(graphicalElement);
			}
			
			if(dx > 0) {
				q = Quadrant.IV;
				performAll(graphicalElement);
				performIV(graphicalElement);
			}
		}
	}
	
	private void performQuadrants3(GraphicalElement graphicalElement) {
		
		if(dy <= 0) {
			if(dx <= 0) {
				q = Quadrant.I;
				performAll(graphicalElement);
				performI(graphicalElement);
			}
			
			if(dx2 >= 0) {
				q = Quadrant.II;
				performAll(graphicalElement);
				performII(graphicalElement);
			}
		}
		
		if(dy2 >= 0) {
			if(dx <= 0) {
				q = Quadrant.III;
				performAll(graphicalElement);
				performIII(graphicalElement);
			}
			
			if(dx2 >= 0) {
				q = Quadrant.IV;
				performAll(graphicalElement);
				performIV(graphicalElement);
			}
		}
	}
	
	private void performQuadrants4(GraphicalElement graphicalElement) {
		
		if(dy < 0) {
			if(dx < 0) {
				q = Quadrant.I;
				performAll(graphicalElement);
				performI(graphicalElement);
			}
			
			if(dx2 > 0) {
				q = Quadrant.II;
				performAll(graphicalElement);
				performII(graphicalElement);
			}
		}
		
		if(dy > 0) {
			if(dx < 0) {
				q = Quadrant.III;
				performAll(graphicalElement);
				performIII(graphicalElement);
			}
			
			if(dx2 > 0) {
				q = Quadrant.IV;
				performAll(graphicalElement);
				performIV(graphicalElement);
			}
		}
	}
	
	protected void prePerform(GraphicalElement graphicalElement) {
		
	}
	
	protected void postPerform(GraphicalElement graphicalElement) {
		
	}
	
	protected void performI(GraphicalElement graphicalElement) {
		
	}
	
	protected void performII(GraphicalElement graphicalElement) {
		
	}
	
	protected void performIII(GraphicalElement graphicalElement) {
		
	}
	
	protected void performIV(GraphicalElement graphicalElement) {
		
	}
	
	protected void performAll(GraphicalElement graphicalElement) {
		
	}

}
