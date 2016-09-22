package cz.vutbr.fit.dash.view.tools.canvas;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dash.controller.PropertyChangeEvent;
import cz.vutbr.fit.dash.controller.EventManager.EventKind;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.view.util.CanvasUtils.WorkingCopy;

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
		return "/icons/Billboard.png";
	}
	
	/**
	 * Reset selection (dashboard is always selected).
	 */
	protected void reset() {
		Dashboard selectedElement = DashAppModel.getInstance().getSelectedDashboard();
		canvas.setSelectedElement(selectedElement);
		if(selectedElement != null) {
			candidateElement = new WorkingCopy(selectedElement.absoluteX(), selectedElement.absoluteY(),
					selectedElement.absoluteX()+selectedElement.width, selectedElement.absoluteY()+selectedElement.height);
		}
	}
	
	@Override
	protected void toolSelected(ActionEvent e) {
		// TODO
		//new ImageAction(ImageAction.GRAY_SCALE).actionPerformed(null);
		reset();
		canvas.updateCursor(Cursor.DEFAULT_CURSOR);
		canvas.repaint();
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(e.propertyKind == EventKind.GRAPHICAL_ELEMENT_CHANGED) {
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
