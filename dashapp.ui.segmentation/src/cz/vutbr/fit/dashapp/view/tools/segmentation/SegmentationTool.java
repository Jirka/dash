package cz.vutbr.fit.dashapp.view.tools.segmentation;

import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.action.BasicDashAction;
import cz.vutbr.fit.dashapp.view.action.SelectionDashAction;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SegmentationTool extends AbstractGUITool implements IGUITool {
	
	public static final String LABEL = "Segmentation";
	public static final String ICON = "/icons/Application form.png";
	
	private SegmentationAlgorithmUI[] segmentationUIs;
	
	public SegmentationTool(SegmentationAlgorithmUI[] segmentationUIs) {
		this.segmentationUIs = segmentationUIs;
	}
	
	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("Segmentation algorithms", ICON, new SelectionDashAction(segmentationUIs), 0);
	}
	
	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu(LABEL);
		
		// toolbar items
		for (SegmentationAlgorithmUI segmentationAlgorithmUI : segmentationUIs) {
			menuBar.addItem(subMenu, segmentationAlgorithmUI.getLabel(), new BasicDashAction(segmentationAlgorithmUI));
		}
	}
}
