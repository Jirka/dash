package cz.vutbr.fit.dash.gui;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class DashboardView implements IComponent {
	
	private Surface surface;
	private JScrollPane panel;

	public DashboardView() {
		surface = new Surface();
		surface.setFocusable(true);
		
		panel = new JScrollPane(surface);
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}
	
	public Surface getSurface() {
		return surface;
	}
	
	public void setScrollPanePosition(int x, int y) {
		//System.out.println("new: " + x + " " + y);
		if(x >= 0) {
			panel.getHorizontalScrollBar().setValue(x);
		}
		if(y >= 0) {
			panel.getVerticalScrollBar().setValue(y);
		}
	}

	public void move(int dx, int dy) {
		//System.out.println("change: " + dx + " " + dy);
		setScrollPanePosition(panel.getHorizontalScrollBar().getValue()+dx, panel.getVerticalScrollBar().getValue()+dy);
	}
}
