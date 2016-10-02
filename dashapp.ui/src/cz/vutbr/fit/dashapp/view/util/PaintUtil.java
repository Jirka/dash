package cz.vutbr.fit.dashapp.view.util;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.view.Canvas;
import cz.vutbr.fit.dashapp.view.util.CanvasUtils.WorkingCopy;

public class PaintUtil {
	
	public final static float DASH[] = { 10.0f };
	public final static Stroke areaStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, DASH, 0.0f);
	public static Color BROWN = new Color(219, 78, 205);
	public static Color LIGHT_BLUE = new Color(0, 204, 255);
	
	private Canvas surface;

	public PaintUtil(Canvas surface) {
		this.surface = surface;
	}
	
	public void paintGraphicalElements(Graphics2D g1, Dashboard dashboard, GraphicalElement filterElement) {
		List<GraphicalElement> elements = dashboard.getChildren(GEType.ALL_TYPES);
		g1.setComposite(AlphaComposite.SrcOver.derive(0.4f));
		for (GraphicalElement element : elements) {
			if(element != filterElement) {
				Color widgetColor = getWidgetColor(element);
				g1.setColor(widgetColor);
				//g1.setStroke(new BasicStroke(2));
				g1.setBackground(widgetColor);
				g1.fillRect(element.absoluteX(), element.absoluteY(), element.width, element.height);
			}
		}
	}
	
	public void paintCandidateElement(Graphics2D g1, WorkingCopy candidateElement) {
		g1.setColor(Color.RED);
		g1.setBackground(Color.RED);
		g1.fillRect(candidateElement.x(), candidateElement.y(), candidateElement.width(), candidateElement.height());
	}
	
	public void paintSelectedDashboard(Graphics2D g1, WorkingCopy selectedElement) {
		g1.setColor(Color.LIGHT_GRAY);
		g1.setComposite(AlphaComposite.SrcOver.derive(0.70f));
		g1.fillRect(0, 0, surface.width, selectedElement.y());
		g1.fillRect(0, selectedElement.y(), selectedElement.x(), selectedElement.height());
		g1.fillRect(selectedElement.x2(), selectedElement.y(), surface.width-selectedElement.x2(), selectedElement.height());
		g1.fillRect(0, selectedElement.y2(), surface.width, surface.height-selectedElement.y2());
		g1.setColor(Color.YELLOW);
		g1.setStroke(areaStroke);
		g1.setComposite(AlphaComposite.SrcOver.derive(0.40f));
		g1.drawRect(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height());
		g1.fillRect(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height());
	}
	
	public void paintSelectedGraphicalElement(Graphics2D g1, WorkingCopy selectedElement) {
		g1.setColor(Color.YELLOW);
		g1.fillRect(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height());
		g1.setColor(Color.ORANGE);
		g1.setComposite(AlphaComposite.SrcOver.derive(1f));
		g1.setStroke(new BasicStroke(2));
		g1.drawRect(selectedElement.x(), selectedElement.y(), selectedElement.width(), selectedElement.height());
	}
	
	public void paintDashboardArea(Graphics2D g1, Dashboard dashboard) {
		g1.setColor(Color.LIGHT_GRAY);
		g1.setComposite(AlphaComposite.SrcOver.derive(0.70f));
		g1.fillRect(0, 0, surface.width, dashboard.y);
		g1.fillRect(0, dashboard.y, dashboard.x, dashboard.height);
		g1.fillRect(dashboard.x+dashboard.width, dashboard.y, surface.width-dashboard.x-dashboard.width, dashboard.height);
		g1.fillRect(0, dashboard.y+dashboard.height, surface.width, surface.height-dashboard.y-dashboard.height);
		g1.setColor(Color.BLACK);
		g1.setStroke(areaStroke);
		g1.drawRect(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
	}
	
	protected Color getWidgetColor(GraphicalElement graphicalElement) {
		Color color;
		switch (graphicalElement.type) {
			case BUTTON:
			case TOOLBAR:
				color = BROWN;
				break;
			case HEADER:
				color = Color.BLUE;
				break;
			case LABEL:
				color = LIGHT_BLUE;
				break;
			case DECORATION:
				color = Color.RED;
				break;
			default:
				color = Color.GREEN;
				break;
			}
		return color;
	}

}
