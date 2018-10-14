package cz.vutbr.fit.dashapp.view.config;

import cz.vutbr.fit.dashapp.segmenation.methods.Experimental1;
import cz.vutbr.fit.dashapp.segmenation.methods.Experimental2;
import cz.vutbr.fit.dashapp.segmenation.methods.Experimental4;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.BottomUp;
import cz.vutbr.fit.dashapp.segmenation.thesis.mejia.BottomUpRefactorized;
import cz.vutbr.fit.dashapp.view.action.image.ImageActionFactory;
import cz.vutbr.fit.dashapp.view.action.image.segmentation.SegmentationImageActionFactory;
import cz.vutbr.fit.dashapp.view.action.segmentatiion.DashboardSegmentationUI;
import cz.vutbr.fit.dashapp.view.action.segmentatiion.SegmentationAlgorithmUI;
import cz.vutbr.fit.dashapp.view.tools.ImageTool;
import cz.vutbr.fit.dashapp.view.tools.SegmentationTool;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class DashAppViewConfiguration extends BasicViewConfiguration {
	
	protected String[] getDebugWorkspacePathSuffixes() {
		return new String[] {
				"/research/widget-based/gen/all/segmentation/color",
				"/widget-based/gen/all/segmentation/color",
				"/research/widget-based/gen/all/color",
				"/widget-based/gen/all/color",
		};
	}
	
	/**
	 * version
	 */
	public static final String VERSION = "rel-segmentation";
	
	@Override
	public String getVersion() {
		return VERSION;
	}
	
	protected void initTools() {
		super.initTools();
		
		guiTools.add(new ImageTool(true, ImageActionFactory.getRecommendedActions()));
		guiTools.add(new ImageTool(true, SegmentationImageActionFactory.getRecommendedActions()));
		
		// segmentation algorithm tool
		SegmentationAlgorithmUI[] segmentationUIs = new SegmentationAlgorithmUI[] {
				new DashboardSegmentationUI(),
				new SegmentationAlgorithmUI(new Experimental1()),
				new SegmentationAlgorithmUI(new Experimental2()),
				new SegmentationAlgorithmUI(new Experimental4()),
				new SegmentationAlgorithmUI(new BottomUp()),
				new SegmentationAlgorithmUI(new BottomUpRefactorized()),
		};
		guiTools.add(new SegmentationTool(true, segmentationUIs));
	}
	

}
