package cz.vutbr.fit.dashapp.view.config;//import cz.vutbr.fit.dashapp.eval.analysis.heatmap.old.WidgetMetricAnalysis;


/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppThesisViewConfiguration extends DashAppViewConfiguration {
	
	@Override
	protected String[] getDebugWorkspacePathSuffixes() {
		return new String[] {
				"/workspace/metrics/gen",
		};
	}
	
	/**
	 * version
	 */
	public static final String EVAL_VERSION = "rel-thesis";
	
	@Override
	public String getVersion() {
		return EVAL_VERSION;
	}

}