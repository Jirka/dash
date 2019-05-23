package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.List;

import cz.vutbr.fit.dashapp.image.util.AdaptiveThresholdUtils;
import cz.vutbr.fit.dashapp.image.util.PosterizationUtils;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class ReduceColorAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Reduce Colors";
	public static final String NAME = "image";
	public static final String FILE = "_" + NAME;
	
	public static final String DEFAULT_FILE = AbstractHeatMapAnalysis.VARIABLE_ACT_FOLDER_NAME;
	
	// enable/disable according to requirements
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME;
	//public String outputFileSuffix = DEFAULT_FILE;
	public String inputFile = DEFAULT_FILE;
	public boolean enable_custom_metrics;
	
	public ReduceColorAnalysis() {
		init();
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
	private String getDashboardFileName(WorkspaceFolder actWorkspaceFolder) {
		// specify dashboard to compare
		return replaceVariables(inputFile, actWorkspaceFolder);
	}
	
	private DashboardFile getDashboardFile(WorkspaceFolder actWorkspaceFolder, String name) {
		List<DashboardFile> fileList = actWorkspaceFolder.getChildren(DashboardFile.class, name, false);
		if(fileList.size() == 1) {
			return fileList.get(0);
		}
		return null;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		DashboardFile df = getDashboardFile(actWorkspaceFolder, getDashboardFileName(actWorkspaceFolder));
		
		int[][] matrix = df.getImageMatrix();
		
		// Gray
		int matrixGray[][] = ColorMatrix.toGrayScale(matrix, false, true);
		// 8 bit
		BufferedImage image = ColorMatrix.printMatrixToImage(null, matrixGray);
		printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath(), "_gray");
		
		// 4 bit
		int[][] posterizedMatrix = PosterizationUtils.posterizeMatrix(matrixGray, (int)(Math.pow(2, 4)), true);
		image = ColorMatrix.printMatrixToImage(null, posterizedMatrix);
		printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath(), "_posterized");
		
		// BW
		int[][] adaptiveMatrix = AdaptiveThresholdUtils.adaptiveThreshold(matrix, false, 0, 0, true);
		image = ColorMatrix.printMatrixToImage(null, adaptiveMatrix);
		printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath(), "_adaptive");
		
		
		// cache
		matrix = null;
		matrixGray = null;
		posterizedMatrix = null;
		adaptiveMatrix = null;
		df = null;
		System.gc();
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
	}

}
