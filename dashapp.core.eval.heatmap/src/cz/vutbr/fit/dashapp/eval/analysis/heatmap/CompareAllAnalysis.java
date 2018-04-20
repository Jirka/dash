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
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils.MeanStatistics;

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
	//public String outputFileSuffix = DEFAULT_OUT_FILE_SUFFIX;
	
	private Map<String, int[]> countVectors;
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
		countVectors = new LinkedHashMap<>();
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
		int[] countVector = new int[actDashboards.length];
		for (int i = 0; i < actDashboards.length; i++) {
			int[][] actDashboardMatrix = BooleanMatrix.toGrayMatrix(BooleanMatrix.printDashboard(actDashboards.dashboards[i], true, GEType.ALL_TYPES));
			// make diff
			int[][] cmpMatrix = GrayMatrix.compareMatrices(matrixRef, actDashboardMatrix);
			countVector[i] = GrayMatrix.getColorCount(cmpMatrix, GrayMatrix.BLACK);
		}
		if(enable_stats_output) {
			// make stats
			countVectors.put(actWorkspaceFolder.getFileName(), countVector);
			matrixSizes.put(actWorkspaceFolder.getFileName(), MatrixUtils.area(matrixRef));
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		if(enable_stats_output) {
			StringBuffer sb = new StringBuffer();
			/*for (Entry<String, MeanSatistics> entry : meanValues.entrySet()) {
				sb.append(entry.getKey() + " " + (GrayMatrix.WHITE - entry.getValue().mean)/GrayMatrix.WHITE + " " + entry.getValue().stdev / GrayMatrix.WHITE
						 + " " + entry.getValue().min + " " + entry.getValue().max+ "\n");
			}*/
			int[][] helpMatrix = new int[1][];
			sb.append("ID mean stdev\n");
			for (WorkspaceFolder workspaceFolder : analyzedFolders) {
				String key = workspaceFolder.getFileName();
				helpMatrix[0] = removeExtremeValues(countVectors.get(key));
				int matrixSize = matrixSizes.get(key);
				MeanStatistics stat = StatsUtils.meanStatistics(helpMatrix);
				double statMean = stat.mean/matrixSize;
				double statStdev = stat.stdev/matrixSize;
				sb.append(key + " " + statMean + " " + statStdev + " " +  "\n");
				//sb.append(key + " " + value + " " + valueCrop + " " + " " + (valueCrop-value) + " " + (1.0-valueCrop/value) + " " +  "\n");
			}
			printTextFile(actWorkspaceFolder, sb.toString(), actWorkspaceFolder.getPath() + "/" + outputFolderPath + "/" + NAME + getFileSuffix(), outputFile + getFileSuffix(actWorkspaceFolder));
		}
	}

	private int[] removeExtremeValues(int[] vector) {
		// TODO 
		return vector;
	}

}
