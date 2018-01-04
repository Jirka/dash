package cz.vutbr.fit.dashapp.view.tools.analysis;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.util.DashAppProgressDialog;
import cz.vutbr.fit.dashapp.view.util.DashAppProgressDialog.DashAppTask;

public class FolderAnalysisUI extends AbstractAnalysisUI {
	
	AbstractAnalysis analysis;
	
	public static final String DEFAULT_FOLDER_REGEX = ".*";
	
	private String chosenFolderRegex;
	
	public FolderAnalysisUI(AbstractAnalysis analysis) {
		super();
		this.analysis = analysis;
	}
	
	@Override
	public String toString() {
		return this.analysis.toString();
	}

	@Override
	public void perform() {
		if(getSettings()) {
			FolderAnalysisTask task = new FolderAnalysisTask(analysis, chosenFolderRegex);
			DashAppProgressDialog monitor = new DashAppProgressDialog(DashAppView.getInstance().getFrame(), task);
			monitor.execute();
		}
	}
	
	private boolean getSettings() {
		// dialog panel
		JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); // TODO use better layout
		
		// folder regex
		JLabel folderRegexLabel = new JLabel("Input folders regex:");
		JTextField folderRegexInput = new JTextField(DEFAULT_FOLDER_REGEX);
		panel.add(folderRegexLabel);
        panel.add(folderRegexInput);
		
        // custom settings
		getCustomSettings(panel);

		int option = JOptionPane.showConfirmDialog(null, panel, analysis + " Settings", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			chosenFolderRegex = (String) folderRegexInput.getText();
			analysis.init();
			processCustomSettings();
			return true;
		}
		return false;
	}

	protected void getCustomSettings(JPanel panel) {
		// extend this class if required
	}
	
	protected void processCustomSettings() {
		// extend this class if required
	}

	static class FolderAnalysisTask extends DashAppTask {
		
		private AbstractAnalysis analysis;
		private String folderRegex;
		private String message;

		public FolderAnalysisTask(AbstractAnalysis analysis, String folderRegex) {
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
