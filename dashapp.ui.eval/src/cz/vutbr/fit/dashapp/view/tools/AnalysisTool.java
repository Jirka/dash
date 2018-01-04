package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;
import cz.vutbr.fit.dashapp.view.tools.analysis.AbstractAnalysisUI;

public class AnalysisTool extends AbstractGUITool implements IGUITool {
	
	public static final String LABEL = "Analysis";
	public static final String ICON = "/icons/Statistics.png";
	
	FolderAction coreAction;
	private AbstractAnalysisUI[] folderAnalysisUIs;
	
	public AnalysisTool(AbstractAnalysisUI[] folderAnalysisUIs) {
		this.coreAction = new FolderAction();
		this.folderAnalysisUIs = folderAnalysisUIs;
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Eval");
		menuBar.addItem(subMenu, LABEL, coreAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton(LABEL, ICON, coreAction, 0);
	}
	
	/**
	 * Go through all folders in workspace and makes heatmaps of dashboards stored in selected folder.
	 * It expects dashboards to differ only in XML description.
	 * Dashboards can be filtered by PREFIX.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class FolderAction extends AbstractAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -8793205852749826603L;

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractAnalysisUI analysis = chooseAction();
			if(analysis != null) {
				// create analysis
				analysis.perform();
			}
		}
		
		protected AbstractAnalysisUI chooseAction() {
			AbstractAnalysisUI resultAnalysis = null;
			if(folderAnalysisUIs.length > 0) {
				resultAnalysis = (AbstractAnalysisUI) JOptionPane.showInputDialog(null, "Choose action",
						"The Choice of an Action", JOptionPane.QUESTION_MESSAGE, null,
						folderAnalysisUIs, folderAnalysisUIs[0]);
			    System.out.println(resultAnalysis);
			}
			return resultAnalysis;
		}
	}

}
