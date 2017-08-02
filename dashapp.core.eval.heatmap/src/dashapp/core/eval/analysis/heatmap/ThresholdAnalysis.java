package dashapp.core.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.List;

import cz.vutbr.fit.dashapp.eval.analysis.AbstractAnalysis;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.DashboardCollection;
import cz.vutbr.fit.dashapp.util.FileUtils;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix.ThresholdNormalization;

public class ThresholdAnalysis extends AbstractAnalysis {
	
	public static final String LABEL = "Threshold Analysis";
	public static final String FILE = "_threshold";
	
	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder, DashboardCollection actDashboards) {
		int actDashboardsCount = actDashboards.length;
		int[][] outputMatrix = actDashboards.printDashboards(null);
		GrayMatrix.update(outputMatrix, new ThresholdNormalization(0.8, actDashboardsCount), false);
		BufferedImage image = GrayMatrix.printMatrixToImage(null, outputMatrix);
		FileUtils.saveImage(image, actWorkspaceFolder.getPath(), FILE);
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		// do nothing
	}
}
