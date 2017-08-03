package cz.vutbr.fit.dashapp.segmenation;

import java.awt.image.BufferedImage;

import cz.vutbr.fit.dashapp.model.Dashboard;
import cz.vutbr.fit.dashapp.model.GraphicalElement;
import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import extern.ImagePreview;

public class SegmentationAlgorithm1 implements ISegmentationAlgorithm {
	
	public static final String NAME = "Algorithm 1";

	@Override
	public Dashboard processImage(BufferedImage image) {
		// convert buffered image to 2D array
		int[][] matrix = ColorMatrix.printImageToMatrix(image);
		
		final int w = image.getWidth();
		final int h = image.getHeight();
		
		// now we can do some stuff with image
		ColorMatrix.toGrayScale(matrix, false, false); // convert to gray scale
		// update displayed image
		// only for debug purposes (to see changes of 2D array)
		// should not be in core
		new ImagePreview(ColorMatrix.printMatrixToImage(null, matrix), "result 1").openWindow();;
		//ColorMatrix.printMatrixToImage(image, matrix);
		
		// create dashboard (represents graphical regions)
		Dashboard dashboard = new Dashboard();
		dashboard.setDimension(0, 0, w, h);
		
		// example of detected regions
		// we put them into a dashboard
		dashboard.addChildGE(new GraphicalElement(dashboard, 10, 10, 50, 60));
		dashboard.addChildGE(new GraphicalElement(dashboard, 100, 100, 50, 60));
		
		// TODO ...
		// algoritm segmentation algorithm which creates graphical elements to appropriate locations in dashboard
		
		// return final dashboard
		// can be null (when something goes wrong)
		return dashboard;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
