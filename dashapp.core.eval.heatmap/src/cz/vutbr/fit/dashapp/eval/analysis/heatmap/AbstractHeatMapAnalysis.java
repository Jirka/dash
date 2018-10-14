package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.regex.Pattern;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractFolderAnalysis;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

/**
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractHeatMapAnalysis extends AbstractFolderAnalysis {
	
	public static final String DEFAULT_LOGIN_PREFIX = "x";
	public static final String DEFAULT_FILE_REGEX = DEFAULT_LOGIN_PREFIX + ".*";
	
	public static final String FILE_SUFFIX_BASIC = "";
	public static final String FILE_SUFFIX_BASIC_BODY = "_body";
	public static final String FILE_SUFFIX_BORDERS = "_borders";
	public static final String FILE_SUFFIX_BORDERS_BODY = "_borders_body";
	
	public static final String VARIABLE_ACT_FOLDER_NAME = "${folder}";
	
	protected Dashboard getBodyDashboard(WorkspaceFolder actWorkspaceFolder) {
		List<DashboardFile> dashboardCandidates = actWorkspaceFolder.getChildren(
				DashboardFile.class, actWorkspaceFolder.getFileName() + AbstractHeatMapAnalysis.FILE_SUFFIX_BASIC_BODY, false
		);
		if(dashboardCandidates != null && dashboardCandidates.size() == 1) {
			return dashboardCandidates.get(0).getDashboard(true);
		}
		return null;
	}
	
	protected int[][] cropMatrix(WorkspaceFolder actWorkspaceFolder, int[][] matrix) {
		Dashboard bodyDashboard = getBodyDashboard(actWorkspaceFolder);
		if(bodyDashboard != null) {
			Rectangle cropRectangle = new Rectangle(bodyDashboard.x, bodyDashboard.y, bodyDashboard.width, bodyDashboard.height);
			return GrayMatrix.cropMatrix(matrix, cropRectangle);
		}
		// copy
		return matrix;
	}
	
	protected String replaceVariables(String string, WorkspaceFolder actWorkspaceFolder) {
		string = string.replaceAll(Pattern.quote(VARIABLE_ACT_FOLDER_NAME), actWorkspaceFolder.getFileName());
		// add another variables if required
		return string;
	}
	
	protected void printImage(WorkspaceFolder actWorkspaceFolder, BufferedImage image, String folderPath, String fileName) {
		FileUtils.saveImage(image, replaceVariables(folderPath, actWorkspaceFolder), replaceVariables(fileName, actWorkspaceFolder));
	}
	
	protected void printTextFile(WorkspaceFolder actWorkspaceFolder, String fileOutput, String folderPath, String fileName) {
		FileUtils.saveTextFile(fileOutput, replaceVariables(folderPath, actWorkspaceFolder), replaceVariables(fileName, actWorkspaceFolder));
	}
	
	protected void printDashboard(WorkspaceFolder actWorkspaceFolder, Dashboard dashboard, String folderPath, String fileName) {
		FileUtils.saveDashboard(dashboard, replaceVariables(folderPath, actWorkspaceFolder), replaceVariables(fileName, actWorkspaceFolder));
	}

}
