package cz.vutbr.fit.dashapp.eval.analysis.heatmap;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.DashboardFile;
import cz.vutbr.fit.dashapp.model.GraphicalElement.GEType;
import cz.vutbr.fit.dashapp.model.WorkspaceFolder;
import cz.vutbr.fit.dashapp.util.matrix.BooleanMatrix;
import cz.vutbr.fit.dashapp.segmenation.methods.DashboardSegmentation;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationAlgorithm;
import cz.vutbr.fit.dashapp.segmenation.ISegmentationDebugListener;
import cz.vutbr.fit.dashapp.segmenation.SegmentationType;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SegmentationAnalysis extends AbstractHeatMapAnalysis {
	
	public static final String LABEL = "Segmentation Analysis";
	public static final String NAME = "segmentation";
	public static final String FILE = "_" + NAME;
	public static final String RECTANGLES_SUFFIX = "-r";
	
	public static final SegmentationType[] DEFAULT_SEGMENTATIONS = new SegmentationType[] { 
			SegmentationType.DashboardSegmentation,
	};
	
	private WorkspaceFolder actWorkspaceFolder;
	public boolean enable_act_folder_output = true;
	public boolean enable_all_folder_output = true;
	public boolean enable_debug_output = false;
	public String outputFolderPath = DEFAULT_OUTPUT_PATH + NAME;
	public String outputFile = FILE;
	public boolean enable_basic_input = true;
	public boolean enable_basic_body_input = false;
	public List<SegmentationType> segmentationTypes = Arrays.asList(DEFAULT_SEGMENTATIONS);
	public boolean enable_custom_segmentations = true;
	private ISegmentationAlgorithm[] segmentations = null;

	@Override
	public String getLabel() {
		return LABEL;
	}

	@Override
	public void init() {
		segmentations = null;
	}
	
	private ISegmentationAlgorithm[] getSegmentations() {
		if(segmentations == null) {
			if(enable_custom_segmentations) {
				segmentations = new ISegmentationAlgorithm[segmentationTypes.size()];
				int i = 0;
				for (SegmentationType segmentationType : segmentationTypes) {
					try {
						segmentations[i] = (ISegmentationAlgorithm) segmentationType.createAlgorithm();
					} catch (Exception e) {
						System.err.println("Unable to create segmentation algorithm: " + segmentationType);
					}
					i++;
				}
			} else {
				// specify own metrics if required
				segmentations = new ISegmentationAlgorithm[] {
						new DashboardSegmentation()
				};
			}
			
			if(enable_debug_output) {
				// register listener
				AnalysisSegmentationDebugListener debugListener = new AnalysisSegmentationDebugListener();
				for (ISegmentationAlgorithm segmentationAlgorithm : segmentations) {
					segmentationAlgorithm.addSegmentationDebugListener(debugListener);
				}
			}
		}
		return segmentations;
	}

	@Override
	public void processFolder(WorkspaceFolder actWorkspaceFolder) {
		if(enable_act_folder_output || enable_all_folder_output) {
			if(enable_basic_input) {
				processFolder(actWorkspaceFolder, FILE_SUFFIX_BASIC);
			}
			
			if(enable_basic_body_input) {
				processFolder(actWorkspaceFolder, FILE_SUFFIX_BASIC);
			}
		}
	}
	
	public void processFolder(WorkspaceFolder actWorkspaceFolder, String suffix) {
		this.actWorkspaceFolder = actWorkspaceFolder;
		List<DashboardFile> dashboardCandidates = actWorkspaceFolder.getChildren(
				DashboardFile.class, actWorkspaceFolder.getFileName() + suffix, false
		);
		if(dashboardCandidates != null && dashboardCandidates.size() == 1) {
			BufferedImage image = dashboardCandidates.get(0).getImage();
			if(image != null) {
				ISegmentationAlgorithm[] segmentationAlgorithms = getSegmentations();
				String algName;
				for (ISegmentationAlgorithm segmentationAlgorithm : segmentationAlgorithms) {
					algName = segmentationAlgorithm.getClass().getSimpleName();
					Dashboard dashboard = segmentationAlgorithm.processImage(image);
					
					if(dashboard != null) {
						BufferedImage rectanglesImage = BooleanMatrix.printMatrixToImage(null, BooleanMatrix.printDashboard(dashboard, true, GEType.ALL_TYPES));
						if(enable_act_folder_output) {
							printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath(), outputFile + "_" + algName);
							printImage(actWorkspaceFolder, rectanglesImage, actWorkspaceFolder.getPath(), outputFile + "_" + algName + RECTANGLES_SUFFIX);
							printDashboard(actWorkspaceFolder, dashboard, actWorkspaceFolder.getPath(), outputFile + "_" + algName);
						}
						if(enable_all_folder_output) {
							printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + algName, actWorkspaceFolder.getFileName());
							printImage(actWorkspaceFolder, rectanglesImage, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + algName, actWorkspaceFolder.getFileName() + RECTANGLES_SUFFIX);
							printDashboard(actWorkspaceFolder, dashboard, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + algName, actWorkspaceFolder.getFileName());
						}
					}
				}
			}
		}
	}

	@Override
	public void sumarizeFolders(WorkspaceFolder actWorkspaceFolder, List<WorkspaceFolder> analyzedFolders) {
		
	}
	
	private class AnalysisSegmentationDebugListener implements ISegmentationDebugListener {

		@Override
		public void debugImage(String label, BufferedImage image, ISegmentationAlgorithm segmentationAlgorithm) {
			if(enable_debug_output) {
				String algName = segmentationAlgorithm.getClass().getSimpleName();
				if(enable_act_folder_output) {
					printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath(), outputFile + "_" + algName + "_" + label);
				}
				if(enable_all_folder_output) {
					printImage(actWorkspaceFolder, image, actWorkspaceFolder.getPath() + "/../" + outputFolderPath + "/" + algName + "/" + label, actWorkspaceFolder.getFileName());
				}
			}
		}

		@Override
		public void debugHistogram(String label, int[] histogram, ISegmentationAlgorithm segmentationAlgorithm) {
			// do nothing
			// TODO print histogram to image
		}
		
	}

}
