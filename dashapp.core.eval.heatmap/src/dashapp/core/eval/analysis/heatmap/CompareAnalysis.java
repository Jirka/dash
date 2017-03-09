package dashapp.core.eval.analysis.heatmap;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.image.GrayMatrix.EntrophyNormalization;
import cz.vutbr.fit.dashapp.image.GrayMatrix.ThresholdCalculator;
import cz.vutbr.fit.dashapp.image.MathUtils;
import cz.vutbr.fit.dashapp.image.MathUtils.MeanSatistics;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;

public class CompareAnalysis extends AbstractAnalysis {
	
	private static final String LABEL = "Compare Analysis";
	private static final String FILE = "_cmp";
	
	private static final boolean excludeBorders = false;
	private static final boolean widgetInsteadOfThreshold = false;
	private static final boolean printImages = false;
	private static final boolean makeMeanStats = false;
	
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
		int[][] printMatrix = actDashboards.printDashboards(null, excludeBorders);
		int[][] thresholdMatrix;
		
		if(!widgetInsteadOfThreshold) {
			// make threshold matrix
			int[][] heatMatrix = GrayMatrix.normalize(printMatrix, actDashboards.length, true);
			double actHeatMean = MathUtils.meanValue(heatMatrix);
			int[][] entrophyMatrix = GrayMatrix.update(printMatrix, new EntrophyNormalization(actDashboardsCount), true);
			double actInversedEntrophyMean = GrayMatrix.WHITE-MathUtils.meanValue(entrophyMatrix);
			double actThreshlod = (actHeatMean+actInversedEntrophyMean)/2;
			thresholdMatrix = GrayMatrix.update(heatMatrix, new ThresholdCalculator((int) actThreshlod), true);
			//double actThreshlod = 1-((actHeatMean+actInversedEntrophyMean)/(2*GrayMatrix.WHITE));
			//int[][] thresholdMatrix = GrayMatrix.update(printMatrix, new ThresholdNormalization(actThreshlod, actDashboardsCount), true);
		} else {
			// use dashboard already made by widget detected instead
			DashboardCollection widgetDashboard = DashAppUtils.makeDashboardCollection(
					actWorkspaceFolder.getChildren(
							DashboardFile.class, "_widget_tb", false
					));
			thresholdMatrix = widgetDashboard.printDashboards(null, excludeBorders);
			GrayMatrix.normalize(thresholdMatrix, 1, false);
		}
		
		// get crop rectangle
		DashboardCollection cropDashboard = DashAppUtils.makeDashboardCollection(
				actWorkspaceFolder.getChildren(
						DashboardFile.class, actWorkspaceFolder.getFileName().substring(1) + "-crop", false
				));
		Rectangle cropRectangle = null;
		if(cropDashboard.length == 1) {
			Dashboard dashboard = cropDashboard.dashboards[0];
			cropRectangle = new Rectangle(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
		}
		
		// get referenced dashboard (dashboard name is same as folder name)
		DashboardCollection refDashboard = DashAppUtils.makeDashboardCollection(
				actWorkspaceFolder.getChildren(
						DashboardFile.class, actWorkspaceFolder.getFileName().substring(1), false
				));
		if(refDashboard.length == 1) {
			// basic compare statistics
			int[][] refMap = refDashboard.printDashboards(null, excludeBorders);
			GrayMatrix.normalize(refMap, refDashboard.length, false);
			int[][] cmpMap = GrayMatrix.compareMatrices(thresholdMatrix, refMap);
			if(makeMeanStats) {
				meanValues.put(actWorkspaceFolder.getFileName(), MathUtils.meanStatistics(cmpMap));
			}
			countValues.put(actWorkspaceFolder.getFileName(), ((double) GrayMatrix.getColorCount(cmpMap, GrayMatrix.BLACK))/
					(actDashboards.size()));
			if(printImages) {
				BufferedImage image = GrayMatrix.printMatrixToImage(null, cmpMap);
				//FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE + getStatsNameSuffix());
				FileUtils.saveImage(image, actWorkspaceFolder.getPath() + "/../_paper", actWorkspaceFolder.getFileName() + FILE + getStatsNameSuffix());
			}
			// crop statistics
			if(cropRectangle != null) {
				int[][] cmpMapCrop = GrayMatrix.cropMatrix(cmpMap, cropRectangle);
				if(makeMeanStats) {
					meanValuesCrop.put(actWorkspaceFolder.getFileName(), MathUtils.meanStatistics(cmpMapCrop));
				}
				countValuesCrop.put(actWorkspaceFolder.getFileName(), ((double) GrayMatrix.getColorCount(cmpMapCrop, GrayMatrix.BLACK))
						/(cropRectangle.width*cropRectangle.height));//meanValues.put(actWorkspaceFolder.getFileName(), GrayMatrix.meanStatistics(cmpMap));
				if(printImages) {
					BufferedImage image = GrayMatrix.printMatrixToImage(null, cmpMapCrop);
					FileUtils.saveImage(image, actWorkspaceFolder.getPath() + "/../_paper", actWorkspaceFolder.getFileName() + FILE + getStatsNameSuffix() + "_crop");
				}
			}
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
			MeanSatistics stat = meanValues.get(key);
			MeanSatistics statCrop = meanValuesCrop.get(key);
			if(makeMeanStats) {
				double statMean = (GrayMatrix.WHITE - stat.mean)/GrayMatrix.WHITE;
				double statStdev = stat.stdev / GrayMatrix.WHITE;
				double statCropMean = (GrayMatrix.WHITE - statCrop.mean)/GrayMatrix.WHITE;
				double statCropStdev = statCrop.stdev / GrayMatrix.WHITE;
				Double value = countValues.get(key);
				Double valueCrop = countValuesCrop.get(key);
				sb.append(key + " " + statMean + " " + statStdev + " " + statCropMean + " " + statCropStdev
						+ " " + (value) + " " + (valueCrop) + " " +  "\n");
			} else {
				Double value = countValues.get(key);
				Double valueCrop = countValuesCrop.get(key);
				sb.append(key + " " + (value) + " " + (valueCrop) + " " +  "\n");
			}
			//sb.append(key + " " + value + " " + valueCrop + " " + " " + (valueCrop-value) + " " + (1.0-valueCrop/value) + " " +  "\n");
		}
		FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath() + "/_results", FILE + getStatsNameSuffix());
	}
	
	private String getStatsNameSuffix() {
		String result = "";
		if(widgetInsteadOfThreshold) {
			result += "_widget";
		} else {
			result += "_threshold";
		}
		if(excludeBorders) {
			result += "_borders";
		}
		return result;
	}

}
