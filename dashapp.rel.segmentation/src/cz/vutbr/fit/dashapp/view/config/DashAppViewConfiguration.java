package cz.vutbr.fit.dashapp.view.config;

import java.util.ArrayList;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.view.tools.AttachTool;
import cz.vutbr.fit.dashapp.view.tools.FileInfoTool;
import cz.vutbr.fit.dashapp.view.tools.FolderTool;
import cz.vutbr.fit.dashapp.view.tools.FullScreenTool;
import cz.vutbr.fit.dashapp.view.tools.GETypeTool;
import cz.vutbr.fit.dashapp.view.tools.GrayScaleTool;
import cz.vutbr.fit.dashapp.view.tools.HistoryTool;
import cz.vutbr.fit.dashapp.view.tools.NewFileTool;
import cz.vutbr.fit.dashapp.view.tools.OpenTool;
import cz.vutbr.fit.dashapp.view.tools.ReloadTool;
import cz.vutbr.fit.dashapp.view.tools.SaveTool;
import cz.vutbr.fit.dashapp.view.tools.XMLTool;
import cz.vutbr.fit.dashapp.view.tools.ZoomTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.BoundTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.InsertTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.SelectTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.ViewTool;
import cz.vutbr.fit.tools.segmentation.SegmentationTool;
import cz.vutbr.fit.view.tools.image.HistogramTool;
import cz.vutbr.fit.view.tools.image.ImageTool;

public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	public static final String DEFAULT_WORKSPACE_PATH = "/home/jurij/work/dash/";
	
	/**
	 * version
	 */
	public static final String VERSION = "rel-segmentation";
	
	@Override
	public String getDefaultWorkspacePath() {
		return DEFAULT_WORKSPACE_PATH;
	}
	
	@Override
	public String getVersion() {
		return VERSION;
	}
	
	protected void initTools() {
		
		guiTools = new ArrayList<>();;
		// basic UI tools
		guiTools.add(new NewFileTool());
		guiTools.add(new OpenTool());
		guiTools.add(new ReloadTool());
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
		
		guiTools.add(new GrayScaleTool());
		
		guiTools.add(new ImageTool());
		guiTools.add(new HistogramTool());
		
		guiTools.add(new SegmentationTool());
	}
	

}
