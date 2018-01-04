package cz.vutbr.fit.dashapp.util.quadrant;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;

import java.awt.Rectangle;
import java.util.List;

import cz.vutbr.fit.dashapp.model.Constants.Quadrant;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;

/**
 * Extensible tool which performs specific tasks for every graphical element of dashboard in correspondence with quadrant.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class QuadrantResolver {
	
	public static final int BY_CENTER = 0;
	public static final int BY_AREA = 1;
	
	protected Quadrant q;
	
	protected double dx, dy, dx2, dy2;
	protected Rectangle rectangle;
	
	public QuadrantResolver() {
		// TODO Auto-generated constructor stub
	}
	
	public void perform(Dashboard dashboard, GEType[] types, int type, boolean excludeCenterLine) {
		
		double centerX = dashboard.halfSizeX();
		double centerY = dashboard.halfSizeY();
		
		List<GraphicalElement> children = dashboard.getChildren(types);
		for (GraphicalElement graphicalElement : children) {
			
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
				/*dx = graphicalElement.x-centerX;
				dy = graphicalElement.y-centerY;
				dx2 = graphicalElement.x2()-centerX;
				dy2 = graphicalElement.y2()-centerY;*/
				
				prePerform(graphicalElement);
				
				/*if(excludeCenterLine) {
					performQuadrants4(graphicalElement);
				} else {
					performQuadrants3(graphicalElement);
				}*/
				performQuadrants5(graphicalElement);
			}
			
			postPerform(graphicalElement);
		}
	}
	
	private void performQuadrants1(GraphicalElement graphicalElement) {
		if(dy <= 0) {
			if(dx <= 0) {
				q = Quadrant.I;
				performAllPre(graphicalElement);
				performI(graphicalElement);
				performAllPost(graphicalElement);
			}
			
			if(dx >= 0) {
				q = Quadrant.II;
				performAllPre(graphicalElement);
				performII(graphicalElement);
				performAllPost(graphicalElement);
			}
		}
		
		if(dy >= 0) {
			if(dx <= 0) {
				q = Quadrant.III;
				performAllPre(graphicalElement);
				performIII(graphicalElement);
				performAllPost(graphicalElement);
			}
			
			if(dx >= 0) {
				q = Quadrant.IV;
				performAllPre(graphicalElement);
				performIV(graphicalElement);
				performAllPost(graphicalElement);
			}
		}
	}
	
	private void performQuadrants2(GraphicalElement graphicalElement) {
		if(dy < 0) {
			if(dx < 0) {
				q = Quadrant.I;
				performAllPre(graphicalElement);
				performI(graphicalElement);
				performAllPost(graphicalElement);
			}
			
			if(dx > 0) {
				q = Quadrant.II;
				performAllPre(graphicalElement);
				performII(graphicalElement);
				performAllPost(graphicalElement);
			}
		}
		
		if(dy > 0) {
			if(dx < 0) {
				q = Quadrant.III;
				performAllPre(graphicalElement);
				performIII(graphicalElement);
				performAllPost(graphicalElement);
			}
			
			if(dx > 0) {
				q = Quadrant.IV;
				performAllPre(graphicalElement);
				performIV(graphicalElement);
				performAllPost(graphicalElement);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void performQuadrants3(GraphicalElement graphicalElement) {
		
		if(dy <= 0) {
			if(dx <= 0) {
				q = Quadrant.I;
				performAllPre(graphicalElement);
				performI(graphicalElement);
				performAllPost(graphicalElement);
			}
			
			if(dx2 >= 0) {
				q = Quadrant.II;
				performAllPre(graphicalElement);
				performII(graphicalElement);
				performAllPost(graphicalElement);
			}
		}
		
		if(dy2 >= 0) {
			if(dx <= 0) {
				q = Quadrant.III;
				performAllPre(graphicalElement);
				performIII(graphicalElement);
				performAllPost(graphicalElement);
			}
			
			if(dx2 >= 0) {
				q = Quadrant.IV;
				performAllPre(graphicalElement);
				performIV(graphicalElement);
				performAllPost(graphicalElement);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void performQuadrants4(GraphicalElement graphicalElement) {
		
		if(dy < 0) {
			if(dx < 0) {
				q = Quadrant.I;
				performAllPre(graphicalElement);
				performI(graphicalElement);
				performAllPost(graphicalElement);
			}
			
			if(dx2 > 0) {
				q = Quadrant.II;
				performAllPre(graphicalElement);
				performII(graphicalElement);
				performAllPost(graphicalElement);
			}
		}
		
		if(dy2 > 0) {
			if(dx < 0) {
				q = Quadrant.III;
				performAllPre(graphicalElement);
				performIII(graphicalElement);
				performAllPost(graphicalElement);
			}
			
			if(dx2 > 0) {
				q = Quadrant.IV;
				performAllPre(graphicalElement);
				performIV(graphicalElement);
				performAllPost(graphicalElement);
			}
		}
	}
	
	private void performQuadrants5(GraphicalElement graphicalElement) {
		
		rectangle = graphicalElement.getRectangle(Quadrant.I);
		if(rectangle.width > 0 && rectangle.height > 0) {
			q = Quadrant.I;
			performAllPre(graphicalElement);
			performI(graphicalElement);
			performAllPost(graphicalElement);
		}
		
		rectangle = graphicalElement.getRectangle(Quadrant.II);
		if(rectangle.width > 0 && rectangle.height > 0) {
			q = Quadrant.II;
			performAllPre(graphicalElement);
			performI(graphicalElement);
			performAllPost(graphicalElement);
		}
		
		rectangle = graphicalElement.getRectangle(Quadrant.III);
		if(rectangle.width > 0 && rectangle.height > 0) {
			q = Quadrant.III;
			performAllPre(graphicalElement);
			performII(graphicalElement);
			performAllPost(graphicalElement);	
		}
		
		rectangle = graphicalElement.getRectangle(Quadrant.IV);
		if(rectangle.width > 0 && rectangle.height > 0) {
			q = Quadrant.IV;
			performAllPre(graphicalElement);
			performIII(graphicalElement);
			performAllPost(graphicalElement);
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
	
	protected void performAllPre(GraphicalElement graphicalElement) {
		
	}
	
	protected void performAllPost(GraphicalElement graphicalElement) {
		
	}

}
