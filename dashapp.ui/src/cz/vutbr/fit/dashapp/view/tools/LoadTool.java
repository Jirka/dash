package cz.vutbr.fit.dashapp.view.tools;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

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
public class LoadTool extends AbstractGUITool implements IGUITool {
	
	NewFileAction newFileAction;
	OpenFolderAction openFolderAction;
	RefreshAction refreshAction;
	
	public LoadTool() {
		newFileAction = new NewFileAction();
		openFolderAction = new OpenFolderAction();
		refreshAction = new RefreshAction();
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		menuBar.addItem(subMenu, "New", newFileAction);
		menuBar.addSeparator(subMenu);
		menuBar.addItem(subMenu, "Open folder", openFolderAction);
		menuBar.addItem(subMenu, "Refresh", refreshAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("New", "/icons/Document.png", newFileAction, 0);
		toolbar.addSeparator();
		toolbar.addButton("Open folder", "/icons/Open file.png", openFolderAction, 0);
		toolbar.addButton("Refresh", "/icons/Refresh.png", refreshAction, 0);
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
			//fc.setCurrentDirectory(new File(System.getProperty("user.home")));
			fc.setCurrentDirectory(new File(DashAppModel.getInstance().getFolderPath()));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				if (file != null) {
					String path = file.getAbsolutePath();
					if (path != null) {
						DashAppController.getEventManager().updateFolderPath(path);
					}
				}
			}
		}
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
