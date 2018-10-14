package cz.vutbr.fit.dashapp.view.tools;

import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.action.BasicDashAction;
import cz.vutbr.fit.dashapp.view.action.SelectionDashAction;
import cz.vutbr.fit.dashapp.view.action.analysis.FileAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;


/**
 * 
 * @author Jiri Hynek
 *
 */
public class FileAnalysisTool extends AbstractGUITool implements IGUITool {
	
	public static final String LABEL = "File analysis";
	public static final String ICON = "/icons/Statistics.png";
	
	private FileAnalysisUI[] fileAnalysisUIs;
	
	public FileAnalysisTool(FileAnalysisUI[] fileAnalysisUIs) {
		this(false, fileAnalysisUIs);
	}
	
	public FileAnalysisTool(boolean addSeparator, FileAnalysisUI[] fileAnalysisUIs) {
		super(addSeparator);
		this.fileAnalysisUIs = fileAnalysisUIs;
	}
	
	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (addSeparator && toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton(LABEL, ICON, new SelectionDashAction(fileAnalysisUIs), 0);
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu(LABEL);
		
		if(addSeparator && subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		
		// toolbar items
		for (FileAnalysisUI fileAnalysisUI : fileAnalysisUIs) {
			menuBar.addItem(subMenu, fileAnalysisUI.getLabel(), new BasicDashAction(fileAnalysisUI));
		}
	}

}
