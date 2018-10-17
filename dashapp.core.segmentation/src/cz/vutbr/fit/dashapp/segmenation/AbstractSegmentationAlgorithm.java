package cz.vutbr.fit.dashapp.segmenation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.dashapp.image.util.HistogramUtils;

/**
 * This class provides implementation of basic methods of segmentation algorithm.
 * 
 * @author Jiri Hynek
 *
 */
public abstract class AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {	

	// ----------------- internal for debugging
	private List<ISegmentationDebugListener> debugListeners;
	
	public AbstractSegmentationAlgorithm() {
		debugListeners = new ArrayList<>();
	}
	
	@Override
	public void addSegmentationDebugListener(ISegmentationDebugListener debugListener) {
		debugListeners.add(debugListener);
	}
	
	@Override
	public void removeSegmentationDebugListener(ISegmentationDebugListener debugListener) {
		debugListeners.remove(debugListener);
	}
	
	@Override
	public List<ISegmentationDebugListener> getDebugListeners() {
		return debugListeners;
	}
	
	protected void debugImage(String label, BufferedImage image) {
		for (ISegmentationDebugListener debugListener : debugListeners) {
			debugListener.debugImage(label, image, this);
		}
	}
	
	protected void debugHistogram(String label, int[][] matrix) {
		int[] histogram = HistogramUtils.getGrayscaleHistogram(matrix);
		for (ISegmentationDebugListener debugListener : debugListeners) {
			debugListener.debugHistogram(label, histogram, this);
		}
	}
	
	// ----------------------------------------------------------------------

}
