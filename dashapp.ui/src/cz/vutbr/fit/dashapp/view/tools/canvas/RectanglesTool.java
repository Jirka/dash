package cz.vutbr.fit.dashapp.view.tools.canvas;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.model.Dashboard;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class RectanglesTool extends AbstractCanvasTool {
	
	public RectanglesTool(boolean requiresSeparator, boolean isDefault, ButtonGroup buttonGroup) {
		super(requiresSeparator, isDefault, buttonGroup);
	}
	
	/**
	 * Help point variable.
	 */
	protected Point previousPoint;

	@Override
	protected String getLabel() {
		return "Rectangles";
	}
	
	@Override
	protected String getTooltip() {
		return "View Rectangles";
	}

	@Override
	protected String getImage() {
		return "/icons/Genealogy.png";
	}
	
	@Override
	protected void toolSelected(ActionEvent e) {
		canvas.setGrayScale(true);
		canvas.updateCursor(Cursor.DEFAULT_CURSOR);
		canvas.repaint();
	}

	@Override
	public void paintComponent(Graphics2D g) {
		Dashboard dashboard = canvas.getDashboard();
		
		// graphical elements
		paintUtil.paintGraphicalElementsTree(g, dashboard, canvas.getSelectedElement());
		
		// dashboard area
		paintUtil.paintDashboardArea(g, dashboard);
	}

}
