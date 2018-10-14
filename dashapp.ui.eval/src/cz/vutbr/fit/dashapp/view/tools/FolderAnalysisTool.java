package cz.vutbr.fit.dashapp.view.tools;

import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.action.BasicDashAction;
import cz.vutbr.fit.dashapp.view.action.SelectionDashAction;
import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;


/**
 * 
 * @author Jiri Hynek
 *
 */
public class FolderAnalysisTool extends AbstractGUITool implements IGUITool {
	
	public static final String LABEL = "Folder analysis";
	public static final String ICON = "/icons/Statistics_folder.png";
	
	private FolderAnalysisUI[] folderAnalysisUIs;
	
	public FolderAnalysisTool(FolderAnalysisUI[] folderAnalysisUIs) {
		this(false, folderAnalysisUIs);
	}
	
	public FolderAnalysisTool(boolean addSeparator, FolderAnalysisUI[] folderAnalysisUIs) {
		super(addSeparator);
		this.folderAnalysisUIs = folderAnalysisUIs;
	}
	
	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (addSeparator && toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton(LABEL, ICON, new SelectionDashAction(folderAnalysisUIs), 0);
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu(LABEL);
		
		if(addSeparator && subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		
		// toolbar items
		for (FolderAnalysisUI folderAnalysisUI : folderAnalysisUIs) {
			menuBar.addItem(subMenu, folderAnalysisUI.getLabel(), new BasicDashAction(folderAnalysisUI));
		}
	}

}
