package dashapp.core.eval.analysis.heatmap;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.image.GrayMatrix.EntrophyNormalization;
import cz.vutbr.fit.dashapp.image.GrayMatrix.PixelCalculator;
import cz.vutbr.fit.dashapp.image.MathUtils;
import cz.vutbr.fit.dashapp.image.MathUtils.MeanSatistics;
import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashAppUtils;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;
	
public class EntrophyAnalysis extends AbstractAnalysis implements PixelCalculator {
	
	private static final String LABEL = "Entrophy Analysis";
	private static final String FILE = "_enthropy";
	
	Map<String, MeanSatistics> meanValues;
	Map<String, MeanSatistics> meanValuesCrop;
	
	int actDashboardsCount;

	public EntrophyAnalysis() {
		meanValues = new LinkedHashMap<>();
		meanValuesCrop = new LinkedHashMap<>();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		this.actDashboardsCount = actDashboards.length;
		int[][] heatMap = actDashboards.printDashboards(null);
		GrayMatrix.update(heatMap, new EntrophyNormalization(actDashboardsCount), false);
		MeanSatistics meanValue = GrayMatrix.meanStatistics(heatMap);
		meanValues.put(actWorkspaceFolder.getFileName(), meanValue);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, heatMap);
		FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE);
		
		DashboardCollection cropDashboard = DashAppUtils.makeDashboardCollection(actWorkspaceFolder.getChildren(DashboardFile.class, actWorkspaceFolder.getFileName() + "-crop", false));
		if(cropDashboard.length == 1) {
			Dashboard dashboard = cropDashboard.dashboards[0];
			Rectangle cropRectangle = new Rectangle(dashboard.x, dashboard.y, dashboard.width, dashboard.height);
			heatMap = GrayMatrix.cropMatrix(heatMap, cropRectangle);
			//meanValuesCrop.put(actWorkspaceFolder.getFileName(), GrayMatrix.meanStatistics(cmpMap));
			meanValue = GrayMatrix.meanStatistics(heatMap);
			meanValuesCrop.put(actWorkspaceFolder.getFileName(), meanValue);
			image = GrayMatrix.printMatrixToImage(null, heatMap);
			FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE + "_x");
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		// do nothing
		StringBuffer sb = new StringBuffer();
		/*for (Entry<String, MeanSatistics> entry : meanValues.entrySet()) {
			sb.append(entry.getKey() + " " + (GrayMatrix.WHITE - ((int) entry.getValue().mean)) + " " + (int) entry.getValue().stdev + "\n");
		}*/
		for (WorkspaceFolder workspaceFolder : analyzedFolders) {
			String key = workspaceFolder.getFileName();
			 MeanSatistics entry = meanValues.get(key);
			 MeanSatistics entryCrop = meanValuesCrop.get(key);
			sb.append(workspaceFolder.getFileName() + " " + (GrayMatrix.WHITE - ((int) entry.mean)) + " " + (int) entry.stdev + "\n");
			sb.append(workspaceFolder.getFileName() + " " + (GrayMatrix.WHITE - ((int) entryCrop.mean)) + " " + (int) entryCrop.stdev + "\n");
			sb.append((GrayMatrix.WHITE - ((int) entryCrop.mean))-(GrayMatrix.WHITE - ((int) entry.mean)) + "\n");
			sb.append("-----------------\n");
		}
		FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath(), "mean_entrophies");
	}

	@Override
	public int calculateValue(int value) {
		double probabilty = (double) value/this.actDashboardsCount;
		return GrayMatrix.toGray(MathUtils.entrophy(probabilty));
	}
}
