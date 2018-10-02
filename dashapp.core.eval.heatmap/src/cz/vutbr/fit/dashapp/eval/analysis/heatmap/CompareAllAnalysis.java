package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils;;

/**
 * Analysis compare file with all users descriptions of regions.
 * 
 * @author Jiri Hynek
 *
 */
public class CompareAllAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Compare Widgets (All)";
	public static final String NAME = "cmp-all";
	public static final String FILE = "_" + NAME;
	
	public static final String DEFAULT_FILE = VARIABLE_ACT_FOLDER_NAME;
	
	public String inputFileRef = DEFAULT_FILE;
	public String inputFilesRegex = DEFAULT_FILE_REGEX;
	public boolean enable_stats_output = true;
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME;
	public String outputFile = FILE;
	public boolean printAverageOutput = true;
	public boolean printAllOutput = true;
	//public String outputFileSuffix = DEFAULT_OUT_FILE_SUFFIX;
	
	private Map<String, double[]> diffVectors;
	private Map<String, Integer> matrixSizes;
	
	public CompareAllAnalysis() {
		init();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
	@Override
	public void init() {
		diffVectors = new LinkedHashMap<>();
		matrixSizes = new LinkedHashMap<>();
	}
	
	private String getFileSuffix(WorkspaceFolder actWorkspaceFolder) {
		// specify name of file suffix to recognize results
		String outputFileSuffix = "__" + inputFileRef + "__" + inputFilesRegex;
		return replaceVariables(outputFileSuffix, actWorkspaceFolder);
	}
	
	private String getFileSuffix() {
		// variables are removed for folder names
		String outputFileSuffix = "__" + inputFileRef + "__" + inputFilesRegex;
		return outputFileSuffix.replaceAll("\\$", "");
	}
	
	private String getRefDashboardFileName(WorkspaceFolder actWorkspaceFolder) {
		// specify dashboard to compare
		return replaceVariables(inputFileRef, actWorkspaceFolder);
	}
	
	private int[][] getDashboardMatrix(WorkspaceFolder actWorkspaceFolder, String name) {
		int[][] resultMatrix = null;
		List<DashboardFile> fileList = actWorkspaceFolder.getChildren(DashboardFile.class, name, false);
		if(fileList.size() == 1) {
			DashboardFile df = fileList.get(0);
			Dashboard dashboard = df.getDashboard(true);
			File xmlFile = df.getXmlFile();
			if(dashboard == null || !xmlFile.exists()) {
				resultMatrix = df.getImageMatrix();
				ColorMatrix.toGrayScale(resultMatrix, true, false);
			} else {
				resultMatrix = DashAppUtils.makeDashboardCollection(fileList).printDashboards(GEType.ALL_TYPES, false);
				GrayMatrix.normalize(resultMatrix, 1, false);
			}
		}
		
		if(resultMatrix == null) {
			resultMatrix = new int[0][0];
		}
		
		return resultMatrix;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		// get ref matrix
		String matrixNameRef = getRefDashboardFileName(actWorkspaceFolder);
		int[][] matrixRef = getDashboardMatrix(actWorkspaceFolder, matrixNameRef);
		// go through dashboards
		DashboardCollection actDashboards = getDashboardCollection(actWorkspaceFolder, inputFilesRegex);
		double[] diffVector = new double[actDashboards.length];
		for (int i = 0; i < actDashboards.length; i++) {
			int[][] actDashboardMatrix = BooleanMatrix.toGrayMatrix(BooleanMatrix.printDashboard(actDashboards.dashboards[i], true, GEType.ALL_TYPES));
			// make diff
			int[][] cmpMatrix = GrayMatrix.compareMatrices(matrixRef, actDashboardMatrix);
			diffVector[i] = StatsUtils.meanStatistics(cmpMatrix).mean;
		}
		if(enable_stats_output) {
			// make stats
			diffVectors.put(actWorkspaceFolder.getFileName(), diffVector);
			matrixSizes.put(actWorkspaceFolder.getFileName(), MatrixUtils.area(matrixRef));
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		if(enable_stats_output) {
			StringBuffer sb_avg = null;
			StringBuffer sb_all = null;
			
			if(printAverageOutput) {
				sb_avg = new StringBuffer();
				sb_avg.append("ID mean\n");
			}
			
			if(printAllOutput) {
				sb_all = new StringBuffer();
				sb_all.append("ID diffs...\n");
			}
			
			double[] diffVector;
			for (WorkspaceFolder workspaceFolder : analyzedFolders) {
				String key = workspaceFolder.getFileName();
				diffVector = diffVectors.get(key);
				if(printAverageOutput) {
					double sum = 0;
					for (double diff : diffVector) {
						sum += diff;
					}
					sb_avg.append(key + " " + (GrayMatrix.WHITE - (sum/diffVector.length))/GrayMatrix.WHITE + " " +  "\n");
				}
				
				if(printAllOutput) {
					sb_all.append(key);
					for (double diff : diffVector) {
						sb_all.append(" " + (GrayMatrix.WHITE - (diff))/GrayMatrix.WHITE);
					}
					sb_all.append("\n");
				}
			}
			
			if(printAverageOutput) {
				printTextFile(actWorkspaceFolder, sb_avg.toString(), actWorkspaceFolder.getPath() + "/" + outputFolderPath + "/" + NAME + getFileSuffix(), outputFile + getFileSuffix(actWorkspaceFolder) + "_avg");
			}
			
			if(printAllOutput) {
				printTextFile(actWorkspaceFolder, sb_all.toString(), actWorkspaceFolder.getPath() + "/" + outputFolderPath + "/" + NAME + getFileSuffix(), outputFile + getFileSuffix(actWorkspaceFolder) + "_all");
			}
		}
	}

}
