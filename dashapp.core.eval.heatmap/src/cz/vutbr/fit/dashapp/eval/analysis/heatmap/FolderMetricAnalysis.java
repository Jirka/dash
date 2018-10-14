package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult.MetricResultAttribute;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GrayBalance;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GraySymmetry;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.FileUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class FolderMetricAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Simple Metric Analysis";
	public static final String NAME = "measure";
	public static final String FILE = "_" + NAME;
	
	public static final String DEFAULT_FILE = HeatMapAnalysis.FILE + FILE_SUFFIX_BORDERS;
	public static final MetricType[] DEFAULT_METRICS = new MetricType[] { 
			MetricType.GrayBalance,
			MetricType.GraySymmetry,
	};
	
	// enable/disable according to requirements
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME + "/simple";
	//public String outputFileSuffix = DEFAULT_FILE;
	public String inputFile = DEFAULT_FILE;
	public boolean enable_custom_metrics;
	public List<MetricType> metricTypes = Arrays.asList(DEFAULT_METRICS);
	
	private Map<IMetric, Map<WorkspaceFolder, MetricResult[]>> results;
	
	public FolderMetricAnalysis() {
		init();
	}
	
	@Override
	public void init() {
		if(results != null) {
			results.clear();
		}
		results = null;
	}
	
	private Map<IMetric, Map<WorkspaceFolder, MetricResult[]>> getResultsMap() {
		if(results == null) {
			results = new LinkedHashMap<>();
			if(enable_custom_metrics) {
				for (MetricType metricType : metricTypes) {
					try {
						results.put((IMetric) metricType.createMetric(), new LinkedHashMap<>());
					} catch (Exception e) {
						System.err.println("Unable to create metric: " + metricType);
					}
				}
			} else {
				// specify own metrics if required
				results.put(new GrayBalance(), new LinkedHashMap<>());
				results.put(new GraySymmetry(), new LinkedHashMap<>());
			}
		}
		return results;
	};
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
	private String getFileSuffix() {
		// specify name of file suffix to recognize results
		return inputFile.replaceAll("\\$", "");
	}
	
	private String getDashboardFileName(WorkspaceFolder actWorkspaceFolder) {
		// specify dashboard to compare
		return replaceVariables(inputFile, actWorkspaceFolder);
	}
	
	@SuppressWarnings("unused")
	private int[][] getDashboardMatrix(WorkspaceFolder actWorkspaceFolder, String name) {
		int[][] resultMatrix = null;
		List<DashboardFile> fileList = actWorkspaceFolder.getChildren(DashboardFile.class, name, false);
		if(fileList.size() == 1) {
			resultMatrix = fileList.get(0).getImageMatrix();
		}
		
		if(resultMatrix == null) {
			resultMatrix = new int[0][0];
		}
		
		return resultMatrix;
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
		//int[][] heatMap = getDashboardMatrix(actWorkspaceFolder, actDashboards, getDashboardFileName(actWorkspaceFolder, actDashboards));
		DashboardFile df = getDashboardFile(actWorkspaceFolder, getDashboardFileName(actWorkspaceFolder));
		if(df != null) {
			Map<IMetric, Map<WorkspaceFolder, MetricResult[]>> resultMap = getResultsMap();
			for (Entry<IMetric, Map<WorkspaceFolder, MetricResult[]>> metricEntry : resultMap.entrySet()) {
				metricEntry.getValue().put(actWorkspaceFolder, metricEntry.getKey().measure(df));
			}
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		Map<IMetric, Map<WorkspaceFolder, MetricResult[]>> resultMap = getResultsMap();
		for (Entry<IMetric, Map<WorkspaceFolder, MetricResult[]>> metricEntry : resultMap.entrySet()) {
			sumarizeFolders(actWorkspaceFolder, analyzedFolders, metricEntry);
		}
	}

	private void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders,
			Entry<IMetric, Map<WorkspaceFolder, MetricResult[]>> metricEntry) {
		StringBuffer sb = new StringBuffer();
		// generate output
		Map<WorkspaceFolder, MetricResult[]> metricResultsMap = metricEntry.getValue();
		for (WorkspaceFolder workspaceFolder : analyzedFolders) {
			MetricResult[] metricResults = metricResultsMap.get(workspaceFolder);
			// description
			if(metricResults != null) {
				if(sb.length() == 0) {
					sb.append("ID\t");
					MetricResult.printLine(sb, metricResults, MetricResultAttribute.INITIALS);
				}
				
				// values
				sb.append(workspaceFolder.getFileName() + "\t");
				MetricResult.printLine(sb, metricResults, MetricResultAttribute.VALUE);
			}
		}
		// save output
		IMetric metric = metricEntry.getKey();
		FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath() + "/" + outputFolderPath + "/" + getFileSuffix(), metric.getName());
	}

}
