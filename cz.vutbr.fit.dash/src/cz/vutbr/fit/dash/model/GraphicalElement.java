package cz.vutbr.fit.dash.model;

import org.simpleframework.xml.Element;

import cz.vutbr.fit.dash.model.DashAppModel.PropertyKind;

@Element(name="graphicalElement")
public class GraphicalElement {
	
	public static enum Type {
		TOOLBAR, BUTTON, HEADER, CHART, LABEL, DECORATION
	}
	
	@Element
	public int x;
	
	@Element
	public int y;
	
	@Element
	public int width;
	
	@Element
	public int height;
	
	@Element(required=false)
	public Type type = Type.CHART;
	
	Dashboard dashboard;
	
	public GraphicalElement() {
		// used by deserialization
	}
	
	public GraphicalElement(Dashboard dashboard, int x, int y, int width, int height) {
		this(dashboard, x, y);
		this.width = width;
		this.height = height;
	}
	
	public GraphicalElement(Dashboard dashboard, int x, int y) {
		this.dashboard = dashboard;
		this.x = x;
		this.y = y;
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public GraphicalElement copy() {
		return new GraphicalElement(dashboard, x, y, width, height);
	}

	public void restore(GraphicalElement backupElement) {
		update(backupElement.x, backupElement.y, backupElement.width, backupElement.height, false);
	}
	
	public void update(Type type) {
		if(this.type != type) {
			GraphicalElement copy = copy();
			this.type = type;
			dashboard.getModel().firePropertyChange(new PropertyChangeEvent(PropertyKind.GRAPHICAL_ELEMENT, copy, this));
		}
	}
	
	public void update(int x, int y, int width, int height, boolean convertToRelative) {
		GraphicalElement copy = null;
		if(convertToRelative) {
			x = toRelativeX(x);
			y = toRelativeY(y);
		}
		if(this.x != x) {
			copy = copy();
			this.x = x;
		}
		if(this.y != y) {
			if(copy == null) copy = copy();
			this.y = y;
		}
		if(this.width != width) {
			if(copy == null) copy = copy();
			this.width = width;
		}
		if(this.height != height) {
			if(copy == null) copy = copy();
			this.height = height;
		}
		if(copy != null) {
			dashboard.getModel().firePropertyChange(new PropertyChangeEvent(PropertyKind.GRAPHICAL_ELEMENT, copy, this));
		}
	}
	
	public int length(int dimension) {
		if(dimension == Constants.X) {
			return this.width;
		} else if(dimension == Constants.Y) {
			return this.height;
		}
		return -1;
	}
	
	public int toRelative(int p, int dimension) {
		if(dimension == Constants.X) {
			return toRelativeX(p);
		} else if(dimension == Constants.Y) {
			return toRelativeY(p);
		}
		return -1;
	}
	
	public int toRelativeY(int y) {
		return dashboard == this ? y : y-dashboard.y;
	}
	
	public int toRelativeX(int x) {
		return dashboard == this ? x : x-dashboard.x;
	}
	
	public int toAbsolute(int p, int dimension) {
		if(dimension == Constants.X) {
			return toAbsoluteX(p);
		} else if(dimension == Constants.Y) {
			return toAbsoluteY(p);
		}
		return -1;
	}
	
	public int toAbsoluteY(int y) {
		return this.y+y;
	}
	
	public int toAbsoluteX(int x) {
		return this.x+x;
	}
	
	public int absolute(int dimension) {
		if(dimension == Constants.X) {
			return absoluteX();
		} else if(dimension == Constants.Y) {
			return absoluteY();
		}
		return -1;
	}
	
	public int absoluteX() {
		return dashboard == this ? x : x+dashboard.x;
	}
	
	public int absoluteY() {
		return dashboard == this ? y : y+dashboard.y;
	}
	
	public int relative(int dimension) {
		if(dimension == Constants.X) {
			return relativeX();
		} else if(dimension == Constants.Y) {
			return relativeY();
		}
		return -1;
	}
	
	public int relativeX() {
		return x;
	}
	
	public int relativeY() {
		return y;
	}
	
	public int area() {
		return width*height;
	}
	
	public double center(int dimension) {
		if(dimension == Constants.X) {
			return centerX();
		} else if(dimension == Constants.Y) {
			return centerY();
		}
		return -1;
	}
	
	public double centerX() {
		return ((double) x)+ownCenterX();
	}
	
	public double centerY() {
		return ((double) y)+ownCenterY();
	}
	
	public double ownCenter(int dimension) {
		if(dimension == Constants.X) {
			return ownCenterX();
		} else if(dimension == Constants.Y) {
			return ownCenterY();
		}
		return -1;
	}
	
	public double ownCenterX() {
		return width/2.0;
	}
	
	public double ownCenterY() {
		return height/2.0;
	}
	
	public double d(double p, int dimension) {
		if(dimension == Constants.X) {
			return dx(p);
		} else if(dimension == Constants.Y) {
			return dy(p);
		}
		return -1;
	}
	
	public double dx(double x) {
		return centerX()-x;
	}
	
	public double dy(double y) {
		return centerY()-y;
	}
	
	public int p(int dimension) {
		if(dimension == Constants.X) {
			return x;
		} else if(dimension == Constants.Y) {
			return y;
		}
		return -1;
	}
	
	public double p2(int dimension) {
		if(dimension == Constants.X) {
			return x2();
		} else if(dimension == Constants.Y) {
			return y2();
		}
		return -1;
	}
	
	public int x2() {
		return x+width;
	}
	
	public int y2() {
		return y+height;
	}
	
	public double area(Quadrant q) {
		double dx = 0;
		double dy = 0;
		switch (q) {
		case I:
			dx = dashboard.ownCenterX()-x;
			dy = dashboard.ownCenterY()-y;
			break;
		case II:
			dx = x2()-dashboard.ownCenterX();
			dy = dashboard.ownCenterY()-y;
			break;
		case III:
			dx = dashboard.ownCenterX()-x;
			dy = y2()-dashboard.ownCenterY();
			break;
		case IV:
			dx = x2()-dashboard.ownCenterX();
			dy = y2()-dashboard.ownCenterY();
			break;
		}
		
		return Math.min(dx, width)
				* Math.min(dy, height);
	}
	
	public double getAspectRatio(boolean normalized) {
		double pi = ((double) height)/width;
		if(normalized && pi > 1.0) {
			pi = 1/pi;
		}
		return pi;
	}

}
