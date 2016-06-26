package cz.vutbr.fit.dash.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import cz.vutbr.fit.dash.gui.DashAppGUI;
import cz.vutbr.fit.dash.gui.ToolBar;

public class FullScreenAction extends AbstractAction {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8778318546851797316L;
	private JFrame frame;
	private int splitPanePosition;
	private Dimension frameSize;
	private ToolBar toolbar;

	public FullScreenAction() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		DashAppGUI gui = DashAppGUI.getInstance();
		
		if(frame == null) {
			frame = new JFrame("Full screen");
			frame.setUndecorated(true);

			splitPanePosition = gui.getSplitPane().getDividerLocation();
			frameSize = gui.getFrame().getSize();
			frame.add(gui.getDashboardView().getComponent(), BorderLayout.CENTER);
			toolbar = new ToolBar();
			frame.add(toolbar.getComponent(), BorderLayout.SOUTH);

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			gs.setFullScreenWindow(frame);
			frame.validate();
		} else {
			gui.getSplitPane().add(gui.getDashboardView().getComponent(), 0);
			gui.getFrame().pack();
			gui.getFrame().setSize(frameSize);
			gui.getSplitPane().setDividerLocation(splitPanePosition);
			toolbar.dispose();
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			frame = null;
		}
	}
}