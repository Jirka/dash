package cz.vutbr.fit.dashapp.view.config;

import cz.vutbr.fit.dashapp.eval.analysis.file.ColorAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.ColorfulnessAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.ComplexRasterAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.ComplexWidgetAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.GrayscaleAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.KimRasterAnalysis;
import cz.vutbr.fit.dashapp.eval.analysis.file.NgoAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.view.action.analysis.FileAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.analysis.file.FileMetricAnalysisUI;
import cz.vutbr.fit.dashapp.view.action.image.ImageActionFactory;
import cz.vutbr.fit.dashapp.view.tools.FileAnalysisTool;
import cz.vutbr.fit.dashapp.view.tools.ImageTool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	public static final String EVAL_VERSION = "rel-eval";
	
	protected void initTools() {
		super.initTools();
		
		guiTools.add(new ImageTool(true, ImageActionFactory.getRecommendedActions()));
		
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
	}
	
	@Override
	public String getVersion() {
		return EVAL_VERSION;
	}

}