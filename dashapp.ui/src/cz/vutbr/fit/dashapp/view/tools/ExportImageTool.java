package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.view.MenuBar;

/**
 * Export image file from canvas.
 * 
 * @author Jiri Hynek
 *
 */
public class ExportImageTool extends AbstractGUITool implements IGUITool {
	
	public static final String LABEL = "Export Image";
	
	ExportActionAction exportAction;
	private List<AbstractButton> btnsExport;
	
	public ExportImageTool() {
		this(false);
	}
	
	public ExportImageTool(boolean addSeparator) {
		super(addSeparator);
		exportAction = new ExportActionAction();
		btnsExport = new ArrayList<>();
	}
	
	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("File");
		if(addSeparator && subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		AbstractButton btn = menuBar.addItem(subMenu, LABEL, exportAction);
		btnsExport.add(btn);
	}

	/*@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (addSeparator && toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		AbstractButton btn = toolbar.addButton(LABEL, "/icons/Save.png", exportAction, 0);
		btnsExport.add(btn);
	}*/

	/**
	 * Load/store action which handles save and load requests.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class ExportActionAction extends AbstractAction {

		/**
		 * UID
		 */
		private static final long serialVersionUID = 9065183817547467420L;

		public ExportActionAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			BufferedImage image = canvas.getImage();
			if(image != null) {
				JFileChooser fc = new JFileChooser();
				fc.setSelectedFile(new File(DashAppUtils.getSelectedDashboardFile().getFileName()));
				fc.showSaveDialog(null);
				try {
					File selectedFile = fc.getSelectedFile();
					if (selectedFile != null) {
						FileUtils.saveImage(image, selectedFile.getParentFile().getAbsolutePath(), selectedFile.getName());
					}
				} catch (Exception ex) {
					// do nothing
				}
			}
		}
	}

}
