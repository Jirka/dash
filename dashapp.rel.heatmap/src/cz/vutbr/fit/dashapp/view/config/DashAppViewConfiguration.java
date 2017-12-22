package cz.vutbr.fit.dashapp.view.config;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.CompareAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.EdgesAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.EntrophyAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.HeatMapAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.HeatMapMetricAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.ThresholdAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.WidgetAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.WidgetMetricAnalysis;
import cz.vutbr.fit.dashapp.view.tools.AttachTool;
import cz.vutbr.fit.dashapp.view.tools.FileInfoTool;
import cz.vutbr.fit.dashapp.view.tools.FolderTool;
import cz.vutbr.fit.dashapp.view.tools.FullScreenTool;
import cz.vutbr.fit.dashapp.view.tools.GETypeTool;
import cz.vutbr.fit.dashapp.view.tools.GrayScaleTool;
import cz.vutbr.fit.dashapp.view.tools.AnalysisTool;
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
import cz.vutbr.fit.view.tools.image.ImageTool;

public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	public static final String EVAL_VERSION = "rel-heatmap";	
	
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
		
		// heat map tools
		LinkedList<AbstractAnalysis> heatmapActions = new LinkedList<>();
		heatmapActions.add(new HeatMapAnalysis());
		heatmapActions.add(new EntrophyAnalysis());
		heatmapActions.add(new ThresholdAnalysis());
		heatmapActions.add(new EdgesAnalysis());
		heatmapActions.add(new WidgetAnalysis());
		heatmapActions.add(new CompareAnalysis());
		heatmapActions.add(new HeatMapMetricAnalysis());
		heatmapActions.add(new WidgetMetricAnalysis());
		guiTools.add(new AnalysisTool(heatmapActions.toArray(new AbstractAnalysis[heatmapActions.size()])));
	}
	
	@Override
	public String getVersion() {
		return EVAL_VERSION;
	}

}