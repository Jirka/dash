package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.segmenation.XYCut4;
import cz.vutbr.fit.dashapp.segmenation.XYCut4.DebugMode;

public class SegmentationAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Segmentation Analysis";
	public static final String NAME = "segmentation";
	public static final String FILE = "_" + NAME;
	
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME;

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		List<DashboardFile> dashboardCandidates = actWorkspaceFolder.getChildren(
				DashboardFile.class, actWorkspaceFolder.getFileName(), false
		);
		if(dashboardCandidates != null && dashboardCandidates.size() == 1) {
			BufferedImage image = dashboardCandidates.get(0).getImage();
			XYCut4 xyCut4 = new XYCut4(DebugMode.SILENT);
			xyCut4.processImage(image);
			Map<String, BufferedImage> debugImages = xyCut4.getDebugImages();
			for (Map.Entry<String, BufferedImage> debugImage : debugImages.entrySet()) {
				printImage(actWorkspaceFolder, debugImage.getValue(), actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + debugImage.getKey(), actWorkspaceFolder.getFileName());
			}
			debugImages.clear();
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
	}

}
