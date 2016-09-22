package cz.vutbr.fit.dash.view.tools.canvas;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 * Canvas tool which provides support for basic dashboard view.
 * 
 * @author Jiri Hynek
 *
 */
public class ViewTool extends AbstractCanvasTool {
	
	public ViewTool(boolean requiresSeparator, boolean isDefault, ButtonGroup buttonGroup) {
		super(requiresSeparator, isDefault, buttonGroup);
	}
	
	/**
	 * Help point variable.
	 */
	protected Point previousPoint;

	@Override
	protected String getLabel() {
		return "View";
	}
	
	@Override
	protected String getTooltip() {
		return "View Image";
	}

	@Override
	protected String getImage() {
		return "/icons/Monitor.png";
	}
	
	@Override
	protected void toolSelected(ActionEvent e) {
		// TODO
		//new ImageAction(ImageAction.RESET).actionPerformed(null);
		canvas.updateCursor(Cursor.DEFAULT_CURSOR);
		canvas.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// store actual position 
		previousPoint = new Point(e.getX(), e.getY());
		canvas.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(previousPoint != null) {
			// screen is moved
			JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, canvas);
			if(viewPort != null) {
				// scale can't be used for this action (user is moving the parent screen)
				int x = e.getX();
				int y = e.getY();
				int dx = previousPoint.x-x;
				int dy = previousPoint.y-y;
				Rectangle view = viewPort.getViewRect();
                view.x += dx;
                view.y += dy;
				if(dx != 0 || dy != 0) {
					canvas.scrollRectToVisible(view);
					//DashAppGUI.getDashboardView().move(dx, dy);
					canvas.repaint();
				}
			}
		}
	}
}
