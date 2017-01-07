package dashapp.core.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.image.GrayMatrix.EntrophyCalculator;
import cz.vutbr.fit.dashapp.image.GrayMatrix.PixelCalculator;
import cz.vutbr.fit.dashapp.image.MathUtils;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;
	
public class EntrophyAnalysis extends AbstractAnalysis implements PixelCalculator {
	
	private static final String LABEL = "Entrophy Analysis";
	private static final String FILE = "_enthropy";
	
	Map<String, Integer> meanValues;
	
	int actDashboardsCount;

	public EntrophyAnalysis() {
		meanValues = new LinkedHashMap<>();
	}
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		this.actDashboardsCount = actDashboards.length;
		int[][] heatMap = actDashboards.printDashboards(null);
		GrayMatrix.update(heatMap, new EntrophyCalculator(actDashboardsCount), false);
		int meanValue = GrayMatrix.meanValue(heatMap);
		meanValues.put(actWorkspaceFolder.getFileName(), meanValue);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, heatMap);
		FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE);
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder) {
		// do nothing
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Integer> entry : meanValues.entrySet()) {
			sb.append(entry.getKey() + " " + entry.getValue() + "\n");
		}
		FileUtils.saveTextFile(sb.toString(), actWorkspaceFolder.getPath(), "mean_entrophies");
	}

	@Override
	public int calculateValue(int value) {
		double probabilty = (double) value/this.actDashboardsCount;
		return GrayMatrix.toGray(MathUtils.entrophy(probabilty));
	}
}
