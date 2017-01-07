package dashapp.core.eval.analysis.heatmap;

import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.image.GrayMatrix.PixelCalculator;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;

public class ThresholdAnalysis extends AbstractAnalysis implements PixelCalculator {
	
	public static final String LABEL = "Threshold Analysis";
	public static final String FILE = "_threshold";
	
	private int actDashboardsCount;
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		this.actDashboardsCount = actDashboards.length;
		int[][] outputMatrix = actDashboards.printDashboards(null);
		GrayMatrix.update(outputMatrix, this, false);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, outputMatrix);
		FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE);
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder) {
		// do nothing
	}

	@Override
	public int calculateValue(int value) {
		double probabilty = (double) value/actDashboardsCount;
		probabilty = probabilty > 0.8 ? 1.0 : 0.0;
		return GrayMatrix.toGray(probabilty);
	}
}
