package cz.vutbr.fit.dashapp.view.tools;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JMenu;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.dialog.SimpleDialogs;

/**
 * Filters area outside of dashboard body and exports it into a new image.
 * 
 * @author Jiri Hynek
 *
 */
public class CropTool extends AbstractGUITool implements IGUITool {
	
	public static final String LABEL = "Crop body";
	
	private static final String CROP_FILE_SUFFIX = "-crop";
	
	CropAction cropAction;
	
	public CropTool() {
		this(false);
	}
	
	public CropTool(boolean addSeparator) {
		super(addSeparator);
		cropAction = new CropAction();
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Edit");
		if(addSeparator && subMenu.getItemCount() > 0) {
			subMenu.addSeparator();
		}
		menuBar.addItem(subMenu, LABEL, cropAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (addSeparator && toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		toolbar.addButton(LABEL, "/icons/Cut.png", cropAction, 0);
	}

	/**
	 * Open folder action which handles open folder event.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class CropAction extends AbstractAction {
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -9097271483505866123L;

		public CropAction() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			IWorkspaceFile selectedFile = DashAppModel.getInstance().getSelectedFile();
			if(selectedFile instanceof DashboardFile) {
				// get files
				Dashboard dashboard = ((DashboardFile) selectedFile).getPhysicalDashboard();
				BufferedImage image = ((DashboardFile) selectedFile).getImage();
				if(dashboard != null && image != null) {
					String workspacePath = DashAppModel.getInstance().getWorkspaceFolder().getPath();
					String cropFileName = selectedFile.getFileName() + CROP_FILE_SUFFIX;
					
					// test if crop file does not exist
					boolean wrong_name = true;
					while (wrong_name) {
						String cropImageFileName = cropFileName + "." + FileUtils.IMAGE_EXTENSION;
						String cropDashboardFileName = cropFileName + "." + FileUtils.DASHBOARD_EXTENSION;
						if(new File(workspacePath + "/" + cropImageFileName).exists() || new File(workspacePath + "/" + cropDashboardFileName).exists()) {
							cropFileName = SimpleDialogs.inputText("File already exists. Select another name", cropFileName);
							if(cropFileName == null || cropFileName.isEmpty()) {
								return;
							}
						} else {
							wrong_name = false;
						}
					}
					
					// crop dashboard
					Rectangle cropRectangle = new Rectangle(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
					BufferedImage croppedImage = ColorMatrix.printMatrixToImage(null, GrayMatrix.cropMatrix(ColorMatrix.printImageToMatrix(image), cropRectangle));
					
					// save dashboard
					FileUtils.saveImage(croppedImage, workspacePath, cropFileName);
					
					Dashboard croppedDashboard = new Dashboard();
					croppedDashboard.setDimension(0, 0, dashboard.width, dashboard.height);
					
					for (GraphicalElement ge : dashboard.getChildren()) {
						// intersection with dashboard real area
						GraphicalElement intersectionGE = ge.intersectionGE(cropRectangle);
						
						// update with change of dashboard coordinates to [0,0].
						intersectionGE.x -= dashboard.x;
						intersectionGE.y -= dashboard.y;
						
						// attach to the new dashboard
						croppedDashboard.addChildGE(intersectionGE);
					}
					FileUtils.saveDashboard(croppedDashboard, workspacePath, cropFileName);
					
					// refresh workspace
					DashAppController.getEventManager().refreshFolder();
					
					// change selection
					DashAppController.getEventManager().changeSelectedWorkspaceFile(cropFileName, FileUtils.IMAGE_EXTENSION);
				}
			}
		}
		
	}

}
