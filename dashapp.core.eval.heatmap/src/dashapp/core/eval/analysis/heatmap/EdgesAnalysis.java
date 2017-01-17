package dashapp.core.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.List;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.image.GrayMatrix;
import cz.vutbr.fit.dashapp.image.GrayMatrix.PixelCalculator;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;

public class EdgesAnalysis extends AbstractAnalysis implements PixelCalculator {
	
	private static final String LABEL = "Edge Detection";
	private static final String FILE = "_edges";
	
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
		int[][] edgesMatrix = GrayMatrix.edges(outputMatrix);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, edgesMatrix);
		FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE);
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		// do nothing
	}

	@Override
	public int calculateValue(int value) {
		double probabilty = (double) value/this.actDashboardsCount;
		probabilty = probabilty > 0.8 ? 1.0 : 0.0;
		return GrayMatrix.toGray(probabilty);
	}
}
