package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.util.Dialogs;

/**
 * Load dashboard support.
 * 
 * @author Jiri Hynek
 *
 */
public class ReloadTool extends AbstractGUITool implements IGUITool {
	
	RefreshAction refreshAction;
	
	public ReloadTool() {
		refreshAction = new RefreshAction();
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		menuBar.addItem(subMenu, "Reload dashboard", refreshAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("Reload dashboard from file", "/icons/Refresh.png", refreshAction, 0);
	}

	/**
	 * Refresh action which handles dashboard (re)load requests.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class RefreshAction extends AbstractAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
			try {
				DashAppController.getEventManager().reloadDashboardFromFile(selectedDashboard);
			} catch (Exception e1) {
				Dialogs.report("Unable to load dashboard file.");
			}
		}
	}

}
