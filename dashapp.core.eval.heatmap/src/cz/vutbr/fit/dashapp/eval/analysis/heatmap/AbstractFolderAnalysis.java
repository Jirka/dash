package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;

public abstract class AbstractFolderAnalysis extends AbstractAnalysis {
	
	public static final String DEFAULT_LOGIN_PREFIX = "x";
	public static final String DEFAULT_FILE_REGEX = DEFAULT_LOGIN_PREFIX + ".*";
	public static final String DEFAULT_OUTPUT_PATH = "../all/";
	
	protected DashboardCollection getDashboardCollection(WorkspaceFolder dashboardFolder, String fileRegex) {
		return new DashboardCollection(DashAppUtils.getDashboards(dashboardFolder.getChildren(DashboardFile.class, fileRegex, true)));
	}

}
