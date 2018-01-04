package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.eval.metric.MetricType;
import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoBalance;
import cz.vutbr.fit.dashapp.eval.util.MetricCalculator;
import cz.vutbr.fit.dashapp.eval.util.MetricResultsCollection;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.IDashboardFile;
import cz.vutbr.fit.dashapp.model.VirtualDashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils.MeanStatistics;

public class AverageMetricAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Average Metric Analysis";
	public static final String NAME = "measure";
	public static final String FILE = "_" + NAME;
	
	public static final MetricType[] DEFAULT_METRICS = new MetricType[] { 
			MetricType.NgoBalance,
	};
	
	public static final int DEFAULT_FILTER_EXTREME_RESULTS = 4;
	
	// enable/disable according to requirements
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME + "/average";
	//public String outputFileSuffix = DEFAULT_FILE;
	public String inputFilesRegex = DEFAULT_FILE_REGEX;
	
	public boolean enable_custom_metrics = true;	
	public List<MetricType> metricTypes = Arrays.asList(DEFAULT_METRICS);
	public boolean enable_image_cache = true;
	public int filter_extreme_results = DEFAULT_FILTER_EXTREME_RESULTS;
	public boolean print_mean = true;
	public boolean print_variance = false;
	public boolean print_stdev = true;
	public boolean print_min = false;
	public boolean print_max = false;
	public boolean enable_basic_output = true;
	public boolean enable_basic_body_output = false;

	private IMetric[] metrics = null;
	private Map<WorkspaceFolder, Map<IMetric, MeanStatistics[]>> results_basic;
	private Map<WorkspaceFolder, Map<IMetric, MeanStatistics[]>> results_body;
	
	public AverageMetricAnalysis() {
		init();
	}
	
	@Override
	public void init() {
		results_basic = new HashMap<>();
		results_body = new HashMap<>();
		metrics = null;
	}
	
	private IMetric[] getMetrics() {
		if(metrics == null) {
			if(enable_custom_metrics) {
				metrics = new IMetric[metricTypes.size()];
				int i = 0;
				for (MetricType metricType : metricTypes) {
					try {
						metrics[i] = (IMetric) metricType.createMetric();
					} catch (Exception e) {
						System.err.println("Unable to create metric: " + metricType);
					}
					i++;
				}
			} else {
				// specify own metrics if required
				metrics = new IMetric[] {
						new NgoBalance()
				};
			}
		}
		return metrics;
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}
	
	private String getFileSuffix() {
		// specify name of file suffix to recognize results
		return inputFilesRegex.replaceAll("\\$", "");
	}
	
	private String getDashboardRegex(WorkspaceFolder actWorkspaceFolder) {
		// specify dashboard to compare
		return replaceVariables(inputFilesRegex, actWorkspaceFolder);
	}
	
	private DashboardCollection getDashboardFile(WorkspaceFolder actWorkspaceFolder, String fileRegex) {;
		return getDashboardCollection(actWorkspaceFolder, fileRegex);
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		DashboardCollection dc = getDashboardFile(actWorkspaceFolder, getDashboardRegex(actWorkspaceFolder));
		
		if(enable_image_cache) {
			cacheImages(dc);
		}
		
		if(enable_basic_output) {
			processFolder(actWorkspaceFolder, dc, results_basic);
		}
		
		if(enable_basic_body_output) {
			cropDashboardCollection(actWorkspaceFolder, dc);
			processFolder(actWorkspaceFolder, dc, results_body);
		}
		
		if(enable_image_cache) {
			clearCache(dc);
		}
	}
	
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection dc, Map<WorkspaceFolder, Map<IMetric, MeanStatistics[]>> results) {
		if(dc != null) {
			// measure
			results.put(actWorkspaceFolder, measure(actWorkspaceFolder, dc));
		}
	}
	
	private Map<IMetric, MeanStatistics[]> measure(WorkspaceFolder actWorkspaceFolder, DashboardCollection dc) {
		Map<IMetric, MetricResultsCollection> resultsCollection = MetricCalculator.measure(dc, getMetrics(), GEType.ALL_TYPES);
		Map<IMetric, MeanStatistics[]> stats = MetricCalculator.statistics(resultsCollection, filter_extreme_results);
		return stats;
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		if(enable_basic_output) {
			sumarizeFolders(actWorkspaceFolder, analyzedFolders, results_basic, "basic");
		}
		
		if(enable_basic_body_output) {
			sumarizeFolders(actWorkspaceFolder, analyzedFolders, results_body, "body");
		}
	}
	
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders, Map<WorkspaceFolder, Map<IMetric, MeanStatistics[]>> results, String spec) {
		IMetric[] metrics = getMetrics();
		for (IMetric metric : metrics) {
			sumarizeFolders(actWorkspaceFolder, analyzedFolders, metric, results, spec);
		}
	}

	private void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders,
			IMetric metric, Map<WorkspaceFolder, Map<IMetric, MeanStatistics[]>> results, String spec) {
		StringBuffer sb = new StringBuffer();
		// generate output
		for (WorkspaceFolder workspaceFolder : analyzedFolders) {
			MeanStatistics[] statsList = results.get(workspaceFolder).get(metric);
			if(statsList != null) {
				// description
				if(sb.length() == 0) {
					sb.append("ID");
					for (int i = 0; i < statsList.length; i++) {
						if(print_mean) {
							sb.append("\tmean");
						}
						if(print_variance) {
							sb.append("\tvariance");
						}
						if(print_stdev) {
							sb.append("\tstdev");
						}
						if(print_min) {
							sb.append("\tmin");
						}
						if(print_max) {
							sb.append("\tmax");
						}
					}
				}
				
				// values
				sb.append("\n" + workspaceFolder.getFileName());
				for (MeanStatistics stats : statsList) {
					if(print_mean) {
						sb.append("\t" + stats.mean);
					}
					if(print_variance) {
						sb.append("\t" + stats.variance);
					}
					if(print_stdev) {
						sb.append("\t" + stats.stdev);
					}
					if(print_min) {
						sb.append("\t" + stats.min);
					}
					if(print_max) {
						sb.append("\t" + stats.max);
					}
				}
			}
		}
		// save output
		FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath() + "/" + outputFolderPath + "/" + getFileSuffix() + "/filter_" + filter_extreme_results + "/" + spec, metric.getName());
	}
	
	private void cropDashboardCollection(WorkspaceFolder actWorkspaceFolder, DashboardCollection dc) {
		Dashboard bodyDashboard = getBodyDashboard(actWorkspaceFolder);
		Rectangle cropRectangle = new Rectangle(bodyDashboard.x, bodyDashboard.y, bodyDashboard.width, bodyDashboard.height);
		VirtualDashboardFile cachedVDFCrop = null; 
		for (int i = 0; i < dc.length; i++) {
			VirtualDashboardFile vdfCrop = cachedVDFCrop;
			if(vdfCrop == null) {
				IDashboardFile oldDF = dc.dashboards[i].getDashboardFile();
				vdfCrop = new VirtualDashboardFile(oldDF.getModel());
				vdfCrop.setImage(ColorMatrix.printMatrixToImage(null, ColorMatrix.printImageToMatrix(oldDF.getImage(), bodyDashboard)));
				if(enable_image_cache) {
					cachedVDFCrop = vdfCrop;
					oldDF.clearCache();
				}
			}
			
			// update dashboard
			dc.dashboards[i] = dc.dashboards[i].copy(cropRectangle, 2);
			dc.dashboards[i].setDashboardFile(vdfCrop);
		}
	}
	
	private void clearCache(DashboardCollection analyzedDashboardCollection) {
		Dashboard[] dashboards = analyzedDashboardCollection.dashboards;
		if(dashboards.length > 0) {
			IDashboardFile dashboardFile = dashboards[0].getDashboardFile();
			dashboardFile.clearCache();
			for (int i = 0; i < dashboards.length; i++) {
				analyzedDashboardCollection.dashboards[i] = null;
			}
		}
		System.gc();
	}

	private void cacheImages(DashboardCollection analyzedDashboardCollection) {
		Dashboard[] dashboards = analyzedDashboardCollection.dashboards;
		if(dashboards.length > 0) {
			IDashboardFile dashboardFile = dashboards[0].getDashboardFile();
			VirtualDashboardFile vdf = new VirtualDashboardFile(dashboardFile.getModel());
			vdf.setImage(dashboardFile.getImage());
			for (int j = 0; j < dashboards.length; j++) {
				Dashboard dashboardCopy = dashboards[j].copy();
				dashboardCopy.setDashboardFile(vdf);
				analyzedDashboardCollection.dashboards[j] = dashboardCopy;
			}
		}
	}

}
