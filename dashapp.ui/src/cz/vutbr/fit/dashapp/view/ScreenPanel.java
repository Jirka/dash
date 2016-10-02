package cz.vutbr.fit.dashapp.view;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import cz.vutbr.fit.dashapp.view.tools.IGUITool;

/**
 * Main screen panel which presents dashboards.
 * 
 * @author Jiri Hynek
 *
 */
public class ScreenPanel implements IComponent {
	
	/**
	 * Main component
	 */
	private JScrollPane panel;
	
	@Override
	public JComponent getComponent() {
		return panel;
	}
	
	/**
	 * Dashboard canvas.
	 */
	private Canvas canvas;
	
	/**
	 * Returns dashboard canvas
	 * 
	 * @return surface
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * Initializes screen panel
	 * @param plugins 
	 */
	public ScreenPanel(List<IGUITool> plugins) {
		canvas = new Canvas(plugins);
		canvas.setFocusable(true);
		panel = new JScrollPane(canvas);
	}
	
	/**
	 * TODO remove?
	 * 
	 * @param x
	 * @param y
	 */
	public void setScrollPanePosition(int x, int y) {
		//System.out.println("new: " + x + " " + y);
		if(x >= 0) {
			panel.getHorizontalScrollBar().setValue(x);
		}
		if(y >= 0) {
			panel.getVerticalScrollBar().setValue(y);
		}
	}

	/**
	 * TODO remove?
	 * 
	 * @param dx
	 * @param dy
	 */
	public void move(int dx, int dy) {
		//System.out.println("change: " + dx + " " + dy);
		setScrollPanePosition(panel.getHorizontalScrollBar().getValue()+dx, panel.getVerticalScrollBar().getValue()+dy);
	}
}
