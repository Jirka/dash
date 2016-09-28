package cz.vutbr.fit.dash.view.tools;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JMenu;

import cz.vutbr.fit.dash.controller.DashAppController;
import cz.vutbr.fit.dash.controller.EventManager.EventKind;
import cz.vutbr.fit.dash.controller.IPropertyChangeListener;
import cz.vutbr.fit.dash.controller.PropertyChangeEvent;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.view.MenuBar;
import cz.vutbr.fit.dash.view.ToolBar;
import cz.vutbr.fit.dash.view.util.Dialogs;

/**
 * Save dashboard support.
 * 
 * @author Jiri Hynek
 *
 */
public class SaveTool extends AbstractGUITool implements IGUITool, IPropertyChangeListener {
	
	SaveAction saveAction;
	SaveAction saveAllAction;
	private List<AbstractButton> btnsSave;
	private List<AbstractButton> btnsSaveAll;
	
	public SaveTool() {
		saveAction = new SaveAction(SaveAction.SAVE);
		saveAllAction = new SaveAction(SaveAction.SAVE_ALL);
		btnsSave = new ArrayList<>();
		btnsSaveAll = new ArrayList<>();
		DashAppController.getInstance().addPropertyChangeListener(this);
	}
	
	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		AbstractButton btn = menuBar.addItem(subMenu, "Save", saveAction);
		btn.setEnabled(false);
		btnsSave.add(btn);
		btn = menuBar.addItem(subMenu, "Save all", saveAllAction);
		btn.setEnabled(false);
		btnsSaveAll.add(btn);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		AbstractButton btn = toolbar.addButton("Save", "/icons/Save.png", saveAction, 0);
		btn.setEnabled(false);
		btnsSave.add(btn);
		btn = toolbar.addButton("Save all", "/icons/Save as.png", saveAllAction, 0);
		btn.setEnabled(false);
		btnsSaveAll.add(btn);
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
	 * Load/store action which handles save and load requests.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class SaveAction extends AbstractAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = 1L;

		public static final int SAVE = 1;
		public static final int SAVE_ALL = 2;

		private int kind;

		public SaveAction(int kind) {
			this.kind = kind;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
				switch (kind) {
				case SAVE:
					Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
					if (selectedDashboard != null) {
						try {
							DashAppController.getEventManager().saveDashboardToFile(selectedDashboard);
							
						} catch (IOException e1) {
							Dialogs.report("Unable to save dashboard file " + selectedDashboard.getDashboardFile().toString() + ".");
						}
					}
					break;
				case SAVE_ALL:
					List<Dashboard> dashboards = DashAppModel.getInstance().getDashboards();
					List<Dashboard> unsucessfullySavedDashboards = new ArrayList<>(); 
					for (Dashboard dashboard : dashboards) {
						if(dashboard.getSerializedDashboard().isDirty()) {
							try {
								DashAppController.getEventManager().saveDashboardToFile(dashboard);
							} catch (Exception e1) {
								unsucessfullySavedDashboards.add(dashboard);
							}
						}
					}
					if(!unsucessfullySavedDashboards.isEmpty()) {
						StringBuffer sb = new StringBuffer();
						for (Dashboard dashboard : unsucessfullySavedDashboards) {
							if(sb.length() > 0) {
								sb.append("\n ");
							}
							sb.append(dashboard.getSerializedDashboard().toString());
						}
						Dialogs.report("Unable to save dashboard files: " + sb.toString() + ".");
					}
					break;
				}
		}
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(e.propertyKind == EventKind.DASHBOARD_STATE_CHANGED) {
			if(e.selectedDashboard == DashAppModel.getInstance().getSelectedDashboard()) {
				enableButtons(btnsSave, (boolean) e.modelChange.newValue);
			}
			List<Dashboard> dashboards = DashAppModel.getInstance().getDashboards();
			boolean existsDirty = false;
			for (Dashboard dashboard : dashboards) {
				if(dashboard.getSerializedDashboard().isDirty()) {
					existsDirty = true;
					break;
				}
			}
			enableButtons(btnsSaveAll, existsDirty);
		} else if(e.propertyKind == EventKind.DASHBOARD_SELECTION_CHANGED) {
			Dashboard selectedDashboard = (Dashboard) e.modelChange.newValue;
			if(selectedDashboard != null) {
				enableButtons(btnsSave, selectedDashboard.getSerializedDashboard().isDirty());
			} else {
				enableButtons(btnsSave, false);
			}
		}
	}

}
