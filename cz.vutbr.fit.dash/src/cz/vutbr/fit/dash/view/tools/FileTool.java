package cz.vutbr.fit.dash.view.tools;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import cz.vutbr.fit.dash.controller.DashAppController;
import cz.vutbr.fit.dash.model.DashAppModel;
import cz.vutbr.fit.dash.model.Dashboard;
import cz.vutbr.fit.dash.view.MenuBar;
import cz.vutbr.fit.dash.view.ToolBar;
import cz.vutbr.fit.dash.view.util.Dialogs;

/**
 * File management support.
 * 
 * @author Jiri Hynek
 *
 */
public class FileTool extends AbstractGUITool implements IGUITool {

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		menuBar.addItem(subMenu, "Open", new OpenFolderAction());
		menuBar.addItem(subMenu, "Refresh", new LoadStoreAction(LoadStoreAction.REFRESH));
		menuBar.addItem(subMenu, "Save", new LoadStoreAction(LoadStoreAction.SAVE));
		menuBar.addItem(subMenu, "Save all", new LoadStoreAction(LoadStoreAction.SAVE_ALL));
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("New", "/icons/Document.png", new NewFileAction(), 0);
		toolbar.addSeparator();
		toolbar.addButton("Open files", "/icons/Open file.png", new OpenFolderAction(), 0);
		toolbar.addButton("Refresh", "/icons/Refresh.png", new LoadStoreAction(LoadStoreAction.REFRESH), 0);
		toolbar.addSeparator();
		toolbar.addButton("Save", "/icons/Save.png", new LoadStoreAction(LoadStoreAction.SAVE), 0);
		toolbar.addButton("Save all", "/icons/Save as.png", null, 0);
	}

	/**
	 * New file action which handles new file creation event.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class NewFileAction extends AbstractAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = -7806859795031384430L;

		public NewFileAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// construct new file dialog GUI
			JTextField nameInput = new JTextField("new_file", 20);
			JSpinner spinnerWidth = new JSpinner();
			spinnerWidth.setValue(640);
			((JSpinner.DefaultEditor) spinnerWidth.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
			spinnerWidth.setPreferredSize(new Dimension(60, 20));
			JSpinner spinnerHeight = new JSpinner();
			((JSpinner.DefaultEditor) spinnerHeight.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
			spinnerHeight.setValue(480);
			spinnerHeight.setPreferredSize(new Dimension(60, 20));
			final JComponent[] inputs = new JComponent[] { new JLabel("Name"), nameInput, new JLabel("Width"),
					spinnerWidth, new JLabel("Height"), spinnerHeight };
			//
			if (JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
				try {
					int width = new Integer(spinnerWidth.getValue().toString());
					int height = new Integer(spinnerHeight.getValue().toString());
					if (DashAppController.getEventManager().createEmptyDashboard(width, height, nameInput.getText())) {
						// TODO report problem;
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}

	/**
	 * Open folder action which handles open folder event.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class OpenFolderAction extends AbstractAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = -565363971398691873L;

		@Override
		public void actionPerformed(ActionEvent e) {

			// file picker //
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(System.getProperty("user.home")));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				if (file != null) {
					String path = file.getAbsolutePath();
					if (path != null) {
						DashAppController.getEventManager()._updateFolderPath(path);
					}
				}
			}
		}
	}

	/**
	 * Load/store action which handles save and load requests.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class LoadStoreAction extends AbstractAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = 1L;

		public static final int REFRESH = 0;
		public static final int SAVE = 1;
		public static final int SAVE_ALL = 2;

		private int kind;

		public LoadStoreAction(int kind) {
			this.kind = kind;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Dashboard selectedDashboard = DashAppModel.getInstance().getSelectedDashboard();
			if (selectedDashboard != null) {
				switch (kind) {
				case SAVE:
					try {
						DashAppController.getEventManager().saveDashboardToFile(selectedDashboard);
					} catch (IOException e1) {
						Dialogs.report("Unable to save dashboard file.");
					}
					break;
				case REFRESH:
					try {
						DashAppController.getEventManager().reloadDashboardFromFile(selectedDashboard);
					} catch (Exception e1) {
						Dialogs.report("Unable to load dashboard file.");
					}
					break;
				}
			}
		}
	}

}
