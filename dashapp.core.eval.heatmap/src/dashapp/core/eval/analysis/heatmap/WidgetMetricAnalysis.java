package dashapp.core.eval.analysis.heatmap;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.IMetric;
import cz.vutbr.fit.dashapp.eval.metric.MetricCalculator;
import cz.vutbr.fit.dashapp.eval.metric.MetricResultsCollection;
import cz.vutbr.fit.dashapp.eval.metric.widget.IWidgetMetric;
import cz.vutbr.fit.dashapp.eval.metric.widget.basic.Area;
import cz.vutbr.fit.dashapp.eval.metric.widget.basic.WidgetCount;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoBalance;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoCohesion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoDensity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoEconomy;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoEquilibrium;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoHomogenity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoProportion;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoRegularity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSequence;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSimplicity;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoSymmetry;
import cz.vutbr.fit.dashapp.eval.metric.widget.ngo.NgoUnity;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.image.MathUtils.MeanSatistics;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;

public class WidgetMetricAnalysis extends AbstractAnalysis {
	
	private static final String LABEL = "Widget Metric Analysis";
	private static final String FILE = "_widget_stats";
	
	IWidgetMetric[] metrics = new IWidgetMetric[] { 
			new WidgetCount(),
			new Area(),
			new NgoBalance(),
			new NgoCohesion(),
			new NgoDensity(),
			new NgoEconomy(),
			new NgoEquilibrium(),
			new NgoHomogenity(),
			new NgoProportion(),
			new NgoRegularity(),
			new NgoSimplicity(),
			new NgoSequence(),
			new NgoSymmetry(),
			new NgoUnity(),
	};
	
	Map<WorkspaceFolder, Map<IWidgetMetric, MeanSatistics[]>> meanValues;
	Map<WorkspaceFolder, Map<IWidgetMetric, MeanSatistics[]>> meanValuesCrop;
	Map<WorkspaceFolder, Map<IWidgetMetric, MeanSatistics[]>> meanValuesCropFull;
	
	public WidgetMetricAnalysis() {
		meanValues = new LinkedHashMap<>();
		meanValuesCrop = new LinkedHashMap<>();
		meanValuesCropFull = new LinkedHashMap<>();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		
		meanValues.put(actWorkspaceFolder, analyzeDashboards(actWorkspaceFolder, actDashboards, "_all"));
		
		// crop by rectangle
		DashboardCollection cropDashboard = DashAppUtils.makeDashboardCollection(actWorkspaceFolder.getChildren(DashboardFile.class, actWorkspaceFolder.getFileName() + "-crop", false));
		if(cropDashboard.length == 1) {
			Dashboard dashboard = cropDashboard.dashboards[0];
			Rectangle cropRectangle = new Rectangle(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
			
			Dashboard[] dashboards = actDashboards.dashboards;
			List<Dashboard> cropDashboards = new LinkedList<>();
			List<Dashboard> filteredDashboards = new LinkedList<>();
			for (int i = 0; i < dashboards.length; i++) {
				Dashboard a = dashboards[i].copy(cropRectangle, 2);
				Dashboard b = dashboards[i].filter(cropRectangle, 2);
				if(a.n(null) != b.n(null)) {
					System.out.println(actWorkspaceFolder.getFileName() + " " + dashboards[i].getDashboardFile().getFileName());
					//printDashboards(actWorkspaceFolder, new DashboardCollection(new Dashboard[] { a, b }), "_a");
					//printDashboards(actWorkspaceFolder, new DashboardCollection(new Dashboard[] { a }), "_abcd");
					//printDashboards(actWorkspaceFolder, new DashboardCollection(new Dashboard[] { b }), "_efgh");
				}
				cropDashboards.add(a);
				filteredDashboards.add(b);
			}
			meanValuesCrop.put(actWorkspaceFolder, analyzeDashboards(actWorkspaceFolder, new DashboardCollection(cropDashboards), "_crop"));
			meanValuesCropFull.put(actWorkspaceFolder, analyzeDashboards(actWorkspaceFolder, new DashboardCollection(filteredDashboards), "_crop_full"));
		}
	}

	private Map<IWidgetMetric, MeanSatistics[]> analyzeDashboards(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards, String label) {
		//printDashboards(actWorkspaceFolder, actDashboards, label);
		return calculateMetrics(actWorkspaceFolder, actDashboards, label);
	}

	private Map<IWidgetMetric, MeanSatistics[]> calculateMetrics(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards, String label) {
		MetricCalculator calculator = new MetricCalculator();
		Map<IWidgetMetric, MetricResultsCollection> resultsCollection = calculator.measure(
				actDashboards,
				metrics,
				GEType.ALL_TYPES
		);
		Map<IWidgetMetric, MeanSatistics[]> stats = calculator.statistics(resultsCollection);
		return stats;
	}

	@SuppressWarnings("unused")
	private void printDashboards(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards, String label) {
		int[][] printed = actDashboards.printDashboards(null);
		GrayMatrix.normalize(printed, actDashboards.length, false);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, printed);
		FileUtils.saveImage(image, actWorkspaceFolder.getPath() + "/../_crop/", actWorkspaceFolder.getFileName() + label);
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		
		for (IMetric metric : metrics) {
			StringBuffer sb = new StringBuffer();
			boolean titleMade = false;
			for (WorkspaceFolder workspaceFolder : analyzedFolders) {
				sb.append(workspaceFolder.getFileName());
				sb.append("\n");
				MeanSatistics[] stats = meanValues.get(workspaceFolder).get(metric);
				MeanSatistics[] statsCrop = meanValuesCrop.get(workspaceFolder).get(metric);
				MeanSatistics[] statsCropFull = meanValuesCropFull.get(workspaceFolder).get(metric);
				if(!titleMade) {
					titleMade = true;
				}
				formatStats(stats, sb);
				formatStats(statsCrop, sb);
				formatStats(statsCropFull, sb);
				sb.append("-----------------\n");
			}
			FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath() + "/" + FILE, metric.getClass().getSimpleName());
		}
	}

	private void formatStats(MeanSatistics[] stats, StringBuffer sb) {
		for (MeanSatistics stat : stats) {
			sb.append(stat.mean + "\t" + stat.stdev + " | \t");
		}
		sb.append("\n");
	}

}
