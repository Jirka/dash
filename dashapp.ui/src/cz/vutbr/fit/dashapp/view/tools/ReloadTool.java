package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.dialog.SimpleDialogs;

/**
 * Load dashboard support.
 * 
 * @author Jiri Hynek
 *
 */
public class ReloadTool extends AbstractGUITool implements IGUITool {
	
	RefreshAction refreshAction;
	
	public ReloadTool() {
		this(false);
	}
	
	public ReloadTool(boolean addSeparator) {
		super(addSeparator);
		refreshAction = new RefreshAction();
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		if(addSeparator && subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		menuBar.addItem(subMenu, "Reload dashboard", refreshAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (addSeparator && toolbar.getAmountOfItems() > 0) {
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
			DashboardFile selectedDashboardFile = DashAppUtils.getSelectedDashboardFile();
			if(selectedDashboardFile != null) {
				try {
					DashAppController.getEventManager().reloadDashboardFromFile(selectedDashboardFile);
				} catch (Exception e1) {
					SimpleDialogs.report("Unable to load dashboard file.");
				}
			}
		}
	}

}
