package cz.vutbr.fit.dashapp.segmenation;

import java.awt.image.BufferedImage;
import java.util.List;

import cz.vutbr.fit.dashapp.model.Dashboard;

/**
 * Segmentation algorithm methods.
 * 
 * @author Jiri Hynek
 *
 */
public interface ISegmentationAlgorithm {
	
	/**
	 * Performs segmentation algorithm.
	 * 
	 * @param image
	 * @return dashboard
	 */
	Dashboard processImage(BufferedImage image);
	
	/**
	 * 
	 * @return name of segmentation algorithm
	 */
	String getName();
	
	/**
	 * Register debug listener (for debug purposes).
	 * 
	 * @param debugListener
	 */
	void addSegmentationDebugListener(ISegmentationDebugListener debugListener);
	
	/**
	 * Unregister debug listener (for debug purposes).
	 * 
	 * @param segmentationAlgorithmTask
	 */
	void removeSegmentationDebugListener(ISegmentationDebugListener debugListener);

	/**
	 * 
	 * @return list of debug listeners (for debug purposes)
	 */
	List<ISegmentationDebugListener> getDebugListeners();

}
