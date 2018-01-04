package cz.vutbr.fit.dashapp.image.colorspace;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

public class RGB implements ColorSpace {
	
	public static final int CHANNEL_R = 0;
	public static final int CHANNEL_G = 1;
	public static final int CHANNEL_B = 2;
	
	public int r;
	public int g;
	public int b;

	public RGB(int rgb) {
		this.r = ColorMatrix.getRed(rgb);
		this.g = ColorMatrix.getGreen(rgb);
		this.b = ColorMatrix.getBlue(rgb);
	}

	@Override
	public Object getColorChannel(int colorChannel) {
		switch (colorChannel) {
		case CHANNEL_R:
			return r;
		case CHANNEL_G:
			return g;
		case CHANNEL_B:
			return b;
		default:
			return -1;
		}
	}

	@Override
	public int toRGB() {
		return ColorMatrix.getRGB(r, g, b);
	}
	
	public static RGB[][] fromRGB(int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		RGB[][] matrixRGB = new RGB[mW][mH];
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				matrixRGB[i][j] = new RGB(matrix[i][j]);
			}
		}
		return matrixRGB;
	}

}
