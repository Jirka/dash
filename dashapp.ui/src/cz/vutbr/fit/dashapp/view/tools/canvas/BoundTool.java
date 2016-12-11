package cz.vutbr.fit.dashapp.view.tools.canvas;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.view.util.CanvasUtils.WorkingCopy;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class BoundTool extends SelectTool {
	
	public BoundTool(boolean requiresSeparator, boolean isDefault, ButtonGroup buttonGroup) {
		super(requiresSeparator, isDefault, buttonGroup);
	}

	@Override
	protected String getLabel() {
		return "Bound";
	}
	
	@Override
	protected String getTooltip() {
		return "Define dashboard area";
	}

	@Override
	protected String getImage() {
		return "/icons/Monitor.png";
	}
	
	/**
	 * Reset selection (dashboard is always selected).
	 */
	protected void reset() {
		Dashboard selectedElement = canvas.getDashboard();
		canvas.setSelectedElement(selectedElement);
		if(selectedElement != null) {
			candidateElement = new WorkingCopy(selectedElement.absoluteX(), selectedElement.absoluteY(),
					selectedElement.absoluteX()+selectedElement.width, selectedElement.absoluteY()+selectedElement.height);
		}
	}
	
	@Override
	protected void toolSelected(ActionEvent e) {
		reset();
		canvas.setGrayScale(true);
		canvas.updateCursor(Cursor.DEFAULT_CURSOR);
		canvas.repaint();
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(e.propertyKind == EventKind.GRAPHICAL_ELEMENT_CHANGED
				|| e.propertyKind == EventKind.FILE_SELECTION_CHANGED) {
			reset();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// store actual position 
		previousPoint = new Point(e.getX(), e.getY());
		canvas.repaint();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// do nothing
	}

	@Override
	public void paintComponent(Graphics2D g) {
		// selected element
		paintUtil.paintSelectedDashboard(g, candidateElement);
	}

}
