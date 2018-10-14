package cz.vutbr.fit.dashapp.view.action.analysis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cz.vutbr.fit.dashapp.eval.analysis.IFileAnalysis;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.IWorkspaceFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.action.IDashActionUI;
import cz.vutbr.fit.dashapp.view.dialog.DashAppProgressDialog;
import cz.vutbr.fit.dashapp.view.dialog.GridLayoutFormDialog;
import cz.vutbr.fit.dashapp.view.dialog.SimpleDialogs;
import cz.vutbr.fit.dashapp.view.dialog.DashAppProgressDialog.DashAppTask;

/**
 * 
 * Analysis takes file in workspace and analyzes it.
 * If the file is DashboardFile, it analyzes it.
 * If the file is WorkspaseFolder it analyzes children (DashboardFiles). Dashboards can be filtered by PREFIX.
 * 
 * @author Jiri Hynek
 *
 */
public class FileAnalysisUI implements IDashActionUI {
	
	protected IFileAnalysis analysis;
	protected IWorkspaceFile workspaceFile;
	protected JCheckBox showInWindowCheckBox;
	protected boolean showInWindow;
	protected JCheckBox printToFileCheckBox;
	protected boolean printToFile;
	
	protected JTextField filesRegexTextField;
	public static final String DEFAULT_FILES_REGEX = ".*";
	private String filesRegex;
	
	public FileAnalysisUI(IFileAnalysis analysis) {
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
		workspaceFile = DashAppModel.getInstance().getSelectedFile();
		if(workspaceFile == null) {
			SimpleDialogs.report("No file selected!");
		} else {
			if(getSettings()) {
				FileAnalysisTask task = null;
				if(workspaceFile instanceof DashboardFile) {
					task = new FileAnalysisTask(analysis, (DashboardFile) workspaceFile, showInWindow, printToFile);
				} else if (workspaceFile instanceof WorkspaceFolder) {
					task = new FileAnalysisTask(analysis, (WorkspaceFile) workspaceFile, filesRegex, showInWindow, printToFile);
				}
				if(task != null) {
					DashAppProgressDialog monitor = new DashAppProgressDialog(DashAppView.getInstance().getFrame(), task);
					monitor.execute();
				}
			}
		}
	}
	
	private boolean getSettings() {
		FileAnalysisSettingsDialog settingsDialog = new FileAnalysisSettingsDialog(this);
		return settingsDialog.showConfirmDialog();
	}

	protected void getCustomSettings(JPanel panel) {
		// show in window
		showInWindowCheckBox = new JCheckBox("Show in Window", true);
		panel.add(showInWindowCheckBox);
		
		// print to file
		printToFileCheckBox = new JCheckBox("Print to file", false);
		panel.add(printToFileCheckBox);
		
		if(workspaceFile instanceof WorkspaceFolder) {
			// folder regex
			JLabel folderRegexLabel = new JLabel("Input folders regex:");
			filesRegexTextField = new JTextField(DEFAULT_FILES_REGEX);
			panel.add(folderRegexLabel);
	        panel.add(filesRegexTextField);
		}
	}
	
	protected void processCustomSettings() {
		showInWindow = showInWindowCheckBox.isSelected();
		printToFile = printToFileCheckBox.isSelected();
		
		if(workspaceFile instanceof WorkspaceFolder) {
			filesRegex = (String) filesRegexTextField.getText();
		}
	}
	
	/**
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public static class FileAnalysisSettingsDialog extends GridLayoutFormDialog {
		
		private FileAnalysisUI analysisUI;

		public FileAnalysisSettingsDialog(FileAnalysisUI analysisUI) {
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
	public static class FileAnalysisTask extends DashAppTask {
		
		private IFileAnalysis analysis;
		private IWorkspaceFile workspaceFile;
		private String filesRegex;
		private boolean showInWindow;
		private boolean printToFile;
		private String message;
		
        public FileAnalysisTask(IFileAnalysis analysis, DashboardFile workspaceFile, boolean showInWindow, boolean printToFile) {
        	super();
			this.analysis = analysis;
			this.workspaceFile = workspaceFile;
			this.showInWindow = showInWindow;
			this.printToFile = printToFile;
		}

		public FileAnalysisTask(IFileAnalysis analysis, WorkspaceFile workspaceFile, String filesRegex, boolean showInWindow, boolean printToFile) {
			super();
			this.analysis = analysis;
			this.workspaceFile = workspaceFile;
			this.filesRegex = filesRegex;
			this.showInWindow = showInWindow;
			this.printToFile = printToFile;
		}

		@Override
        public Void doInBackground() {
        	try {
    			StringBuffer result = new StringBuffer();
    			if(workspaceFile instanceof DashboardFile) {
    				DashboardFile dashboardFile = (DashboardFile) workspaceFile;
    				message = "file " + dashboardFile.getFileName();
    				result.append(analysis.processFile(dashboardFile));
    				setProgress(99);
    			} else if (workspaceFile instanceof WorkspaceFolder) {
    				List<DashboardFile> dashboards = ((WorkspaceFolder) workspaceFile).getChildren(DashboardFile.class, filesRegex.isEmpty() ? ".*" : filesRegex, true);
    				int progress = 0;
        			double folderCount = dashboards.size()+1;
        			setProgress(1);
    				for (DashboardFile dashboardFile : dashboards) {
    					result.append("------ ");
    					result.append(dashboardFile.getFileName());
    					result.append("\n");
	    				message = "file " + dashboardFile.getFileName();
	    				setProgress((int) (++progress/folderCount*100));
	    				result.append(analysis.processFile(dashboardFile));
	    				result.append("\n\n");
	    				if(isCancelled()) {
	    					return null;
	    				}
					}
    			}
    			
    			message = "finishing...";
    			setProgress(99);
    			
    			WorkspaceFolder workspaceFolder = workspaceFile.getModel().getWorkspaceFolder();
    			String fileName = workspaceFile.getFileName();
    			String resultString = result.toString();
    			
    			// print result to file
    			if(printToFile) {
    				FileUtils.saveTextFile(resultString, workspaceFolder.getPath(), fileName + "_" + analysis.getClass().getSimpleName());
    			}
    			
    			setProgress(100);
    			
    			// show result in window
    			if(showInWindow) {
    				showInWindow(resultString);
    			}
    			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
            return null;
        }
		
        private void showInWindow(String resultString) {
        	JFrame frame = new JFrame("Report");
			
			Toolkit toolkit = frame.getToolkit();
			Dimension screenSize = toolkit.getScreenSize();
			int offset_w = 600;
			int offset_h = 400;
			frame.setBounds((screenSize.width/2-offset_w/2), screenSize.height/2-offset_h/2, offset_w, offset_h);
			
			JTextArea area = new JTextArea(resultString);
			area.setEditable(false);
			frame.add(new JScrollPane(area), BorderLayout.CENTER);
			frame.setVisible(true);
		}

		@Override
        public void done() {
        	//DashAppController.getEventManager().refreshFolder();
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
