package cz.vutbr.fit.dashapp.image.colorspace;

import cz.vutbr.fit.dashapp.util.matrix.ColorMatrix;
import extern.HSLColor;

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
		if(matrix.length > 0) {
			HSL[][] matrixHSL = new HSL[matrix.length][matrix[0].length];
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					matrixHSL[i][j] = new HSL(matrix[i][j]);
				}
			}
			return matrixHSL;
		}
		return null;
	}
}