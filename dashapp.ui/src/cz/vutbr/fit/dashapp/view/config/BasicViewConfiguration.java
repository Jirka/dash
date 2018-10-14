package cz.vutbr.fit.dashapp.view.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.PathUtils;
import cz.vutbr.fit.dashapp.view.tools.AttachTool;
import cz.vutbr.fit.dashapp.view.tools.CropTool;
import cz.vutbr.fit.dashapp.view.tools.FileInfoTool;
import cz.vutbr.fit.dashapp.view.tools.FolderTool;
import cz.vutbr.fit.dashapp.view.tools.FullScreenTool;
import cz.vutbr.fit.dashapp.view.tools.GETypeTool;
import cz.vutbr.fit.dashapp.view.tools.GrayScaleTool;
import cz.vutbr.fit.dashapp.view.tools.HistoryTool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;
import cz.vutbr.fit.dashapp.view.tools.OpenTool;
import cz.vutbr.fit.dashapp.view.tools.ReloadTool;
import cz.vutbr.fit.dashapp.view.tools.NewFileTool;
import cz.vutbr.fit.dashapp.view.tools.SaveTool;
import cz.vutbr.fit.dashapp.view.tools.XMLTool;
import cz.vutbr.fit.dashapp.view.tools.ZoomTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.BoundTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.InsertTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.RectanglesTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.SelectTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.ViewTool;

/**
 * Global GUI settings.
 * 
 * @author Jiri Hynek
 *
 */
public class BasicViewConfiguration implements IViewConfiguration {
	
	/**
	 * list of GUI tools
	 */
	List<IGUITool> guiTools;
	
	/**
	 * default width
	 */
	public static final int WIDTH = 900;
	
	/**
	 * default height
	 */
	public static final int HEIGHT = 600;
	
	/**
	 * application name
	 */
	public static final String APP_NAME = "Dashboard analyzer";
	
	/**
	 * version
	 */
	public static final String VERSION = "rel-min";
	
	public BasicViewConfiguration() {
		// set workspace path
		DashAppModel model = DashAppModel.getInstance();
		model.setWorkspaceFolder(new WorkspaceFolder(model, new File(getDefaultWorkspacePath())));
		// initialize GUI tools
		initTools();
	}
	
	protected void initTools() {
		
		guiTools = new ArrayList<>();;
		// basic UI tools
		guiTools.add(new NewFileTool());
		guiTools.add(new OpenTool());
		guiTools.add(new ReloadTool());
		guiTools.add(new SaveTool(true));
		guiTools.add(new HistoryTool(true));
		guiTools.add(new ZoomTool(true));
		guiTools.add(new FullScreenTool(true));
		
		guiTools.add(new FolderTool());
		guiTools.add(new FileInfoTool());
		guiTools.add(new XMLTool());
		
		// canvas tools
		ButtonGroup buttonGroup = new ButtonGroup();
		guiTools.add(new ViewTool(true, true, buttonGroup));
		guiTools.add(new BoundTool(false, false, buttonGroup));
		guiTools.add(new SelectTool(false, false, buttonGroup));
		guiTools.add(new InsertTool(false, false, buttonGroup));
		guiTools.add(new RectanglesTool(false, false, buttonGroup));
		
		guiTools.add(new AttachTool(true));
		guiTools.add(new CropTool(true));
		guiTools.add(new GETypeTool());
		
		guiTools.add(new GrayScaleTool());
	}

	/**
	 * Returns tools providing additional GUI support. 
	 * 
	 * @return GUI tools
	 */
	public List<IGUITool> getGUITools() {
		return guiTools;
	}
	
	/**
	 * 
	 * @return preferred width of application
	 */
	public int getWidth() {
		return WIDTH;
	}
	
	/**
	 * 
	 * @return preferred height of application
	 */
	public int getHeight() {
		return HEIGHT;
	}
	
	/**
	 * 
	 * @return name of application
	 */
	public String getAppName() {
		return APP_NAME;
	}
	
	@Override
	public String getVersion() {
		return VERSION;
	}
	
	@Override
	public String getDefaultWorkspacePath() {
		// basic workspace path
		String basicWorkspacePath = replaceSeparators(getBasicWorkspacePath());
		
		// try debug workspace path suffixes
		String[] debugPathSuffixes = getDebugWorkspacePathSuffixes();
		if(debugPathSuffixes != null) {
			for (String debugPathSuffix : debugPathSuffixes) {
				String advancedWorkspacePath = replaceSeparators(basicWorkspacePath + debugPathSuffix);
				File asvancedWorkspacePathFile = new File(advancedWorkspacePath);
				if(asvancedWorkspacePathFile.exists() && asvancedWorkspacePathFile.isDirectory()) {
					return advancedWorkspacePath;
				}
			}
		}
		
		return basicWorkspacePath;
	}
	
	private String replaceSeparators(String path) {
		return path.replaceAll("/", File.separator);
	}
	
	protected String getBasicWorkspacePath() {
		// for debug purposes (private)
		String workspacePath = PathUtils.getDashSamplesPath();
		if(workspacePath != null) {
			return workspacePath;
		}
		// for eval purposes (public)
		workspacePath = PathUtils.getDashEvalPath();
		if(workspacePath != null) {
			return workspacePath;
		}
		// home path
		return PathUtils.DEFAULT_WORKSPACE_HOME_PATH;
	}

	protected String[] getDebugWorkspacePathSuffixes() {
		return null;
	}

}
