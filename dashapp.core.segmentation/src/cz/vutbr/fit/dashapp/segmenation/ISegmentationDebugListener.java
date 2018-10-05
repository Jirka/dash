package cz.vutbr.fit.dashapp.segmenation;

import java.awt.image.BufferedImage;

/**
 * Debug listener which provide actual image and histogram during performing of segmentation algorithm.  
 * 
 * @author Jiri Hynek
 *
 */
public interface ISegmentationDebugListener {
	
	void debugImage(String label, BufferedImage image, ISegmentationAlgorithm segmentationAlgorithm);
	
	void debugHistogram(String label, int[] histogram, ISegmentationAlgorithm segmentationAlgorithm);

}
