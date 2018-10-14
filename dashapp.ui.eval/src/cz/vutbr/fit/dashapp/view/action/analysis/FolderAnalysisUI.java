package cz.vutbr.fit.dashapp.view.action.analysis;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.eval.analysis.IFolderAnalysis;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.action.IDashActionUI;
import cz.vutbr.fit.dashapp.view.dialog.DashAppProgressDialog;
import cz.vutbr.fit.dashapp.view.dialog.GridLayoutFormDialog;
import cz.vutbr.fit.dashapp.view.dialog.DashAppProgressDialog.DashAppTask;

/**
 * 
 * Analysis goes through all folders in workspace and makes heatmaps of dashboards stored in selected folder.
 * It expects dashboards to differ only in XML description.
 * Dashboards can be filtered by PREFIX.
 * 
 * @author Jiri Hynek
 *
 */
public class FolderAnalysisUI implements IDashActionUI {
	
	protected IFolderAnalysis analysis;
	protected JTextField folderRegexInput;
	
	public static final String DEFAULT_FOLDER_REGEX = ".*";
	
	private String chosenFolderRegex;
	
	public FolderAnalysisUI(IFolderAnalysis analysis) {
		super();
		this.analysis = analysis;
	}
	
	public String getLabel() {
		return this.analysis.toString();
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public void perform(ActionEvent e) {
		if(getSettings()) {
			FolderAnalysisTask task = new FolderAnalysisTask(analysis, chosenFolderRegex);
			DashAppProgressDialog monitor = new DashAppProgressDialog(DashAppView.getInstance().getFrame(), task);
			monitor.execute();
		}
	}
	
	private boolean getSettings() {
		FolderAnalysisSettingsDialog settingsDialog = new FolderAnalysisSettingsDialog(this);
		return settingsDialog.showConfirmDialog();
	}

	protected void getCustomSettings(JPanel panel) {
		// folder regex
		JLabel folderRegexLabel = new JLabel("Input folders regex:");
		folderRegexInput = new JTextField(DEFAULT_FOLDER_REGEX);
		panel.add(folderRegexLabel);
        panel.add(folderRegexInput);
		
        // extend this class if required
	}
	
	protected void processCustomSettings() {
		chosenFolderRegex = (String) folderRegexInput.getText();
		analysis.init();

		// extend this class if required
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class FolderAnalysisSettingsDialog extends GridLayoutFormDialog {
		
		private FolderAnalysisUI analysisUI;

		public FolderAnalysisSettingsDialog(FolderAnalysisUI analysisUI) {
			super(analysisUI.analysis + " Settings");
			this.analysisUI = analysisUI;
		}
		
		@Override
		protected void addCustomWidgets(JPanel panel) {
			analysisUI.getCustomSettings(panel);
		}
		
		@Override
		protected boolean processCustomWidgets() {
			analysisUI.processCustomSettings();
			
			return true;
		}
	}

	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class FolderAnalysisTask extends DashAppTask {
		
		private IFolderAnalysis analysis;
		private String folderRegex;
		private String message;

		public FolderAnalysisTask(IFolderAnalysis analysis, String folderRegex) {
			super();
			this.analysis = analysis;
			this.folderRegex = folderRegex;
		}
		
        @Override
        public Void doInBackground() {
        	try {
        		DashAppModel model = DashAppModel.getInstance();
    			WorkspaceFolder workspaceFolder = model.getWorkspaceFolder();
    			List<WorkspaceFolder> dashboardGroups = workspaceFolder.getChildren(WorkspaceFolder.class, folderRegex.isEmpty() ? ".*" : folderRegex, true);
    			int progress = 0;
    			double folderCount = dashboardGroups.size()+1;
    			setProgress(1);
    			
    			for (WorkspaceFolder dashboardFolder : dashboardGroups) {
    				message = "folder " + dashboardFolder.getFileName();
    				this.analysis.processFolder(dashboardFolder);
    				progress++;
    				setProgress((int) (progress/folderCount*100));
    				System.out.println(dashboardFolder.toString());
    				if(isCancelled()) {
    					return null;
    				}
    			}
    			message = "finishing...";
    			this.analysis.sumarizeFolders(workspaceFolder, dashboardGroups);
    			setProgress(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
            return null;
        }

        @Override
        public void done() {
        	DashAppController.getEventManager().refreshFolder();
        	System.out.println("folder analysis done");
        }

		@Override
		public Object getMainLabel() {
			return "Analyzing dashoards";
		}

		@Override
		public Object getMessage() {
			return message;
		}		
    }

}
