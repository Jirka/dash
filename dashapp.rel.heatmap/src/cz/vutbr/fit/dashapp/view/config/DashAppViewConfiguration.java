package cz.vutbr.fit.dashapp.view.config;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.view.tools.AttachTool;
import cz.vutbr.fit.dashapp.view.tools.FileInfoTool;
import cz.vutbr.fit.dashapp.view.tools.FolderTool;
import cz.vutbr.fit.dashapp.view.tools.FullScreenTool;
import cz.vutbr.fit.dashapp.view.tools.GETypeTool;
import cz.vutbr.fit.dashapp.view.tools.GrayScaleTool;
//import cz.vutbr.fit.dashapp.eval.analysis.heatmap.TestOfGEBoundariesAnalysis;
//import cz.vutbr.fit.dashapp.eval.analysis.heatmap.old.WidgetMetricAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.view.tools.AnalysisTool;
import cz.vutbr.fit.dashapp.view.tools.HistoryTool;
import cz.vutbr.fit.dashapp.view.tools.NewFileTool;
import cz.vutbr.fit.dashapp.view.tools.OpenTool;
import cz.vutbr.fit.dashapp.view.tools.ReloadTool;
import cz.vutbr.fit.dashapp.view.tools.SaveTool;
import cz.vutbr.fit.dashapp.view.tools.XMLTool;
import cz.vutbr.fit.dashapp.view.tools.ZoomTool;
import cz.vutbr.fit.dashapp.view.tools.analysis.AbstractAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.AverageMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.CompareAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.EdgesAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.EntropyAnalysisUI;
//import cz.vutbr.fit.dashapp.view.tools.analysis.FolderAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.HeatMapAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.SimpleMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.ThresholdAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.WidgetAnalysisUI;
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
		LinkedList<AbstractAnalysisUI> heatmapActions = new LinkedList<>();
		heatmapActions.add(new HeatMapAnalysisUI());
		heatmapActions.add(new EntropyAnalysisUI());
		heatmapActions.add(new ThresholdAnalysisUI());
		heatmapActions.add(new EdgesAnalysisUI());
		heatmapActions.add(new WidgetAnalysisUI());
		heatmapActions.add(new CompareAnalysisUI());
		MetricType[] metricTypes = MetricType.values();
		heatmapActions.add(new SimpleMetricAnalysisUI(metricTypes));
		heatmapActions.add(new AverageMetricAnalysisUI(metricTypes));
		//heatmapActions.add(new FolderAnalysisUI(new WidgetMetricAnalysis()));
		//heatmapActions.add(new FolderAnalysisUI(new TestOfGEBoundariesAnalysis()));
		guiTools.add(new AnalysisTool(heatmapActions.toArray(new AbstractAnalysisUI[heatmapActions.size()])));
	}
	
	@Override
	public String getVersion() {
		return EVAL_VERSION;
	}

}