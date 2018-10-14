package cz.vutbr.fit.dashapp.view.tools;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.controller.IPropertyChangeListener;
import cz.vutbr.fit.dashapp.controller.PropertyChangeEvent;
import cz.vutbr.fit.dashapp.controller.EventManager.EventKind;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.model.VirtualDashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.view.IComponent;
import cz.vutbr.fit.dashapp.view.SideBar;

/**
 * View which contains dashboard file info.
 * 
 * @author Jiri Hynek
 *
 */
public class FileInfoTool extends AbstractGUITool implements IGUITool, IComponent, IPropertyChangeListener {

	private JTextArea text;
	private static final String DEFAULT_TEXT = "No file selected.";

	public FileInfoTool() {
		text = new JTextArea();
		text.setText(DEFAULT_TEXT);
		initListeners();
	}
	
	/**
	 * Initializes listeners
	 */
	private void initListeners() {
		// register model change listener
		DashAppController.getInstance().addPropertyChangeListener(this);
	}
	
	@Override
	public void provideSidebarItems(SideBar sideBar) {
		sideBar.addItem("file", getComponent());
	}
	
	@Override
	public JComponent getComponent() {
		return text;
	}
	
	@Override
	public void firePropertyChange(PropertyChangeEvent e) {
		if(e.propertyKind == EventKind.FILE_SELECTION_CHANGED ||
				EventKind.isModelChanged(e)) {
			IWorkspaceFile selectedFile = DashAppModel.getInstance().getSelectedFile();
			if(selectedFile != null) {
				StringBuffer fileInfo = new StringBuffer();
				fileInfo.append("File name: " + selectedFile.getFileName() + "\n");
				fileInfo.append("File type: " + getFileType(selectedFile) + "\n");
				
				if(selectedFile instanceof DashboardFile) {
					BufferedImage image = ((DashboardFile) selectedFile).getImage();
					if(image != null) {
						fileInfo.append('\n');
						fileInfo.append("image file: " + ((DashboardFile) selectedFile).getImageFile().getName() + "\n");
						fileInfo.append("image resolution: " + image.getWidth() + "x" + image.getHeight() + "\n");
						
					}
				}
				
				text.setText(fileInfo.toString());
			} else {
				text.setText(DEFAULT_TEXT);
			}
		}
	}

	private String getFileType(IWorkspaceFile workspaceFile) {
		if(workspaceFile instanceof WorkspaceFolder) {
			return "folder";
		} else if(workspaceFile instanceof DashboardFile) {
			return "dashboard";
		} else if(workspaceFile instanceof VirtualDashboardFile) {
			return "virtual dashboard";
		}
		return null;
	}

}
