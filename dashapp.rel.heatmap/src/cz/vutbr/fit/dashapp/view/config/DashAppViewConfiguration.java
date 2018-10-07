package cz.vutbr.fit.dashapp.view.config;

import java.util.ArrayList;

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
import cz.vutbr.fit.dashapp.segmenation.SegmentationType;
import cz.vutbr.fit.dashapp.view.tools.FolderAnalysisTool;
import cz.vutbr.fit.dashapp.view.tools.HistoryTool;
import cz.vutbr.fit.dashapp.view.tools.NewFileTool;
import cz.vutbr.fit.dashapp.view.tools.OpenTool;
import cz.vutbr.fit.dashapp.view.tools.ReloadTool;
import cz.vutbr.fit.dashapp.view.tools.SaveTool;
import cz.vutbr.fit.dashapp.view.tools.XMLTool;
import cz.vutbr.fit.dashapp.view.tools.ZoomTool;
import cz.vutbr.fit.dashapp.view.tools.analysis.FolderAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.AverageMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.CompareAllAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.CompareAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.EdgesAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.EntropyAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.HeatMapAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.SegmentationAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.SimpleMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.ThresholdAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.analysis.heatmap.WidgetAnalysisUI;
import cz.vutbr.fit.dashapp.view.tools.canvas.BoundTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.InsertTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.SelectTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.ViewTool;
import cz.vutbr.fit.dashapp.view.tools.image.old.OldImageTool;

public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	@Override
	public String getDefaultWorkspacePath() {
		return super.getDefaultWorkspacePath() + "/research/widget-based/gen/sort-id";
	}
	
	/**
	 * version
	 */
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
		
		guiTools.add(new OldImageTool());
		
		// folder analysis tools
		MetricType[] metricTypes = MetricType.values();
		SegmentationType[] segmentationTypes = SegmentationType.values();
		FolderAnalysisUI[] folderActionsUIs = new FolderAnalysisUI[] {
				new HeatMapAnalysisUI(),
				new EntropyAnalysisUI(),
				new ThresholdAnalysisUI(),
				new EdgesAnalysisUI(),
				new WidgetAnalysisUI(),
				new CompareAnalysisUI(),
				new CompareAllAnalysisUI(),
				new SimpleMetricAnalysisUI(metricTypes),
				new AverageMetricAnalysisUI(metricTypes),
				//new FolderAnalysisUI(new WidgetMetricAnalysis()),
				//new FolderAnalysisUI(new TestOfGEBoundariesAnalysis()),
				new SegmentationAnalysisUI(segmentationTypes),
		};
		guiTools.add(new FolderAnalysisTool(folderActionsUIs));
	}
	
	@Override
	public String getVersion() {
		return EVAL_VERSION;
	}

}