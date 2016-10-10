package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenu;

//import cz.vutbr.fit.dashapp.controller.DashAppController;
//import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
//import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
//import cz.vutbr.fit.dashapp.controller.IPropertyChangeListener;

/**
 * Zoom support for UI.
 * 
 * @author Jiri Hynek
 *
 */
public class ZoomTool extends AbstractGUITool implements IGUITool/*, IPropertyChangeListener*/ {
	
	// zoom
	public static final double[] zoomField = { 0.125, 0.25, 0.375, 0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 4.0, 8.0 };
	public static final int ZOOM_MIN = 0;
	public static final int ZOOM_MAX = zoomField.length;
	public static final int DEFAULT_ZOOM_LEVEL = ZOOM_MAX/2;
	
	public static final String STR_ZOOM_IN = "Zoom in";
	public static final String STR_ZOOM_OUT = "Zoom out";
	
	private int zoomLevel;
	private ZoomAction zoomAction;
	private List<AbstractButton> btnsZoomIn;
	private List<AbstractButton> btnsZoomOut;
	
	/**
	 * Initializes zoom tool.
	 */
	public ZoomTool() {
		zoomLevel = DEFAULT_ZOOM_LEVEL;
		zoomAction = new ZoomAction();
		btnsZoomIn = new ArrayList<>();
		btnsZoomOut = new ArrayList<>();
		//DashAppController.getInstance().addPropertyChangeListener(this);
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("View");
		btnsZoomIn.add(menuBar.addItem(subMenu, STR_ZOOM_IN, zoomAction));
		btnsZoomOut.add(menuBar.addItem(subMenu, STR_ZOOM_OUT, zoomAction));
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if(toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		btnsZoomIn.add(toolbar.addButton(STR_ZOOM_IN, "/icons/Zoom in.png", zoomAction, 0));
		btnsZoomOut.add(toolbar.addButton(STR_ZOOM_OUT, "/icons/Zoom out.png", zoomAction, 0));
	}
	
	/*private void reset() {
		enableButtons(btnsZoomIn, true);
		enableButtons(btnsZoomOut, true);
		zoomLevel = DEFAULT_ZOOM_LEVEL;
		updateCanvasScaleRate();
	}*/
	
	private void updateCanvasScaleRate() {
		DashAppView.getInstance().getDashboardView().getCanvas().updateScaleRate(zoomField[zoomLevel]);
	}
	
	/**
	 * Enables/disables selected button.
	 * 
	 * @param buttons
	 * @param enable
	 */
	public void enableButtons(List<AbstractButton> buttons, boolean enable) {
		for (AbstractButton button : buttons) {
			button.setEnabled(enable);
		}
	}
	
	/**
	 * Zoom action which handles zoom update event.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class ZoomAction extends AbstractAction {
		
		/**
		 * UID 
		 */
		private static final long serialVersionUID = 8636377644255186208L;
		
		public ZoomAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// update zoom level
			Object source = e.getSource();
			int oldZoomLevel = zoomLevel;
			if(source instanceof JComponent) {
				if(btnsZoomIn.contains(source)) {
					zoomLevel++;
					updateCanvasScaleRate();
				} else if(btnsZoomOut.contains(source)) {
					zoomLevel--;
					updateCanvasScaleRate();
				}
			}
			
			// update buttons
			if(zoomLevel - oldZoomLevel > 0) {
				if(zoomLevel == 1) {
					enableButtons(btnsZoomOut, true);
				} else if(zoomLevel == zoomField.length-1) {
					enableButtons(btnsZoomIn, false);
				}
			} else {
				if(zoomLevel == zoomField.length-2) {
					enableButtons(btnsZoomIn, true);
				} else if(zoomLevel == 0) {
					enableButtons(btnsZoomOut, false);
				}
			}
		}
	}

	/*@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(e.propertyKind == EventKind.DASHBOARD_SELECTION_CHANGED) {
			reset();
		}
	}*/
}
