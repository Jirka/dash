package cz.vutbr.fit.dashapp.eval.analysis.old;

import cz.vutbr.fit.dashapp.model.DashboardFile;

public interface IAnalysis {
	
	public String getName();
	
	public String analyze(DashboardFile dashboardFile);

}
