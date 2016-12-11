package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;

/**
 * Load dashboard support.
 * 
 * @author Jiri Hynek
 *
 */
public class OpenTool extends AbstractGUITool implements IGUITool {
	
	OpenFolderAction openFolderAction;
	
	public OpenTool() {
		openFolderAction = new OpenFolderAction();
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		menuBar.addItem(subMenu, "Open folder", openFolderAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton("Open folder", "/icons/Open file.png", openFolderAction, 0);
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
			fc.setCurrentDirectory(DashAppModel.getInstance().getWorkspaceFolder().getFile());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				if (file != null) {
					String path = file.getAbsolutePath();
					if (path != null) {
						DashAppController.getEventManager().
						updateWorkspaceFolder(new WorkspaceFolder(DashAppModel.getInstance(), new File(path)));
					}
				}
			}
		}
	}

}
