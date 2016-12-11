package cz.vutbr.fit.dashapp.view.tools.canvas;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.view.util.CanvasUtils;
import cz.vutbr.fit.dashapp.view.util.CanvasUtils.WorkingCopy;

/**
 * Canvas tool which provides support for selection and modification of graphical elements.
 * 
 * @author Jiri Hynek
 *
 */
public class SelectTool extends ViewTool {
	
	public SelectTool(boolean requiresSeparator, boolean isDefault, ButtonGroup buttonGroup) {
		super(requiresSeparator, isDefault, buttonGroup);
	}
	
	/**
	 * Working copy of graphical element which is currently modified.
	 */
	protected WorkingCopy candidateElement;
	
	/**
	 * TODO
	 */
	private Point helpAttachPoint;

	@Override
	protected String getLabel() {
		return "Select";
	}
	
	@Override
	protected String getTooltip() {
		return "Select graphical element";
	}

	@Override
	protected String getImage() {
		return "/icons/Pointer.png";
	}
	
	@Override
	protected void toolSelected(ActionEvent e) {
		canvas.setSelectedElement(null);
		candidateElement = null;
		canvas.setGrayScale(true);
		canvas.updateCursor(Cursor.DEFAULT_CURSOR);
		canvas.repaint();
	}
	
	@Override
	public void resetSelections() {
		candidateElement = null;
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			canvas.getPopUpMenu().show(canvas, e.getX(), e.getY());
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// store actual position 
		int x = canvas.unscale(e.getX());
		int y = canvas.unscale(e.getY());
		
		// try to locate existing element
		GraphicalElement selectedElement = CanvasUtils.findGraphicalElement(canvas.getDashboard().getChildren(GEType.ALL_TYPES), x, y);
		
		// select found element
		if(selectedElement != null) {
			candidateElement = new WorkingCopy(selectedElement.absoluteX(), selectedElement.absoluteY(),
												selectedElement.absoluteX()+selectedElement.width,
												selectedElement.absoluteY()+selectedElement.height);
			// change cursor (if move action is not offered right now)
			if(canvas.getCursor().getType() == Cursor.DEFAULT_CURSOR) {
				canvas.updateCursor(Cursor.HAND_CURSOR);
			}
			// initialize help point used by attaching to borders
			helpAttachPoint = new Point(0, 0);
		} else {
			// cancel element selection
			candidateElement = null;
		}
		
		if(candidateElement != null) {
			previousPoint = new Point(x, y);
		} else {
			previousPoint = new Point(e.getX(), e.getY());
		}
		
		canvas.setSelectedElement(selectedElement);
		canvas.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(candidateElement != null) {
			// selected element could have been moved or resized
			WorkingCopy candidateElementBackup = candidateElement; // candidate element selection will be cleared by property change event
			GraphicalElement selectedElementBackup = canvas.selectedElement;
			CanvasUtils.cropCandidateElement(candidateElementBackup, canvas.width, canvas.height, canvas.getCursor().getType() == Cursor.HAND_CURSOR);
			if(candidateElement.width() > 2 && candidateElement.height() > 2) {
				DashAppController.getEventManager().updateGraphicalElement(canvas.getSelectedElement(),
						candidateElement.x(), candidateElement.y(), candidateElement.width(), candidateElement.height(), true);
				// candidate element selection will be lost by property change events
			} else {
				// no change is made
				candidateElementBackup.restore(selectedElementBackup.absoluteX(), selectedElementBackup.absoluteY(),
						selectedElementBackup.absoluteX()+selectedElementBackup.width,
						selectedElementBackup.absoluteY()+selectedElementBackup.height);
			}
			// element is still selected but no operation is active
			canvas.updateCursor(Cursor.DEFAULT_CURSOR);
			candidateElement = candidateElementBackup; // restore candidate element selection
			canvas.selectedElement = selectedElementBackup;
			canvas.repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(candidateElement != null) {
			// selected element is moved
			int x = canvas.unscale(e.getX());
			int y = canvas.unscale(e.getY());
			
			// compute difference
			int dx = previousPoint.x-x;
			int dy = previousPoint.y-y;
			
			// calculate change of mouse position
			if(dx != 0 || dy != 0) {
				// calculate and update selected element position (move and resize action) and size (resize action)
				Dashboard dashboard = canvas.getDashboard();
				CanvasUtils.updatePosition(dashboard, dashboard.getChildren(GEType.ALL_TYPES), canvas.getSelectedElement(),
								candidateElement, dx, dy, helpAttachPoint, canvas.getCursor().getType(),
								canvas.getAttachSize(), canvas.width, canvas.height);
				// save actual position that can be used in next possible drag event 
				previousPoint.x = x;
				previousPoint.y = y;
				// repaint canvas
				canvas.repaint();
			}
		} else {
			super.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(candidateElement != null) {
			// possible mouse hover over border of selected element
			int x = canvas.unscale(e.getX());
			int y = canvas.unscale(e.getY());
			int recommendedCursorType = CanvasUtils.getRecommendedCursorType(candidateElement, x, y);
			// offer move action to user if this action is available
			canvas.updateCursor(recommendedCursorType);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			/*if(candidateElement != null) {
				candidateElement = null;
				canvas.repaint();
			}*/
			if(canvas.getSelectedElement() != null) {
				if(canvas.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
					GraphicalElement selectedElement = canvas.getSelectedElement();
					candidateElement.restore(selectedElement.absoluteX(), selectedElement.absoluteY(),
							selectedElement.absoluteX()+selectedElement.width,
							selectedElement.absoluteY()+selectedElement.height);
					canvas.updateCursor(Cursor.DEFAULT_CURSOR);
				} else {
					candidateElement = null;
					canvas.setSelectedElement(null);
				}
				canvas.repaint();
			}
		} else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			if(canvas.getSelectedElement() != null) {
				if(canvas.getCursor().getType() == Cursor.DEFAULT_CURSOR) {
					DashAppController.getEventManager().deleteGraphicalElement(canvas.getSelectedElement());
					candidateElement = null;
					canvas.setSelectedElement(null);
					canvas.repaint();
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics2D g) {
		Dashboard dashboard = canvas.getDashboard();
		
		// graphical elements
		paintUtil.paintGraphicalElements(g, dashboard, canvas.getSelectedElement());
		
		// selected element
		if(candidateElement != null) {
			paintUtil.paintSelectedGraphicalElement(g, candidateElement);
		}
		
		// dashboard area
		paintUtil.paintDashboardArea(g, dashboard);
	}

}
