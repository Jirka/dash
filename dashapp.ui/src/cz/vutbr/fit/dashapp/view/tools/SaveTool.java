package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.controller.IPropertyChangeListener;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.util.Dialogs;

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
					DashboardFile selectedDashboardFile = DashAppUtils.getSelectedDashboardFile();
					if (selectedDashboardFile != null) {
						try {
							DashAppController.getEventManager().saveDashboardToFile(selectedDashboardFile);
							
						} catch (IOException e1) {
							Dialogs.report("Unable to save dashboard file " + selectedDashboardFile.toString() + ".");
						}
					}
					break;
				case SAVE_ALL:
					List<DashboardFile> dashboards = DashAppModel.getInstance().getOpenedDashboardFiles();
					List<DashboardFile> unsucessfullySavedDashboards = new ArrayList<>(); 
					for (DashboardFile dashboardFile : dashboards) {
						if(dashboardFile.getSerializedDashboard().isDirty()) {
							try {
								DashAppController.getEventManager().saveDashboardToFile(dashboardFile);
							} catch (Exception e1) {
								unsucessfullySavedDashboards.add(dashboardFile);
							}
						}
					}
					if(!unsucessfullySavedDashboards.isEmpty()) {
						StringBuffer sb = new StringBuffer();
						for (DashboardFile dashboardFile : unsucessfullySavedDashboards) {
							if(sb.length() > 0) {
								sb.append("\n ");
							}
							sb.append(dashboardFile.getSerializedDashboard().toString());
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
			if(e.selectedFile == DashAppUtils.getSelectedDashboardFile()) {
				enableButtons(btnsSave, (boolean) e.modelChange.newValue);
			}
			List<DashboardFile> dashboards = DashAppModel.getInstance().getOpenedDashboardFiles();
			boolean existsDirty = false;
			for (DashboardFile dashboardFile : dashboards) {
				if(dashboardFile.getSerializedDashboard().isDirty()) {
					existsDirty = true;
					break;
				}
			}
			enableButtons(btnsSaveAll, existsDirty);
		} else if(e.propertyKind == EventKind.FILE_SELECTION_CHANGED) {
			IWorkspaceFile selectedFile = (IWorkspaceFile) e.modelChange.newValue;
			if(selectedFile != null && selectedFile instanceof DashboardFile) {
				enableButtons(btnsSave, ((DashboardFile) selectedFile).getSerializedDashboard().isDirty());
			} else {
				enableButtons(btnsSave, false);
			}
		}
	}
	
	@Override
	public boolean windowsClosing(WindowEvent e) {
		List<DashboardFile> dashboards = DashAppModel.getInstance().getOpenedDashboardFiles();
		List<DashboardFile> unsavedDashboards = new ArrayList<>(); 
		for (DashboardFile dashboardFile : dashboards) {
			if(dashboardFile.getSerializedDashboard().isDirty()) {
				unsavedDashboards.add(dashboardFile);
			}
		}
		if(!unsavedDashboards.isEmpty()) {
			int result = Dialogs.YesNoCancel("There are several unsaved dashboards. Do you want to save them?");
			if(result == JOptionPane.YES_OPTION) {
				saveAllAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			} else if(result == JOptionPane.CANCEL_OPTION) {
				return true;
			}
		}
		return false;
	}

}
