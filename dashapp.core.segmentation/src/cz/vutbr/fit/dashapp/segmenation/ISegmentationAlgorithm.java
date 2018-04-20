package cz.vutbr.fit.dashapp.segmenation;

import java.awt.image.BufferedImage;
import java.util.Map;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.segmenation.AbstractSegmentationAlgorithm.DebugMode;

public interface ISegmentationAlgorithm {
	
	Dashboard processImage(BufferedImage image);
	
	String getName();

	void setDebugMode(DebugMode debugMode);

	Map<String, BufferedImage> getDebugImages();

}
