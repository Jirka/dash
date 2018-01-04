package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.List;

import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdNormalization;

public class ThresholdAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Threshold Analysis";
	public static final String NAME = "threshold";
	public static final String FILE = "_" + NAME;
	
	private static final double DEFAULT_THRESHOLD = 0.8;
	
	// enable/disable according to requirements
	public boolean enable_basic_output = true;
	public boolean enable_basic_body_output = true;
	public boolean enable_borders_output = true;
	public boolean enable_borders_body_output = true;
	public boolean enable_act_folder_output = true;
	public boolean enable_all_folder_output = true;
	public boolean enable_custom_threshold = true;
	public double threshold = DEFAULT_THRESHOLD;
	public String inputFilesRegex = DEFAULT_FILE_REGEX;
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME;
	public String outputFile = FILE;
	
	public ThresholdAnalysis() {
		init();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
	@Override
	public void init() {		
	}
	
	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		DashboardCollection actDashboards = getDashboardCollection(actWorkspaceFolder, inputFilesRegex);
		int matrix[][] = actDashboards.printDashboards(null, false);
		if(enable_basic_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, matrix, FILE_SUFFIX_BASIC);
		}
		if(enable_basic_body_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, cropMatrix(actWorkspaceFolder, matrix), FILE_SUFFIX_BASIC_BODY);
		}
		matrix = actDashboards.printDashboards(null, true);
		if(enable_borders_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, matrix, FILE_SUFFIX_BORDERS);
		}
		if(enable_borders_body_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, cropMatrix(actWorkspaceFolder, matrix), FILE_SUFFIX_BORDERS_BODY);
		}
	}

	public void processHeatMap(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards,
			int[][] heatMap, String suffix) {
		int actDashboardsCount = actDashboards.length;
		double threshold = getThreshold(actWorkspaceFolder, actDashboards, heatMap);
		heatMap = GrayMatrix.update(heatMap, new ThresholdNormalization(threshold, actDashboardsCount), true);
		if(enable_act_folder_output || enable_all_folder_output) {
			BufferedImage image = GrayMatrix.printMatrixToImage(null, heatMap);
			String thresholdLabel = "_" + Double.toString(threshold).replace('.', ',');
			if(enable_act_folder_output) {
				printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath(), outputFile + thresholdLabel + suffix);
			}
			if(enable_all_folder_output) {
				printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + NAME + suffix, actWorkspaceFolder.getFileName() + outputFile + thresholdLabel + suffix);
			}
		}
	}
	
	private double getThreshold(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards, int[][] matrix) {
		if(enable_custom_threshold) {
			return threshold;
		} else {
			// provide dynamic threshold according to requirements
			return DEFAULT_THRESHOLD;
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		// do nothing
	}
}
