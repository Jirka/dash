package cz.vutbr.fit.dash.view.tools.canvas;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dash.controller.DashAppController;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.model.GraphicalElement;
import cz.vutbr.fit.dash.model.GraphicalElement.GEType;
import cz.vutbr.fit.dash.view.util.CanvasUtils;
import cz.vutbr.fit.dash.view.util.CanvasUtils.WorkingCopy;

/**
 * Canvas tool which provides support for insertion of a new graphical element.
 * 
 * @author Jiri Hynek
 *
 */
public class InsertTool extends AbstractCanvasTool {
	
	public InsertTool(boolean requiresSeparator, boolean isDefault, ButtonGroup buttonGroup) {
		super(requiresSeparator, isDefault, buttonGroup);
	}
	
	/**
	 * Working copy of graphical element which is currently inserted
	 */
	protected WorkingCopy candidateElement;
	
	@Override
	protected String getLabel() {
		return "Insert";
	}
	
	@Override
	protected String getTooltip() {
		return "Insert grapical element";
	}

	@Override
	protected String getImage() {
		return "/icons/Edit.png";
	}
	
	@Override
	protected void toolSelected(ActionEvent e) {
		// TODO
		//new ImageAction(ImageAction.GRAY_SCALE).actionPerformed(null);
		canvas.updateCursor(Cursor.CROSSHAIR_CURSOR);
		canvas.repaint();
	}
	
	@Override
	public void resetSelections() {
		candidateElement = null;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// create candidate element at selected position
		int x = canvas.unscale(e.getX());
		int y = canvas.unscale(e.getY());
		// attach enabled
		int attachSize = canvas.getAttachSize();
		if(canvas.getAttachSize() > 0) {
			Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
			List<GraphicalElement> elements = dashboard.getChildren(GEType.ALL_TYPES);
			x = CanvasUtils.getPreferredX(dashboard, elements, x, null, null, attachSize, canvas.width);
			y = CanvasUtils.getPreferredY(dashboard, elements, y, null, null, attachSize, canvas.height);
		}
		candidateElement = new WorkingCopy(x, y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(candidateElement != null && candidateElement.x2 != -1 && candidateElement.y2 != -1) {
			// create new graphical element
			DashAppController.getEventManager().createGrapicalElement(
					DashAppModel.getInstance().getSelectedDashboard(),
					candidateElement.x(), candidateElement.y(), 
					candidateElement.width(), candidateElement.height(), true);
			// release candidate element
			candidateElement = null;
			// draw as a basic graphical element (not as a candidate element)
			canvas.repaint();
		} else {
			// no need to repaint if candidate position of element has not been initialized
			candidateElement = null;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(candidateElement != null) {
			int x = canvas.unscale(e.getX());
			int y = canvas.unscale(e.getY());
			// calculate and update candidate element size (consider border of existing elements if required)
			int attachSize = canvas.getAttachSize();
			if(canvas.getAttachSize() > 0) {
				Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
				List<GraphicalElement> elements = dashboard.getChildren(GEType.ALL_TYPES);
				candidateElement.x2 = CanvasUtils.getPreferredX(dashboard, elements, x, null, null, attachSize, canvas.width);
				candidateElement.y2 = CanvasUtils.getPreferredY(dashboard, elements, y, null, null, attachSize, canvas.height);
			} else {
				candidateElement.x2 = x;
				candidateElement.y2 = y;
			}
			canvas.repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(candidateElement != null) {
				candidateElement = null;
				canvas.repaint();
			}
		}
	}

	@Override
	public void paintComponent(Graphics2D g) {
		Dashboard dashboard = DashAppModel.getInstance().getSelectedDashboard();
		
		// graphical elements
		paintUtil.paintGraphicalElements(g, dashboard, null);
		
		// candidate element
		if(candidateElement != null) {
			paintUtil.paintCandidateElement(g, candidateElement);
		}
		
		// dashboard area
		if(canvas.selectedElement == null) {
			paintUtil.paintDashboardArea(g, dashboard);
		}
	}

}
