package cz.vutbr.fit.dashapp.eval.analysis;

import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;

/**
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractFolderAnalysis extends AbstractAnalysis implements IFolderAnalysis {
	
	public static final String DEFAULT_OUTPUT_PATH = "../all/";
	
	protected DashboardCollection getDashboardCollection(WorkspaceFolder dashboardFolder, String fileRegex) {
		return new DashboardCollection(DashAppUtils.getDashboards(dashboardFolder.getChildren(DashboardFile.class, fileRegex, true)));
	}

}
