package cz.vutbr.fit.dashapp.view.tools;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import cz.vutbr.fit.dashapp.controller.DashAppController;
import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.view.DashAppView;
import cz.vutbr.fit.dashapp.view.MenuBar;
import cz.vutbr.fit.dashapp.view.ToolBar;
import cz.vutbr.fit.dashapp.view.tools.AbstractGUITool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;
import cz.vutbr.fit.dashapp.view.util.DashAppProgressDialog;
import cz.vutbr.fit.dashapp.view.util.DashAppProgressDialog.DashAppTask;

public class AnalysisTool extends AbstractGUITool implements IGUITool {
	
	public static final String LABEL = "Make Analysis";
	public static final String ICON = "/icons/Statistics.png";
	
	HeatmapCoreAction coreAction;
	private AbstractAnalysis[] heatmapActions;
	
	public AnalysisTool(AbstractAnalysis[] heatmapActions) {
		this.coreAction = new HeatmapCoreAction();
		this.heatmapActions = heatmapActions;
	}

	@Override
	public void provideMenuItems(MenuBar menuBar) {
		JMenu subMenu = menuBar.getSubMenu("Eval");
		menuBar.addItem(subMenu, LABEL, coreAction);
	}

	@Override
	public void provideToolbarItems(ToolBar toolbar) {
		if (toolbar.getAmountOfItems() > 0) {
			toolbar.addSeparator();
		}
		JButton btn = toolbar.addButton(LABEL, ICON, coreAction, 0);
		btn.setText("Heatmaps");
	}
	
	/**
	 * Go through all folders in workspace and makes heatmaps of dashboards stored in selected folder.
	 * It expects dashboards to differ only in XML description.
	 * Dashboards can be filtered by PREFIX.
	 * 
	 * @author Jiri Hynek
	 *
	 */
	public class HeatmapCoreAction extends AbstractAction {
		
		public static final String PREFIX = "x";
		
		/**
		 * UID
		 */
		private static final long serialVersionUID = -8793205852749826603L;

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractAnalysis heatMapAction = chooseAction();
			if(heatMapAction != null) {
				String folder = selectFolder();
				if(folder != null) {
					HeatmapTask task = new HeatmapTask(heatMapAction, folder, getPrefix() + ".*");
					DashAppProgressDialog monitor = new DashAppProgressDialog(DashAppView.getInstance().getFrame(), task);
					monitor.execute();
				}
			}
		}
		
		protected AbstractAnalysis chooseAction() {
			AbstractAnalysis resultAction = null;
			if(heatmapActions.length == 0) {
				
			} else {
				resultAction = (AbstractAnalysis) JOptionPane.showInputDialog(null, "Choose action",
						"The Choice of an Action", JOptionPane.QUESTION_MESSAGE, null,
						heatmapActions, heatmapActions[0]);
			    System.out.println(resultAction);
			}
			return resultAction;
		}
		
		protected String selectFolder() {
			String resultFolder = JOptionPane.showInputDialog(null, "Choose folder",
						"The Choice of a Folder", JOptionPane.QUESTION_MESSAGE);
			System.out.println(resultFolder);
			return resultFolder;
		}
		
		protected String getPrefix() {
			return PREFIX;
		}
	}
	
	static class HeatmapTask extends DashAppTask {
		
		private AbstractAnalysis analysis;
		private String folderRegex;
		private String fileRegex;
		private String message;

		public HeatmapTask(AbstractAnalysis analysis, String folderRegex, String fileRegex) {
			super();
			this.analysis = analysis;
			this.folderRegex = folderRegex;
			this.fileRegex = fileRegex;
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
    				DashboardCollection actDashboards = new DashboardCollection(DashAppUtils.getDashboards(dashboardFolder.getChildren(DashboardFile.class, fileRegex, true)));
    				this.analysis.processFolder(dashboardFolder, actDashboards);
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
        	System.out.println("heatmap");
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
