package cz.vutbr.fit.dashapp.image.colorspace;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.GrayMatrix;

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
		if(matrix.length > 0) {
			Gray[][] matrixGray = new Gray[matrix.length][matrix[0].length];
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					matrixGray[i][j] = new Gray(matrix[i][j]);
				}
			}
			return matrixGray;
		}
		return null;
	}
}