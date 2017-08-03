package cz.vutbr.fit.dashapp.view.config;

public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	public static final String DEFAULT_WORKSPACE_PATH = "/home/jurij/work/dashboards/evals/widget-based/random";
	
	@Override
	public String getDefaultWorkspacePath() {
		return DEFAULT_WORKSPACE_PATH;
	}

}
