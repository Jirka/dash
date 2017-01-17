package dashapp.core.eval.analysis.heatmap;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.image.GrayMatrix.EntrophyNormalization;
import cz.vutbr.fit.dashapp.image.GrayMatrix.ThresholdNormalization;
import cz.vutbr.fit.dashapp.image.MathUtils.MeanSatistics;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;

public class CompareAnalysis extends AbstractAnalysis {
	
	private static final String LABEL = "Compare Analysis";
	private static final String FILE = "_cmp_xc";
	
	Map<String, MeanSatistics> meanValues;
	Map<String, MeanSatistics> meanValuesCrop;
	Map<String, Double> countValues;
	Map<String, Double> countValuesCrop;
	
	public CompareAnalysis() {
		meanValues = new LinkedHashMap<>();
		countValues = new LinkedHashMap<>();
		meanValuesCrop = new LinkedHashMap<>();
		countValuesCrop = new LinkedHashMap<>();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		// make dashboards heatmap
		int actDashboardsCount = actDashboards.length;
		int[][] dashboardsMatrix = actDashboards.printDashboards(null);
		
		int[][] heatMatrix = GrayMatrix.normalize(dashboardsMatrix, actDashboards.length, true);
		double actHeatMean = GrayMatrix.meanValue(heatMatrix);
		int[][] entrophyMatrix = GrayMatrix.update(dashboardsMatrix, new EntrophyNormalization(actDashboardsCount), true);
		double actInversedEntrophyMean = GrayMatrix.WHITE-GrayMatrix.meanValue(entrophyMatrix);
		double actThreshlod = 1-((actHeatMean+actInversedEntrophyMean)/(2*GrayMatrix.WHITE));
		
		//GrayMatrix.normalize(heatMap, actDashboards.length, false);
		GrayMatrix.update(dashboardsMatrix, new ThresholdNormalization(actThreshlod, actDashboardsCount), false);
		
		DashboardCollection cropDashboard = DashAppUtils.makeDashboardCollection(actWorkspaceFolder.getChildren(DashboardFile.class, actWorkspaceFolder.getFileName() + "-crop", false));
		Rectangle cropRectangle = null;
		if(cropDashboard.length == 1) {
			Dashboard dashboard = cropDashboard.dashboards[0];
			cropRectangle = new Rectangle(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
		}
		
		// get referenced dashboard (dashboard name is same as folder name)
		DashboardCollection refDashboard = DashAppUtils.makeDashboardCollection(actWorkspaceFolder.getChildren(DashboardFile.class, actWorkspaceFolder.getFileName(), false));
		if(refDashboard.length == 1) {
			int[][] refMap = refDashboard.printDashboards(null);
			GrayMatrix.normalize(refMap, refDashboard.length, false);
			int[][] cmpMap = GrayMatrix.compareMatrices(dashboardsMatrix, refMap);
			//meanValues.put(actWorkspaceFolder.getFileName(), GrayMatrix.meanStatistics(cmpMap));
			countValues.put(actWorkspaceFolder.getFileName(), ((double) GrayMatrix.getColorCount(cmpMap, GrayMatrix.BLACK))/refDashboard.size());
			if(cropRectangle != null) {
				cmpMap = GrayMatrix.cropMatrix(cmpMap, cropRectangle);
				//meanValuesCrop.put(actWorkspaceFolder.getFileName(), GrayMatrix.meanStatistics(cmpMap));
				countValuesCrop.put(actWorkspaceFolder.getFileName(), ((double) GrayMatrix.getColorCount(cmpMap, GrayMatrix.BLACK))/refDashboard.size());//meanValues.put(actWorkspaceFolder.getFileName(), GrayMatrix.meanStatistics(cmpMap));
			}
			BufferedImage image = GrayMatrix.printMatrixToImage(null, cmpMap);
			FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE);
			FileUtils.saveImage(image, actWorkspaceFolder.getPath() + "/../_000-cmp", actWorkspaceFolder.getFileName() + FILE);
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		// do nothing
		StringBuffer sb = new StringBuffer();
		/*for (Entry<String, MeanSatistics> entry : meanValues.entrySet()) {
			sb.append(entry.getKey() + " " + (GrayMatrix.WHITE - entry.getValue().mean)/GrayMatrix.WHITE + " " + entry.getValue().stdev / GrayMatrix.WHITE
					 + " " + entry.getValue().min + " " + entry.getValue().max+ "\n");
		}*/
		for (WorkspaceFolder workspaceFolder : analyzedFolders) {
			String key = workspaceFolder.getFileName();
			Double value = countValues.get(key);
			Double valueCrop = countValuesCrop.get(key);
			sb.append(key + " " + value + " " + valueCrop + " " + " " + (valueCrop-value) + " " + (1.0-valueCrop/value) + " " +  "\n");
		}
		FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath(), "mean" + FILE);
	}

}
