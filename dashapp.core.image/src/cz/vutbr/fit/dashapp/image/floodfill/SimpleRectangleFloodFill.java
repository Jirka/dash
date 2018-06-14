package cz.vutbr.fit.dashapp.image.floodfill;

import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class SimpleRectangleFloodFill extends BasicFloodFill {

	public SimpleRectangleFloodFill(int[][] matrix, boolean createNew, int refColor) {
		super(matrix, createNew, refColor);
	}

	@Override
	protected void postProcessMatrix(int[][] resultMatrix) {
		super.postProcessMatrix(resultMatrix);
		// convert colors to black
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				if (resultMatrix[i][j] < 0) {
					resultMatrix[i][j] = GrayMatrix.BLACK;
				}
			}
		}
	}

	@Override
	protected void postProcessSeedPixel(int x1, int y1, int x2, int y2, int markColor) {
		super.postProcessSeedPixel(x1, y1, x2, y2, markColor);
		// test rectangle size
		if (x2 - x1 > mW / 2 && y2 - y1 > mH / 2) {
			// delete area
			for (int x = 0; x < mW; x++) {
				for (int y = 0; y < mH; y++) {
					if (matrix[x][y] == markColor) {
						matrix[x][y] = GrayMatrix.WHITE;
					}
				}
			}
		} else {
			// create rectangle
			MatrixUtils.drawPixels(matrix, x1, y1, x2, y2, markColor);
		}
	}

}