package cz.vutbr.fit.dashapp.view.config;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.view.tools.AttachTool;
import cz.vutbr.fit.dashapp.view.tools.FolderTool;
import cz.vutbr.fit.dashapp.view.tools.FullScreenTool;
import cz.vutbr.fit.dashapp.view.tools.GrayScaleTool;
import cz.vutbr.fit.dashapp.view.tools.HistoryTool;
import cz.vutbr.fit.dashapp.view.tools.OpenTool;
import cz.vutbr.fit.dashapp.view.tools.SaveTool;
import cz.vutbr.fit.dashapp.view.tools.XMLTool;
import cz.vutbr.fit.dashapp.view.tools.ZoomTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.InsertTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.SelectTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.ViewTool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	public static final String DEFAULT_WORKSPACE_PATH = "dashboards/";
	
	public static final String IIS_VERSION = "rel-iis-2016";	
	
	@Override
	public String getDefaultWorkspacePath() {
		File f = new File(System.getProperty("java.class.path"));
		File dir = f.getAbsoluteFile().getParentFile();
		return dir.toString()   + File.separator + DEFAULT_WORKSPACE_PATH;
	}
	
	protected void initTools() {
		guiTools = new ArrayList<>();;
		// basic UI tools
		guiTools.add(new OpenTool());
		guiTools.add(new SaveTool(true));
		guiTools.add(new HistoryTool(true));
		guiTools.add(new ZoomTool(true));
		guiTools.add(new FullScreenTool(true));
		
		guiTools.add(new FolderTool());
		guiTools.add(new XMLTool());
		
		// canvas tools
		ButtonGroup buttonGroup = new ButtonGroup();
		guiTools.add(new ViewTool(true, true, buttonGroup));
		guiTools.add(new SelectTool(false, false, buttonGroup));
		guiTools.add(new InsertTool(false, false, buttonGroup));
		
		guiTools.add(new AttachTool(true));
		
		guiTools.add(new GrayScaleTool());
	}
	
	@Override
	public String getVersion() {
		return IIS_VERSION;
	}

}
