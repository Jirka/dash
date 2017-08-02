package dashapp.core.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils;
import cz.vutbr.fit.dashapp.util.matrix.StatsUtils.MeanSatistics;

public class HeatMapAnalysis extends AbstractAnalysis {
	
	private static final String LABEL = "Heatmap Analysis";
	private static final String FILE = "_heatmap";
	
	Map<String, MeanSatistics> meanValues;
	
	public HeatMapAnalysis() {
		meanValues = new LinkedHashMap<>();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		int[][] heatMap = actDashboards.printDashboards(null);
		GrayMatrix.normalize(heatMap, actDashboards.length, false);
		MeanSatistics meanValue = StatsUtils.meanStatistics(heatMap);
		System.out.println((int) meanValue.mean);
		meanValues.put(actWorkspaceFolder.getFileName(), meanValue);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, heatMap);
		//FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE);
		FileUtils.saveImage(image, actWorkspaceFolder.getPath() + "/../_000-sum", actWorkspaceFolder.getFileName() + FILE);
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		// do nothing
		StringBuffer sb = new StringBuffer();
		for (Entry<String, MeanSatistics> entry : meanValues.entrySet()) {
			sb.append(entry.getKey() + " " + (int) entry.getValue().mean + " " + (int) entry.getValue().stdev
					 + " " + entry.getValue().min + " " + entry.getValue().max+ "\n");
		}
		FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath(), "mean_heats");
	}

}
