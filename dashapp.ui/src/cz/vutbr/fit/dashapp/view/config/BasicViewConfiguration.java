package cz.vutbr.fit.dashapp.view.config;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.model.DashAppModel;
import cz.vutbr.fit.dashapp.view.tools.AttachTool;
import cz.vutbr.fit.dashapp.view.tools.FileInfoTool;
import cz.vutbr.fit.dashapp.view.tools.FolderTool;
import cz.vutbr.fit.dashapp.view.tools.FullScreenTool;
import cz.vutbr.fit.dashapp.view.tools.GETypeTool;
import cz.vutbr.fit.dashapp.view.tools.HistoryTool;
import cz.vutbr.fit.dashapp.view.tools.IGUITool;
import cz.vutbr.fit.dashapp.view.tools.LoadTool;
import cz.vutbr.fit.dashapp.view.tools.SaveTool;
import cz.vutbr.fit.dashapp.view.tools.XMLTool;
import cz.vutbr.fit.dashapp.view.tools.ZoomTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.BoundTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.InsertTool;
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
	 * default project path
	 */
	public static final String DEFAULT_WORKSPACE_PATH = System.getProperty("user.home");
	
	public BasicViewConfiguration() {
		// set workspace path
		DashAppModel.getInstance().setFolderPath(getDefaultWorkspacePath());
		// initialize GUI tools
		initTools();
	}
	
	protected void initTools() {
		
		guiTools = new ArrayList<>();;
		// basic UI tools
		guiTools.add(new LoadTool());
		guiTools.add(new SaveTool());
		guiTools.add(new HistoryTool());
		guiTools.add(new ZoomTool());
		guiTools.add(new FullScreenTool());
		
		guiTools.add(new FolderTool());
		guiTools.add(new FileInfoTool());
		guiTools.add(new XMLTool());
		
		// canvas tools
		ButtonGroup buttonGroup = new ButtonGroup();
		guiTools.add(new ViewTool(true, true, buttonGroup));
		guiTools.add(new BoundTool(false, false, buttonGroup));
		guiTools.add(new SelectTool(false, false, buttonGroup));
		guiTools.add(new InsertTool(false, false, buttonGroup));
		
		guiTools.add(new AttachTool());
		guiTools.add(new GETypeTool());
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
	public String getDefaultWorkspacePath() {
		return DEFAULT_WORKSPACE_PATH;
	}

}
