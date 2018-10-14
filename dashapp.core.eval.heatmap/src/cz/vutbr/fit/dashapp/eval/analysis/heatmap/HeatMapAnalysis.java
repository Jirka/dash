package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils.MeanStatistics;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class HeatMapAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Heatmap Analysis";
	public static final String NAME = "heatmap";
	public static final String FILE = "_" + NAME;
	
	// enable/disable according to requirements
	public boolean enable_basic_output = true;
	public boolean enable_basic_body_output = true;
	public boolean enable_borders_output = true;
	public boolean enable_borders_body_output = true;
	public boolean enable_act_folder_output = true;
	public boolean enable_all_folder_output = true;
	public boolean enable_stats_output = true;
	public String inputFilesRegex = DEFAULT_FILE_REGEX;
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME;
	public String outputFile = FILE;
	
	private Map<String, MeanStatistics> meanValues_basic;
	private Map<String, MeanStatistics> meanValues_basic_body;
	private Map<String, MeanStatistics> meanValues_borders;
	private Map<String, MeanStatistics> meanValues_borders_body;
	
	public HeatMapAnalysis() {
		init();
	}
	
	@Override
	public void init() {
		meanValues_basic = new LinkedHashMap<>();
		meanValues_basic_body = new LinkedHashMap<>();
		meanValues_borders = new LinkedHashMap<>();
		meanValues_borders_body = new LinkedHashMap<>();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		DashboardCollection actDashboards = getDashboardCollection(actWorkspaceFolder, inputFilesRegex);
		int matrix[][] = actDashboards.printDashboards(null, false);
		if(enable_basic_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, matrix, meanValues_basic, FILE_SUFFIX_BASIC);
		}
		if(enable_basic_body_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, cropMatrix(actWorkspaceFolder, matrix), meanValues_basic_body, FILE_SUFFIX_BASIC_BODY);
		}
		matrix = actDashboards.printDashboards(null, true);
		if(enable_borders_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, matrix, meanValues_borders, FILE_SUFFIX_BORDERS);
		}
		if(enable_borders_body_output) {
			processHeatMap(actWorkspaceFolder, actDashboards, cropMatrix(actWorkspaceFolder, matrix), meanValues_borders_body, FILE_SUFFIX_BORDERS_BODY);
		}
	}
	
	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		if(enable_basic_output) {
			sumarizeFolders(actWorkspaceFolder, analyzedFolders, meanValues_basic, FILE_SUFFIX_BASIC);
		}
		if(enable_basic_body_output) {
			sumarizeFolders(actWorkspaceFolder, analyzedFolders, meanValues_basic_body, FILE_SUFFIX_BASIC_BODY);
		}
		if(enable_borders_output) {
			sumarizeFolders(actWorkspaceFolder, analyzedFolders, meanValues_borders, FILE_SUFFIX_BORDERS);
		}
		if(enable_borders_body_output) {
			sumarizeFolders(actWorkspaceFolder, analyzedFolders, meanValues_borders_body, FILE_SUFFIX_BORDERS_BODY);
		}
	}
	
	public void processHeatMap(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards,
			int[][] heatMap, Map<String, MeanStatistics> meanValues, String suffix) {
		heatMap = GrayMatrix.normalize(heatMap, actDashboards.length, true);
		if(enable_stats_output) {
			MeanStatistics meanValue = StatsUtils.meanStatistics(heatMap);
			//System.out.println((int) meanValue.mean);
			meanValues.put(actWorkspaceFolder.getFileName(), meanValue);
		}
		if(enable_act_folder_output || enable_all_folder_output) {
			BufferedImage image = GrayMatrix.printMatrixToImage(null, heatMap);
			if(enable_act_folder_output) {
				printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath(), outputFile + suffix);
			}
			if(enable_all_folder_output) {
				printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + NAME + suffix, actWorkspaceFolder.getFileName() + outputFile + suffix);
			}
		}
	}
	
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders, 
			Map<String, MeanStatistics> meanValues, String suffix) {
		if(enable_stats_output) {
			StringBuffer sb = new StringBuffer();
			for (Entry<String, MeanStatistics> entry : meanValues.entrySet()) {
				sb.append(entry.getKey() + " " + (int) entry.getValue().mean + " " + (int) entry.getValue().stdev
						 + " " + entry.getValue().min + " " + entry.getValue().max+ "\n");
			}
			printTextFile(actWorkspaceFolder, sb.toString(), actWorkspaceFolder.getPath() + "/" + outputFolderPath + "/" + NAME + suffix, outputFile + suffix);
		}
	}

}
