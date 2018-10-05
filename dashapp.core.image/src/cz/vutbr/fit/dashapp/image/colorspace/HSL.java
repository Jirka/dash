package cz.vutbr.fit.dashapp.image.colorspace;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;
import extern.HSLColor;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class HSL implements ColorSpace {
	
	public static final int CHANNEL_HUE = 0;
	public static final int CHANNEL_SATURATION = 1;
	public static final int CHANNEL_LIGHTNESS = 2;

	public float h;
	public float s;
	public float l;

	public HSL(int rgb) {
		float[] hsbvals = HSLColor.fromRGB(ColorMatrix.getRed(rgb), ColorMatrix.getGreen(rgb), ColorMatrix.getBlue(rgb));
		this.h = hsbvals[0]/360;
		this.s = hsbvals[1]/100;
		this.l = hsbvals[2]/100;
	}

	@Override
	public Object getColorChannel(int colorChannel) {
		switch (colorChannel) {
		case CHANNEL_HUE:
			return new Double(h);
		case CHANNEL_SATURATION:
			return new Double(s);
		case CHANNEL_LIGHTNESS:
			return new Double(l);
		default:
			return -1;
		}
	}
	
	public int toRGB()
	{
		return HSLColor.toRGB(s, l, h).getRGB();
	}
	
	public static HSL[][] fromRGB(int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		HSL[][] matrixHSL = new HSL[mW][mH];
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				matrixHSL[i][j] = new HSL(matrix[i][j]);
			}
		}
		return matrixHSL;
	}
}