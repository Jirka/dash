package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils.MeanStatistics;

public class CompareAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Compare 2 Files";
	public static final String NAME = "cmp";
	public static final String FILE = "_" + NAME;
	
	public static final String DEFAULT_FILE_1 = VARIABLE_ACT_FOLDER_NAME;
	public static final String DEFAULT_FILE_2 = HeatMapAnalysis.FILE + FILE_SUFFIX_BORDERS;
	//public static final String DEFAULT_OUT_FILE_SUFFIX = "__" + DEFAULT_FILE_1 + "__" + DEFAULT_FILE_2;
	
	public boolean enable_act_folder_output = true;
	public boolean enable_all_folder_output = true;
	public boolean enable_stats_output = true;
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME;
	public String outputFile = FILE;
	//public String outputFileSuffix = DEFAULT_OUT_FILE_SUFFIX;
	public String inputFile1 = DEFAULT_FILE_1;
	public String inputFile2 = DEFAULT_FILE_2;
	
	private Map<String, MeanStatistics> meanValues;
	private Map<String, Double> countValues;
	
	public CompareAnalysis() {
		init();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
	@Override
	public void init() {
		meanValues = new LinkedHashMap<>();
		countValues = new LinkedHashMap<>();
	}
	
	private String getFileSuffix(WorkspaceFolder actWorkspaceFolder) {
		// specify name of file suffix to recognize results
		String outputFileSuffix = "__" + inputFile1 + "__" + inputFile2;
		return replaceVariables(outputFileSuffix, actWorkspaceFolder);
	}
	
	private String getFileSuffix() {
		// variables are removed for folder names
		String outputFileSuffix = "__" + inputFile1 + "__" + inputFile2;
		return outputFileSuffix.replaceAll("\\$", "");
	}
	
	private String getFirstDashboardFileName(WorkspaceFolder actWorkspaceFolder) {
		// specify dashboard to compare
		return replaceVariables(inputFile1, actWorkspaceFolder);
	}
	
	private String getSecondDashboardFileName(WorkspaceFolder actWorkspaceFolder) {
		// specify dashboard to compare
		return replaceVariables(inputFile2, actWorkspaceFolder);
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
		// get names
		String matrixName1 = getFirstDashboardFileName(actWorkspaceFolder);
		String matrixName2 = getSecondDashboardFileName(actWorkspaceFolder);
		// get matrices
		int[][] matrix1 = getDashboardMatrix(actWorkspaceFolder, matrixName1);
		int[][] matrix2 = getDashboardMatrix(actWorkspaceFolder, matrixName2);
		// make diff
		int[][] cmpMatrix = GrayMatrix.compareMatrices(matrix1, matrix2);
		if(enable_stats_output) {
			// make stats
			meanValues.put(actWorkspaceFolder.getFileName(), StatsUtils.meanStatistics(cmpMatrix));
			countValues.put(actWorkspaceFolder.getFileName(), ((double) GrayMatrix.getColorCount(cmpMatrix, GrayMatrix.WHITE))/
					(MatrixUtils.area(cmpMatrix)));
		}
		if(enable_act_folder_output || enable_all_folder_output) {
			// create and save image
			BufferedImage image = GrayMatrix.printMatrixToImage(null, cmpMatrix);
			if(enable_act_folder_output) {
				printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath(), outputFile + getFileSuffix(actWorkspaceFolder));
			}
			if(enable_all_folder_output) {
				printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + NAME + getFileSuffix(), actWorkspaceFolder.getFileName() + outputFile + getFileSuffix(actWorkspaceFolder));
			}
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
			sb.append("ID mean stdev white\n");
			for (WorkspaceFolder workspaceFolder : analyzedFolders) {
				String key = workspaceFolder.getFileName();
				MeanStatistics stat = meanValues.get(key);
				double statMean = (GrayMatrix.WHITE - stat.mean)/GrayMatrix.WHITE;
				double statStdev = stat.stdev / GrayMatrix.WHITE;
				Double value = countValues.get(key);
				sb.append(key + " " + statMean + " " + statStdev + " " + (value) + " " +  "\n");
				//sb.append(key + " " + value + " " + valueCrop + " " + " " + (valueCrop-value) + " " + (1.0-valueCrop/value) + " " +  "\n");
			}
			printTextFile(actWorkspaceFolder, sb.toString(), actWorkspaceFolder.getPath() + "/" + outputFolderPath + "/" + NAME + getFileSuffix(), outputFile + getFileSuffix(actWorkspaceFolder));
		}
	}

}
