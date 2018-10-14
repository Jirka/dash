package cz.vutbr.fit.dashapp.eval.analysis;

import cz.vutbr.fit.dashapp.model.DashboardFile;

/**
 * 
 * @author Jiri Hynek
 *
 */
public interface IFileAnalysis extends IAnalysis {
	
	public String processFile(DashboardFile dashboardFile);

}
