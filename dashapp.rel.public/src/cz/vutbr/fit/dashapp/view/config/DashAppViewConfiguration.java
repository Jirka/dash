package cz.vutbr.fit.dashapp.view.config;

import java.util.ArrayList;

import javax.swing.ButtonGroup;

import cz.vutbr.fit.dashapp.eval.analysis.file.ColorAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.ColorfulnessAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.ComplexRasterAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.ComplexWidgetAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.GrayscaleAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.KimRasterAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.NgoAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.heatmap.ReduceColorAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.segmenation.SegmentationType;
import cz.vutbr.fit.dashapp.segmenation.methods.Experimental1;
import cz.vutbr.fit.dashapp.segmenation.methods.Experimental2;
import cz.vutbr.fit.dashapp.segmenation.methods.Experimental4;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.BottomUp;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.BottomUpRefactorized;
import cz.vutbr.fit.dashapp.view.action.analysis.FileAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.FolderAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.file.FileMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.AverageMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.CompareAllAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.CompareAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.EdgesAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.EntropyAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.FolderMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.HeatMapAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.SegmentationAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.ThresholdAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.heatmap.WidgetAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.image.ImageActionFactory;
import cz.vutbr.fit.dashapp.view.action.image.segmentation.SegmentationImageActionFactory;
import cz.vutbr.fit.dashapp.view.action.segmentatiion.DashboardSegmentationUI;
import cz.vutbr.fit.dashapp.view.action.segmentatiion.SegmentationAlgorithmUI;
import cz.vutbr.fit.dashapp.view.tools.AttachTool;
import cz.vutbr.fit.dashapp.view.tools.CropTool;
import cz.vutbr.fit.dashapp.view.tools.DownloadTool;
import cz.vutbr.fit.dashapp.view.tools.ExportImageTool;
import cz.vutbr.fit.dashapp.view.tools.FileAnalysisTool;
import cz.vutbr.fit.dashapp.view.tools.FileInfoTool;
import cz.vutbr.fit.dashapp.view.tools.FolderAnalysisTool;
import cz.vutbr.fit.dashapp.view.tools.FolderTool;
import cz.vutbr.fit.dashapp.view.tools.FullScreenTool;
import cz.vutbr.fit.dashapp.view.tools.GETypeTool;
import cz.vutbr.fit.dashapp.view.tools.GrayScaleTool;
import cz.vutbr.fit.dashapp.view.tools.HistoryTool;
import cz.vutbr.fit.dashapp.view.tools.ImageTool;
import cz.vutbr.fit.dashapp.view.tools.NewFileTool;
import cz.vutbr.fit.dashapp.view.tools.OpenTool;
import cz.vutbr.fit.dashapp.view.tools.ReloadTool;
import cz.vutbr.fit.dashapp.view.tools.SaveTool;
import cz.vutbr.fit.dashapp.view.tools.SegmentationTool;
import cz.vutbr.fit.dashapp.view.tools.XMLTool;
import cz.vutbr.fit.dashapp.view.tools.ZoomTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.BoundTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.InsertTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.RectanglesTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.SelectTool;
import cz.vutbr.fit.dashapp.view.tools.canvas.ViewTool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	/**
	 * version
	 */
	public static final String VERSION = "rel-public";
	
	public String getVersion() {
		return VERSION;
	}
	
	protected void initTools() {
		guiTools = new ArrayList<>();;
		// basic UI tools
		guiTools.add(new NewFileTool());
		guiTools.add(new OpenTool());
		guiTools.add(new DownloadTool());
		guiTools.add(new ReloadTool());
		guiTools.add(new SaveTool(true));
		guiTools.add(new ExportImageTool(true));
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
		
		// image tools
		guiTools.add(new ImageTool(true, ImageActionFactory.getRecommendedActions()));
		guiTools.add(new ImageTool(true, SegmentationImageActionFactory.getRecommendedActions()));
		
		// segmentation algorithm tool
		SegmentationAlgorithmUI[] segmentationUIs = new SegmentationAlgorithmUI[] {
				new DashboardSegmentationUI(),
				new SegmentationAlgorithmUI(new Experimental1()),
				new SegmentationAlgorithmUI(new Experimental2()),
				new SegmentationAlgorithmUI(new Experimental4()),
				new SegmentationAlgorithmUI(new BottomUp()),
				new SegmentationAlgorithmUI(new BottomUpRefactorized()),
		};
		guiTools.add(new SegmentationTool(true, segmentationUIs));
		
		// file analysis tools
		MetricType[] metricTypes = MetricType.values();
		FileAnalysisUI[] fileActionsUIs = new FileAnalysisUI[] {
				new FileMetricAnalysisUI(metricTypes),
				new FileAnalysisUI(new GrayscaleAnalysis()),
				new FileAnalysisUI(new ColorAnalysis()),
				new FileAnalysisUI(new ColorfulnessAnalysis()),
				new FileAnalysisUI(new KimRasterAnalysis()),
				new FileAnalysisUI(new NgoAnalysis()),
				new FileAnalysisUI(new ComplexRasterAnalysis()),
				new FileAnalysisUI(new ComplexWidgetAnalysis()),
		};
		guiTools.add(new FileAnalysisTool(true, fileActionsUIs));
		
		// folder heatmap analysis tools
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
		guiTools.add(new FolderAnalysisTool(false, folderActionsUIs));
		
		
	}

}
