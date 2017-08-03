package cz.vutbr.fit.dashapp.segmenation;

import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.model.Dashboard;

public interface ISegmentationAlgorithm {
	
	Dashboard processImage(BufferedImage image);
	
	String getName();

}
