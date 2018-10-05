package cz.vutbr.fit.dashapp.image.colorspace;

import java.awt.Color;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import cz.vutbr.fit.dashapp.util.matrix.MatrixUtils;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class HSB implements ColorSpace {
	
	public static final int CHANNEL_HUE = 0;
	public static final int CHANNEL_SATURATION = 1;
	public static final int CHANNEL_BRIGHTNESS = 2;
	
	public float h;
	public float s;
	public float b;

	public HSB(int rgb) {
		float[] hsbvals = new float[3];
		Color.RGBtoHSB(ColorMatrix.getRed(rgb), ColorMatrix.getGreen(rgb), ColorMatrix.getBlue(rgb), hsbvals);
		this.h = hsbvals[0];
		this.s = hsbvals[1];
		this.b = hsbvals[2];
	}

	@Override
	public Object getColorChannel(int colorChannel) {
		switch (colorChannel) {
		case CHANNEL_HUE:
			return new Double(h);
		case CHANNEL_SATURATION:
			return new Double(s);
		case CHANNEL_BRIGHTNESS:
			return new Double(b);
		default:
			return -1;
		}
	}

	@Override
	public int toRGB() {
		return Color.HSBtoRGB(h, s, b);
	}
	
	public static HSB[][] fromRGB(int[][] matrix) {
		int mW = MatrixUtils.width(matrix);
		int mH = MatrixUtils.height(matrix);
		HSB[][] matrixHSB = new HSB[mW][mH];
		for (int i = 0; i < mW; i++) {
			for (int j = 0; j < mH; j++) {
				matrixHSB[i][j] = new HSB(matrix[i][j]);
			}
		}
		return matrixHSB;
	}
}