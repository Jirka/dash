package cz.vutbr.fit.dashapp.view.config;

import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.AverageMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.CompareAllAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.CompareAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.EdgesAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.EntropyAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.HeatMapAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.SegmentationAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.FolderMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.ThresholdAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.WidgetAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.image.ImageActionFactory;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.ReduceColorAnalysis;
//import cz.vutbr.fit.dashapp.eval.analysis.heatmap.TestOfGEBoundariesAnalysis;
//import cz.vutbr.fit.dashapp.eval.analysis.heatmap.old.WidgetMetricAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.segmenation.SegmentationType;
import cz.vutbr.fit.dashapp.view.tools.FolderAnalysisTool;
import cz.vutbr.fit.dashapp.view.tools.ImageTool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	@Override
	protected String[] getDebugWorkspacePathSuffixes() {
		return new String[] {
				"/research/widget-based/gen/sort-id",
				"/widget-based/gen/sort-id"
		};
	}
	
	/**
	 * version
	 */
	public static final String EVAL_VERSION = "rel-heatmap";	
	
	protected void initTools() {
		super.initTools();
		
		guiTools.add(new ImageTool(true, ImageActionFactory.getRecommendedActions()));
		
		// folder analysis tools
		MetricType[] metricTypes = MetricType.values();
		SegmentationType[] segmentationTypes = SegmentationType.values();
		FolderAnalysisUI[] folderActionsUIs = new FolderAnalysisUI[] {
				new HeatMapAnalysisUI(),
				new EntropyAnalysisUI(),
				new ThresholdAnalysisUI(),
				new EdgesAnalysisUI(),
				new WidgetAnalysisUI(),
				new FolderAnalysisUI(new ReduceColorAnalysis()),
				new CompareAnalysisUI(),
				new CompareAllAnalysisUI(),
				new FolderMetricAnalysisUI(metricTypes),
				new AverageMetricAnalysisUI(metricTypes),
				//new FolderAnalysisUI(new WidgetMetricAnalysis()),
				//new FolderAnalysisUI(new TestOfGEBoundariesAnalysis()),
				new SegmentationAnalysisUI(segmentationTypes),
		};
		guiTools.add(new FolderAnalysisTool(true, folderActionsUIs));
	}
	
	@Override
	public String getVersion() {
		return EVAL_VERSION;
	}

}