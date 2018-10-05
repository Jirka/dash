package cz.vutbr.fit.dashapp.segmenation.util.image;

import java.util.Vector;

import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughTransform;
import ac.essex.ooechs.imaging.commons.edge.hough.HoughLine.Orientation;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class HoughLineUtil {
	
	/**
	 * Method finds lines using hough transformation.
	 * 
	 * @param matrix
	 * @param scaleW
	 * @param scaleH
	 * @return
	 */
	public static int[][] process(int[][] matrix, double scaleW, double scaleH) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		
		int[][] lineMatrix = GrayMatrix.newMatrix(mW, mH, GrayMatrix.WHITE); // debug
		//int[][] edgesMatrix = GrayMatrix.inverse(GrayMatrix.edges(matrix), false);
		
		HoughTransform t = new HoughTransform(mW, mH);
		t.addPoints(matrix);
		Vector<HoughLine> lines = t.getLines((int) (mW*scaleW));
		for (HoughLine line : lines) {
			Orientation orientation = line.getOrientation();
			if(orientation == Orientation.H) {
				line.draw(lineMatrix, GrayMatrix.BLACK, true);
			}
		}
		lines = t.getLines((int) (mH*scaleH));
		for (HoughLine line : lines) {
			Orientation orientation = line.getOrientation();
			if(orientation == Orientation.V) {
				line.draw(lineMatrix, GrayMatrix.BLACK, true);
			}
		}
		
		return lineMatrix;
	}

}
