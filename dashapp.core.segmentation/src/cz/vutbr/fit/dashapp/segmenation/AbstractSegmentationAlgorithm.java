package cz.vutbr.fit.dashapp.segmenation;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

import extern.ImagePreview;

public abstract class AbstractSegmentationAlgorithm implements ISegmentationAlgorithm {	

	// ----------------- internal for debugging
	
	private DebugMode debugMode;
	private Map<String, BufferedImage> debugMatrices;
	
	public static enum DebugMode {
		NONE,
		SILENT,
		INTERACTIVE
	}
	
	public AbstractSegmentationAlgorithm() {
		this(DebugMode.INTERACTIVE);
	}
	
	public AbstractSegmentationAlgorithm(DebugMode debugMode) {
		this.debugMode = debugMode;
		if(debugMode == DebugMode.SILENT) {
			debugMatrices = new LinkedHashMap<>();
		}
	}
	
	protected void debug(String name, BufferedImage image) {
		if(debugMode == DebugMode.INTERACTIVE) {
			new ImagePreview(image, name).openWindow(800,600,0.8);
		} else if(debugMode == DebugMode.SILENT) {
			debugMatrices.put(name, image);
		}
	}
	
	public Map<String, BufferedImage> getDebugImages() {
		return debugMatrices;
	}
	
	// ----------------------------------------------------------------------

}
