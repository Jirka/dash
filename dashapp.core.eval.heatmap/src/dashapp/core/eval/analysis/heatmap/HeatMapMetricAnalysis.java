package dashapp.core.eval.analysis.heatmap;

import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.eval.metric.MetricResult;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GrayBalance;
import cz.vutbr.fit.dashapp.eval.metric.raster.gray.GraySymmetry;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;

public class HeatMapMetricAnalysis extends AbstractAnalysis {
	
	private static final String LABEL = "Heatmap Metric Analysis";
	private static final String FILE = "_heatmap_metrics";
	
	Map<WorkspaceFolder, MetricResult[]> meanValues;
	Map<WorkspaceFolder, MetricResult[]> meanValuesCrop;
	
	public HeatMapMetricAnalysis() {
		meanValues = new LinkedHashMap<>();
		meanValuesCrop = new LinkedHashMap<>();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		int[][] heatMap = actDashboards.printDashboards(null, true);
		GrayMatrix.normalize(heatMap, actDashboards.length, false);
		
		meanValues.put(actWorkspaceFolder, measure(heatMap));
		
		DashboardCollection cropDashboard = DashAppUtils.makeDashboardCollection(
				actWorkspaceFolder.getChildren(
						DashboardFile.class, actWorkspaceFolder.getFileName().substring(1) + "-crop", false
				));
		if(cropDashboard.length == 1) {
			Dashboard dashboard = cropDashboard.dashboards[0];
			Rectangle cropRectangle = new Rectangle(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
			heatMap = GrayMatrix.cropMatrix(heatMap, cropRectangle);
			meanValuesCrop.put(actWorkspaceFolder, measure(heatMap));
		}
	}
	
	private MetricResult[] measure(int[][] matrix) {
		MetricResult[] results = new MetricResult[2];
		results[0] = new GrayBalance().measureGrayMatrix(matrix)[0];
		results[1] = new GraySymmetry().measureGrayMatrix(matrix)[0];
		return results;
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		StringBuffer sb = new StringBuffer();
		for (WorkspaceFolder workspaceFolder : analyzedFolders) {
			MetricResult[] entry = meanValues.get(workspaceFolder);
			MetricResult[] entryCrop = meanValuesCrop.get(workspaceFolder);
			sb.append(workspaceFolder.getFileName() + " " + entry[0].value + " " + entryCrop[0].value
					+ " " + entry[1].value + " " + entryCrop[1].value + "\n");
		}
		FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath() + "/_results", FILE + "_borders");
	}

}
