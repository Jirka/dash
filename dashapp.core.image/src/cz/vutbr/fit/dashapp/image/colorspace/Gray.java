package cz.vutbr.fit.dashapp.image.colorspace;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class Gray implements ColorSpace {

	public int grayValue;

	public static final int CHANNEL_GRAY = 0;

	public Gray(int rgb) {
		this.grayValue = ColorMatrix.toGrayScaleValue(rgb);
	}

	@Override
	public Object getColorChannel(int colorChannel) {
		switch (colorChannel) {
			case CHANNEL_GRAY:
				return new Double(grayValue);
			default:
				return -1;
		}
	}

	@Override
	public int toRGB() {
		return GrayMatrix.getRGB(grayValue);
	}
	
	public static Gray[][] fromRGB(int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		Gray[][] matrixGray = new Gray[mW][mH];
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				matrixGray[i][j] = new Gray(matrix[i][j]);
			}
		}
		return matrixGray;
	}
}